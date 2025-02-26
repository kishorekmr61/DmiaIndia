package com.dmia.bioattendance.ui.userAttendance

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmia.bioattendance.ui.userDashBoard.UserDashboardActivity
import com.dmia.bioattendance.R
import com.dmia.bioattendance.data.constant.ApiConstant
import com.dmia.bioattendance.data.helpers.AppPreference
import com.dmia.bioattendance.databinding.ActivityUserAttendanceBinding
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.model.CommonModelResponse
import com.dmia.bioattendance.model.UsersData
import com.dmia.bioattendance.utils.helper.LocationHelper
import com.dmia.bioattendance.utils.locationPermissionSettingPopup
import com.dmia.bioattendance.utils.showDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class UserAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserAttendanceBinding
    private val vm: UserAttendanceViewModel by viewModels()
    private val REQUEST_CODE = 101

    private var isSettingsDialogOpened = false

    var employeeid = ""
    var imei = ""
    var isregistered = false

    private var permissionAskingCount = 0

    @Inject
    lateinit var location: LocationHelper

    private var userLocation = Location("user")
    private lateinit var biometricManager: BiometricManager
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var enrollBiometricRequestLauncher: ActivityResultLauncher<Intent>

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                updateLocation()
            } else {
                if (permissionAskingCount > 1) {
                    locationPermissionSettingPopup(

                        positiveCallback = {
                            isSettingsDialogOpened = false
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }

                    )
                } else {
                    requestPermissions()
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = vm
        setObserver()
        biometricManager = BiometricManager.from(this)
        biometricManager = BiometricManager.from(this)

        // Initialize BiometricPrompt to setup success & error callbacks of biometric prompt
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(
                this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        showSnackBar("Authentication error: Code: $errorCode ($errString)")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        showSnackBar("Failed to authenticate. Please try again.")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val type = result.authenticationType
                        showSnackBar("\uD83C\uDF89 Authentication successful! Type: $type \uD83C\uDF89")
                    }
                },
            )

        // Initialize PromptInfo to set title, subtitle, and authenticators of the biometric prompt
        try {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Example biometric authentication")
                .setSubtitle("Please authenticate yourself first.")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
                .build()
        } catch (e: Exception) {
            showSnackBar(e.message ?: "Unable to initialize PromptInfo")
        }

        // Initialize a launcher for requesting user to enroll in biometric
        enrollBiometricRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    tryAuthenticateBiometric()
                } else {
                    showSnackBar("Failed to enroll in biometric")
                }
            }


//        configuretoolbar()
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // if permissions are not provided we are requesting for permissions.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE
            )
        }
        if (AppPreference.read(ApiConstant.EMPLOYEEID, "")?.isEmpty() == true) {
            getEmployeeid()
        } else {
            var employeedetails = Gson().fromJson(
                AppPreference.read(ApiConstant.EMPLOYEEDETAILS, ""), UsersData::class.java
            )
            binding.employeeEdt?.setText(
                employeedetails.Name.plus("(").plus(employeedetails.ID).plus(")")
            )
            employeeid = AppPreference.read(ApiConstant.EMPLOYEEID, "") ?: ""
        }
        imei = Settings.Secure.getString(
            applicationContext.contentResolver, Settings.Secure.ANDROID_ID
        );

        val intent: Intent = intent
        if (intent.hasExtra("Login")) {
            isregistered = true
        }

        binding.biometricImg?.setOnClickListener { v: View? ->
//            FingerPrintUtils.openSecuritySettings(
//                this@UserAttendanceActivity
//            )
            checkDeviceCapability {
                showBiometricPrompt()
            }
        }
