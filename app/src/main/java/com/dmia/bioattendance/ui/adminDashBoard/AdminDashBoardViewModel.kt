package com.dmia.bioattendance.ui.adminDashBoard

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.model.CommonModelResponse
import com.dmia.bioattendance.repositry.UsersRepositry
import com.dmia.bioattendance.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminDashBoardViewModel @Inject constructor(
    private val repository: UsersRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var deactivationResponse = MutableLiveData<NetworkResult<CommonModelResponse>>()
    var activationResponse = MutableLiveData<NetworkResult<CommonModelResponse>>()
    var isloading = ObservableField(false)


    fun deactivateUser(employeeid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getDeactivateUserData(employeeid).collect { values ->
                    deactivationResponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }

    fun activateUser(fingerprintvalue:String ,employeeid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getActivateUserData(fingerprintvalue ,employeeid).collect { values ->
                    activationResponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }
}