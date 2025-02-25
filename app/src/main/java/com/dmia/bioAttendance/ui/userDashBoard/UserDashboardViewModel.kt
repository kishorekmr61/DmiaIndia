package com.dmia.bioAttendance.ui.userDashBoard

import android.app.Application
import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmia.bioAttendance.repositry.UsersRepositry
import com.dmia.bioAttendance.utils.helper.NetworkHelper
import com.dmia.bioAttendance.helper.NetworkResult
import com.dmia.bioAttendance.model.CommonModelResponse
import com.dmia.bioAttendance.utils.helper.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject


@HiltViewModel
class UserDashboardViewModel @Inject constructor(
    private val repository: UsersRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
    locationData: LocationHelper,
) : ViewModel() {

    var domainresponse = MutableLiveData<NetworkResult<ResponseBody>>()
    var activationResponse = MutableLiveData<NetworkResult<CommonModelResponse>>()
    var isloading = ObservableField(false)

    var userLocation: MutableLiveData<Location> = locationData

    fun getdoaminData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getDomainData().collect { values ->
                    domainresponse.postValue(values)
                }
            } else {
//                app.showToast("No Internet")
            }
        }
    }

}