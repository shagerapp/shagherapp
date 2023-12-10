package com.example.parking.ApiClient.interfaces;

import retrofit2.http.Body;

public interface IResponce<T> {

    void onSuccess(@Body T response);
    void onFailure(String call);



}
