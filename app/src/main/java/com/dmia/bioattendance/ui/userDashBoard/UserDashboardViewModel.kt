package com.dmia.bioattendance.ui.userDashBoard

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