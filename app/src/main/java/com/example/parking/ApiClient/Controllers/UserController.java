package com.example.parking.ApiClient.Controllers;

import android.content.Context;

import com.example.parking.ApiClient.interfaces.IUserResponse;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.ApiClient.models.SignUpRequest;
import com.example.parking.Models.RegisterationToken;
import com.google.gson.Gson;

import retrofit2.Call;

public class UserController implements IUserResponse {


    public void signUp(SignUpRequest body, Context context)  {

        Call<BaseResponse> call = ApiRequest.getService().signUp(body);
        new ApiRequest().executeRequest(call,this);


//        call.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (response.isSuccessful()) {
//                    // Request successful
//
//                    System.out.println("Request successful. Response: " + String.valueOf(response.body()));
//
//                } else
//                {
//                    // Request failed
//                    System.out.println("Request failed. Response code: " + String.valueOf(response.code()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                // Request failed due to network or other issues
//                System.out.println("Request failed : " + t.getMessage());
//            }
//        });



    }


   public void setToken(RegisterationToken body, Context context)  {

        Call<BaseResponse> call = ApiRequest.getService().setToken(body);
        new ApiRequest().executeRequest(call,this);
    }

    @Override
    public void onSuccess(BaseResponse call) {

        System.out.println("myResponse Success: " +new Gson().toJson(call));
    }

    @Override
    public void onFailure(String call) {

    }
}
