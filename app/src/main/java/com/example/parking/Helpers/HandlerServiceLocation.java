package com.example.parking.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HandlerServiceLocation   {

    private static LocationRequest locationRequest;

    private static  Activity  _activity;

    public HandlerServiceLocation(Activity activity)
    {
        _activity=activity;
    }




    public  void startLocation(ICallback callBack)
    {
        //        locationRequest = LocationRequest.create();
        //        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //        locationRequest.setInterval(1000);
        //        locationRequest.setFastestInterval(2000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ActivityCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled())
                {
                    callBack.OnResponseCallback("Already");
                }
                else
                {
                    turnOnGPS(callBack);
                }

            } else
            {
//                new AppPermissions().requestLocationPermission(_activity);
                _activity.requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }





    public  void turnOnGPS(ICallback callBack) {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(_activity.getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {

                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(_activity, "Location Service is already tured on", Toast.LENGTH_SHORT).show();
                    HandlerServiceLocation.this.startLocation(callBack);
                }
                catch (ApiException e)
                {

                    switch (e.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try
                            {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(_activity, 2);

                            }
                            catch (IntentSender.SendIntentException ex)
                            {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    public  boolean isGPSEnabled()
    {

        if(_activity==null)
            return false;

        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) _activity.getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||  locationManager.isProviderEnabled(LocationManager.KEY_LOCATIONS);
        return isEnabled;

    }
}
