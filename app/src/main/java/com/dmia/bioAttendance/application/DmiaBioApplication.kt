package com.dmia.bioAttendance.application

import android.app.Application
import androidx.multidex.MultiDex
import com.dmia.bioAttendance.data.helpers.AppPreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DmiaBioApplication : Application() {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        AppPreference.init(applicationContext)
        context = this
        MultiDex.install(this)

    }

    companion object {
        lateinit var context: DmiaBioApplication
    }

}