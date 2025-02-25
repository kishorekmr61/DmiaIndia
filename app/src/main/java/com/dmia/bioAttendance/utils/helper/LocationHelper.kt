package com.dmia.bioAttendance.utils.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.dmia.bioAttendance.data.constant.ApiConstant.Companion.FASTEST_INTERVAL
import com.dmia.bioAttendance.data.constant.ApiConstant.Companion.INTERVAL
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocationHelper @Inject constructor(@ApplicationContext private val context: Context) :
    MutableLiveData<Location>() {

    private var fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     *
     * This method must be called from the main thread. If you need set a value from a background
     * thread, you can use postValue(Object)
     *
     */
    private fun setLocationData(location: Location) {
        value = location
    }

    /**
     * Static object of location request
     */
    companion object {
        val locationRequest: LocationRequest = LocationRequest.create()
            .apply {
                interval = INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 25f
            }
    }


    /**
     * Called when the number of active observers change to 1 from 0.
     * This callback can be used to know that this LiveData is being used thus should be kept
     * up to date.
     */
    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }


    /**
     * Called when the number of active observers change from 1 to 0.
     *
     * This does not mean that there are no observers left, there may still be observers but their
     * lifecycle states aren't STARTED or RESUMED
     * (like an Activity in the back stack).
     *
     * You can check if there are observers via hasObservers() method
     */
    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
        startLocationUpdates()
    }

    /**
     * Callback that triggers on location updates available
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation

            if (mLastLocation != null) {
                setLocationData(mLastLocation)
            }
        }
    }

    /**
     * Initiate Location Updates using Fused Location Provider and
     * attaching callback to listen location updates
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

}