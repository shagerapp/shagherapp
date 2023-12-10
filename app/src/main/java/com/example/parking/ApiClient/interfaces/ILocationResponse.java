package com.example.parking.ApiClient.interfaces;

import android.location.Location;

import retrofit2.http.Body;

public interface ILocationResponse  {
    void onCurrentLocationResponse(@Body Location call);
    void onCurrentLocationFailure(String message);
}
