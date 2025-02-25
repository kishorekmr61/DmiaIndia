package com.dmia.bioAttendance.ui.loginActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dmia.bioAttendance.R
import com.dmia.bioAttendance.databinding.ActivityAdminLoginBinding
import com.dmia.bioAttendance.helper.NetworkResult
import com.dmia.bioAttendance.ui.AdminDashboardActivity.AdminDashboardActivity
import com.dmia.bioAttendance.ui.UserDashboardActivity.UserDashboardActivity
import com.dmia.bioAttendance.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private val vm: LoginInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = vm
        setObserver()
        binding.loginBtn.setOnClickListener {
            val Username = binding.etUsername.text.toString();/*"jayant.kn@dmiagrp.com"*/
            val Password = binding.etPswrd.text.toString();/*"dmiaerp123"*/
            if (Username?.isNotEmpty() == true && Password?.isNotEmpty() == true) {
                binding.pgBar.visibility = View.VISIBLE
                vm.verifyLogin(Username, Password)
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_credentials),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun setObserver() {
        vm.userResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE

                    if (response.data == "SUCCESS") {
                        val intent =
                            Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        showDialog(
                            this,
                            getString(R.string.failure),
                            getString(R.string.login_credentials)
                        ) { dialog, which -> dialog.dismiss() }
                    }
                }


                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this,
                        getString(R.string.failure),
                        getString(R.string.login_credentials)
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@LoginActivity, UserDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}