//        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this)
    }

    private fun tryAuthenticateBiometric() {
        checkDeviceCapability {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun setObserver() {
        observeLocationUpdates()
        vm.attendanceResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->

                        if (response.data != null) {
                            showDialog(
                                this@UserAttendanceActivity,
                                resposnes?.Description,
                                resposnes?.EmpName
                            ) { dialog, which ->
                                dialog.dismiss()
                                val intent = Intent(
                                    this@UserAttendanceActivity, UserDashboardActivity::class.java
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        } else {
                            parseErrorData(resposnes)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@UserAttendanceActivity, getString(R.string.error), response.message
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
        vm.userRegistrationResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (response.data != null) {
                            showDialog(
                                this@UserAttendanceActivity,
                                resposnes?.Description,
                                response.data?.EmpName
                            ) { dialog, which ->
                                dialog.dismiss()
                                val intent = Intent(
                                    this@UserAttendanceActivity, UserDashboardActivity::class.java
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        } else {
                            showDialog(
                                this@UserAttendanceActivity,
                                getString(R.string.failure),
                                getString(R.string.empid_not_found_in_the_company_employee_list)
                            ) { dialog, which -> dialog.dismiss() }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@UserAttendanceActivity,
                        getString(R.string.failure),
                        getString(R.string.empid_not_found_in_the_company_employee_list)
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun parseErrorData(resposnes: CommonModelResponse?) {
        showDialog(
            this@UserAttendanceActivity, resposnes?.Description, resposnes?.EmpName
        ) { dialog, which -> dialog.dismiss() }
    }


    override fun onResume() {
        super.onResume()
        binding.goToSettingsBtn?.visibility = View.GONE
        binding.authMessageTv?.visibility = View.VISIBLE
        binding.authMessageTv?.text = getString(R.string.place_your_finger_on_scanner_to_scan)
    }


    override fun onBackPressed() {
        val intent = Intent(applicationContext, UserDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(applicationContext, UserDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getEmployeeid() {
        val myDialog = Dialog(this@UserAttendanceActivity)
        myDialog.setContentView(R.layout.deregisteremp_layout)
        myDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val employeeid_edt = myDialog.findViewById<EditText>(R.id.employeeid_edt)
        val cancel_btn = myDialog.findViewById<View>(R.id.cancel_btn) as Button
        val deactivate_txt = myDialog.findViewById<TextView>(R.id.deactivate_txt)
        deactivate_txt.text = "Enter Employee Id"
        val deactivate_btn = myDialog.findViewById<View>(R.id.deactivate_btn) as Button
        deactivate_btn.text = "Ok"
        myDialog.setCancelable(false)
        deactivate_btn.setOnClickListener {
            employeeid = employeeid_edt.text.toString()
            if (employeeid.isNotEmpty()) {
                binding.employeeEdt?.setText(employeeid)
                myDialog.dismiss() //
            } else {
                Toast.makeText(
                    this@UserAttendanceActivity,
                    "Please Enter valid Employee ID",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        cancel_btn.setOnClickListener {
            myDialog.dismiss()
            finish()
        }
        myDialog.show()
    }

    private fun checkDeviceCapability(onSuccess: () -> Unit) {
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                onSuccess()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showSnackBar("No biometric features available on this device")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showSnackBar("Biometric features are currently unavailable")
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                showSnackBar("Biometric options are incompatible with the current Android version")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    enrollBiometricRequestLauncher.launch(Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or DEVICE_CREDENTIAL
                        )
                    })
                } else {
                    showSnackBar("Could not request biometric enrollment in API level < 30")
                }
            }

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                showSnackBar("Biometric features are unavailable because security vulnerabilities has been discovered in one or more hardware sensors")
            }

            else -> {
                throw IllegalStateException()
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.place_your_finger_on_scanner_to_scan))
            .setDeviceCredentialAllowed(true).setSubtitle("")
            .setConfirmationRequired(false).build()

        val biometricPrompt = BiometricPrompt(this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle authentication error
                    showMessage(errString.toString())
                    if (errString == "No fingerprints enrolled.") {
                        navigatetoBiometricScreen()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authenticationSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Handle authentication failure
                    binding.authMessageTv?.text = getString(R.string.cannot_recognize_your_finger)
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun navigatetoBiometricScreen() {
        val intent: Intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                    EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                Intent(Settings.ACTION_FINGERPRINT_ENROLL)
            }

            else -> {
                Intent(Settings.ACTION_SECURITY_SETTINGS)
            }
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun authenticationSuccess() {
        if (isregistered) {

            if (employeeid.isNotEmpty()) {
                vm.postUserRegistration(
                    imei /*tryEncrypt(cryptoObject.getCipher())*/, employeeid
                )
            } else {
//                progressDialog?.dismiss()
                showDialog(
                    this@UserAttendanceActivity,
                    getString(R.string.error),
                    "Please enter EmployeeId"
                ) { dialog, which -> dialog.dismiss() }
            }
        } else {
            if (userLocation.latitude != 0.0 && userLocation?.longitude != 0.0) {
                vm.postUserAttendance(
                    employeeid,
                    imei,
                    location.value?.latitude.toString(),
                    location.value?.longitude.toString(),
                    "getGPsPositions"
                )
            } else {
                showDialog(
                    this@UserAttendanceActivity,
                    "Error",
                    "Unable to get your location to post attendance ,please check location enabled"
                ) { dialog, which ->
                    dialog.dismiss()
                }
            }
        }
    }


    private fun showMessage(message: String) {
        showDialog(
            this@UserAttendanceActivity,
            "Error",
            message
        ) { dialog, which ->
            dialog.dismiss()
        }
    }

    private fun updateLocation() {
        if (isLocationEnabled()) {
            observeLocationUpdates()
        } else {
            //showLongToast("Turn on location")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun requestPermissions() {
        ++permissionAskingCount
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun observeLocationUpdates() {
        vm.getLocationData.observe(this) {
            userLocation = it
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if permissions are granted we are displaying below toast message.
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                // in the below line, we are displaying toast message if permissions are not granted.
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}