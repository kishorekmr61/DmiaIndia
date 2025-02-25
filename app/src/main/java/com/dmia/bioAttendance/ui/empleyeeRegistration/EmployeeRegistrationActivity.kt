package com.dmia.bioAttendance.ui.empleyeeRegistration

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dmia.bioAttendance.BR
import com.dmia.bioAttendance.R
import com.dmia.bioAttendance.data.constant.ApiConstant
import com.dmia.bioAttendance.data.helpers.AppPreference
import com.dmia.bioAttendance.databinding.ActivityEmployeeRegistrationBinding
import com.dmia.bioAttendance.helper.NetworkResult
import com.dmia.bioAttendance.model.UsersData
import com.dmia.bioAttendance.ui.UserDashboardActivity.UserDashboardActivity
import com.dmia.bioAttendance.ui.userAttendance.UserAttendanceActivity
import com.dmia.bioAttendance.utils.setUpMultiViewRecyclerAdapter
import com.dmia.bioAttendance.utils.showDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray


@AndroidEntryPoint
class EmployeeRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeRegistrationBinding
    private val vm: EmployeeViewModel by viewModels()

    var getEmployess: MutableList<String>? = null
    var progressDialog: ProgressDialog? = null
    var employeename = ""
    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_registration)
        binding = ActivityEmployeeRegistrationBinding.inflate(layoutInflater)
        configuretoolbar()
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = vm
        setObserver()
        binding.searchBtn.setOnClickListener { v: View? ->
            employeename = binding.autoCompleteTextView.text.toString()
            vm.searchUsers(employeename)
        }
    }

    private fun setObserver() {
        vm.employeSearchResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (response.data != null) {
                            var dataresponse = JSONArray(response?.data?.string())
                            var datalistobj = ArrayList<UsersData>()
                            for (i in 0 until dataresponse.length()) {
                                datalistobj.add(
                                    UsersData(
                                        dataresponse.getJSONObject(i).getString("ID"),
                                        dataresponse.getJSONObject(i).getString("Name")
                                    )
                                )
                            }
                            updatEmployeesListAdapter(datalistobj)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@EmployeeRegistrationActivity,
                        getString(R.string.failure),
                        getString(R.string.empid_not_found),
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun updatEmployeesListAdapter(employeelist: ArrayList<UsersData>) {
        binding.employeesList.setUpMultiViewRecyclerAdapter(
            employeelist
        ) { item: UsersData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)

            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.layout -> {
                        employeeWarningMessage(item)
                    }
                }
            })
            binder.executePendingBindings()
        }
    }


    private fun configuretoolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
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

    fun employeeWarningMessage(userdata: UsersData) {
        val myDialog = Dialog(this)
        myDialog.setContentView(R.layout.deregisteremp_layout)
        myDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val employeeid_edt = myDialog.findViewById<EditText>(R.id.employeeid_edt)
        employeeid_edt.visibility = View.GONE
        val cancel_btn = myDialog.findViewById<View>(R.id.cancel_btn) as Button
        val deactivate_txt = myDialog.findViewById<TextView>(R.id.deactivate_txt)
        "${
            userdata.Name.plus("(").plus(userdata.ID).plus(")")
        } is your EmployeeID & Name ,Please check once again before registering.".also { deactivate_txt.text = it }
        deactivate_txt.textSize = 14f
        val deactivate_btn = myDialog.findViewById<View>(R.id.deactivate_btn) as Button
        deactivate_btn.text = "Ok"
        myDialog.setCancelable(false)
        deactivate_btn.setOnClickListener {
            myDialog.dismiss()
            val intent = Intent(applicationContext, UserAttendanceActivity::class.java)
            intent.putExtra("Login", "Login")
            AppPreference.write(ApiConstant.EMPLOYEEID, userdata.ID)
            AppPreference.write(ApiConstant.EMPLOYEEDETAILS, Gson().toJson(userdata))
            startActivity(intent)
        }
        cancel_btn.setOnClickListener { myDialog.dismiss() }
        myDialog.show()
    }
}