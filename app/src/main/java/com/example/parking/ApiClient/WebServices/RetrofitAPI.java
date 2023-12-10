package com.example.parking.ApiClient.WebServices;

import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionResponseModel;
import com.example.parking.ApiClient.models.GooglePlaceModel.GoogleResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<GoogleResponseModel> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}
