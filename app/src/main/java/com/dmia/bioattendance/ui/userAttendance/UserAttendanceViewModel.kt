package com.dmia.bioattendance.ui.userAttendance

import android.app.Application
import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.model.CommonModelResponse
import com.dmia.bioattendance.repositry.UsersRepositry
import com.dmia.bioattendance.utils.helper.LocationHelper
import com.dmia.bioattendance.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject


@HiltViewModel
class UserAttendanceViewModel @Inject constructor(
    private val repository: UsersRepositry,
    private var networkHelper: NetworkHelper,
    locationData: LocationHelper,
    var app: Application,
) : ViewModel() {

    var attendanceResponse = MutableLiveData<NetworkResult<CommonModelResponse>>()
    var userRegistrationResponse = MutableLiveData<NetworkResult<CommonModelResponse>>()
    var isloading = ObservableField(false)
    var getLocationData: MutableLiveData<Location> = locationData



    fun postUserAttendance(
        empID: String?,
        fingerprintvalue: String?,
        Latitude: String?,
        Longitude: String?, getGPsPositions: String?
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postUserAttendanceData(
                    empID,
                    fingerprintvalue,
                    Latitude,
                    Longitude, getGPsPositions
                ).collect { values ->
                    attendanceResponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }

    fun postUserRegistration(
        fingerprintvalue: String?, empID: String?
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postUserRegistrtaionData(
                    fingerprintvalue, empID
                ).collect { values ->
                    userRegistrationResponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }
}