package com.dmia.bioattendance.ui.adminDashBoard

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import com.dmia.bioattendance.ui.userDashBoard.UserDashboardActivity
import com.dmia.bioattendance.R
import com.dmia.bioattendance.data.constant.ApiConstant
import com.dmia.bioattendance.data.helpers.AppPreference
import com.dmia.bioattendance.databinding.ActivityAdminDashboardBinding
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.model.UsersData
import com.dmia.bioattendance.utils.showDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val vm: AdminDashBoardViewModel by viewModels()


    var StrEmployeeid = ""
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = vm
        setObserver()
        binding.empregisterLayout.setOnClickListener { v -> activateUser() }
        binding.deactivateempLayout.setOnClickListener { v ->
            if (AppPreference.read(ApiConstant.EMPLOYEEID, "")?.isEmpty() == true) {
                val myDialog = Dialog(this@AdminDashboardActivity)
                myDialog.setContentView(R.layout.deregisteremp_layout)
                myDialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val employeeid_edt = myDialog.findViewById<EditText>(R.id.employeeid_edt)
                val deactivate_txt = myDialog.findViewById<TextView>(R.id.deactivate_txt)
                deactivate_txt.text = getString(R.string.deactivate_employee)
                val cancel_btn =
                    myDialog.findViewById<View>(R.id.cancel_btn) as Button
                val deactivate_btn =
                    myDialog.findViewById<View>(R.id.deactivate_btn) as Button
                deactivate_btn.text = getString(R.string.deactivate)
                myDialog.setCancelable(false)
                deactivate_btn.setOnClickListener { v1: View? ->
                    StrEmployeeid = employeeid_edt.text.toString()
                    if (!StrEmployeeid.isEmpty()) {
                        myDialog.dismiss()
                        progressDialog =
                            ProgressDialog.show(
                                this@AdminDashboardActivity,
                                "",
                                getString(R.string.please_wait),
                                true
                            )
                        vm.deactivateUser(StrEmployeeid)
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            getString(R.string.invalid_employeeid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                cancel_btn.setOnClickListener { myDialog.dismiss() }
                myDialog.show()
            } else {
                progressDialog =
                    ProgressDialog.show(
                        this@AdminDashboardActivity,
                        "",
                        getString(R.string.please_wait),
                        true
                    )
                vm.deactivateUser(AppPreference.read(ApiConstant.EMPLOYEEID, "") ?: "")
            }
        }
    }

    private fun setObserver() {
        vm.deactivationResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (response.data != null) {
                            showDialog(
                                this@AdminDashboardActivity,
                                getString(R.string.success),
                                response.data?.EmpName
                            ) { dialog, which ->
                                dialog.dismiss()
                                AppPreference.write(ApiConstant.EMPLOYEEID, "")
                                AppPreference.write(ApiConstant.EMPLOYEEDETAILS, "")
                                val intent =
                                    Intent(
                                        this@AdminDashboardActivity,
                                        UserDashboardActivity::class.java
                                    )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        } else {
                            showDialog(
                                this@AdminDashboardActivity,
                                getString(R.string.failure),
                                getString(R.string.empid_not_found)
                            ) { dialog, which -> dialog.dismiss() }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@AdminDashboardActivity,
                        getString(R.string.failure),
                        getString(R.string.empid_not_found)
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
        vm.activationResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (response.data != null) {
                            showDialog(
                                this@AdminDashboardActivity,
                                getString(R.string.success),
                                response.data?.EmpName
                            ) { dialog, which ->
                                dialog.dismiss()
                                val intent =
                                    Intent(
                                        this@AdminDashboardActivity,
                                        UserDashboardActivity::class.java
                                    )
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        } else {
                            showDialog(
                                this@AdminDashboardActivity,
                                getString(R.string.failure),
                                getString(R.string.can_t_approve)
                            ) { dialog, which -> dialog.dismiss() }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@AdminDashboardActivity,
                        getString(R.string.failure),
                        getString(R.string.can_t_approve)
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }


    open fun activateUser() {
        val myDialog = Dialog(this@AdminDashboardActivity)
        myDialog.setContentView(R.layout.activationpopupdialog)
        myDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val activationuser_text = myDialog.findViewById<TextView>(R.id.activationuser_text)
        val cancel_btn = myDialog.findViewById<View>(R.id.cancel_btn) as Button
        val activate_btn = myDialog.findViewById<Button>(R.id.activate_btn)
        var employeedetails = Gson().fromJson(AppPreference.read(ApiConstant.EMPLOYEEDETAILS, ""),
            UsersData::class.java)
        activationuser_text.text =
            "Do you wish to activate " + employeedetails.Name.plus("(").plus(employeedetails.ID).plus(")")
        activate_btn.text = "Ok"
        myDialog.setCancelable(false)
        var imei = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        );
        activate_btn.setOnClickListener { v: View? ->
            myDialog.dismiss()
            vm.activateUser(imei, AppPreference.read(ApiConstant.EMPLOYEEID, "") ?: "")
        }
        cancel_btn.setOnClickListener {
            myDialog.dismiss()
            val intent = Intent(this@AdminDashboardActivity, UserDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        myDialog.show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@AdminDashboardActivity, UserDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}