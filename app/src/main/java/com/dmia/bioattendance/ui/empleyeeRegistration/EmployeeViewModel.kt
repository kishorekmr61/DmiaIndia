package com.dmia.bioattendance.ui.empleyeeRegistration

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.repositry.UsersRepositry
import com.dmia.bioattendance.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject


@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val repository: UsersRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var employeSearchResponse = MutableLiveData<NetworkResult<ResponseBody>>()
    var isloading = ObservableField(false)


    fun searchUsers(employeeid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getEmployeeSearchData(employeeid).collect { values ->
                    employeSearchResponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }

}