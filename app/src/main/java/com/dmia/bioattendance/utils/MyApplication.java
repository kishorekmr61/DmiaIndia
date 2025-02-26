package com.dmia.bioattendance.utils;


import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MyApplication extends MultiDexApplication {

    private GoogleApiClient mGoogleApiClient;
    public AppCompatActivity activity;


    public GoogleApiClient getGoogleApiClient(AppCompatActivity activity, GoogleApiClient.OnConnectionFailedListener listener) {
        this.activity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, listener)
                .addApi(LocationServices.API)
                .build();
        return mGoogleApiClient;
    }
}
