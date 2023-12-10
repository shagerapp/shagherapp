package com.example.parking.ApiClient.interfaces;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionResponseModel;
import com.example.parking.ApiClient.models.SignUpRequest;
import com.example.parking.Models.RegisterationToken;
import com.example.parking.Models.UserLocationStateModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIInterface {


    @POST(AppConfig.API_URL +"Service/signUp")
    Call<BaseResponse> signUp(@Body SignUpRequest signUpRequest);

    @POST(AppConfig.API_URL +"Service/changeRegisterationToken")
    Call<BaseResponse> setToken(@Body RegisterationToken request);

    //@GET(AppConfig.SUB_URL+"maps/api/directions/json")
    @POST(AppConfig.API_URL +"Service/checkUserLocationState")
    Call<BaseResponse> checkUserLocationState(@Body UserLocationStateModel request);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);

    @GET
    Call<BaseResponse> sendNotifyAfterUserBooked(@Body String url);

}

