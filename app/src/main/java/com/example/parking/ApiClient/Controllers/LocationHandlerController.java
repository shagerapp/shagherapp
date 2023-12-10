package com.example.parking.ApiClient.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.example.parking.ApiClient.WebServices.RetrofitAPI;
import com.example.parking.ApiClient.WebServices.RetrofitClient;
import com.example.parking.ApiClient.interfaces.ILocationResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandlerController  {


    private Context context;
    private RetrofitAPI retrofitAPI;
    private  double thresholdDistance = 3;

    public LocationHandlerController(Context context) {

        this.context = context;
        this.retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
    }

    public void getCurrentLocation(ILocationResponse callback)
    {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    callback.onCurrentLocationResponse(location);
                }
                else
                {
                    callback.onCurrentLocationFailure("Location Not Found");

                }
            }
        });
    }
}
