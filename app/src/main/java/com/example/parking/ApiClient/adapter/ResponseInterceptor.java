package com.example.parking.ApiClient.adapter;


import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResponseInterceptor implements Interceptor {
    private final Context context;


    public ResponseInterceptor(Context context) {
        this.context = context;

    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401 || !response.request().url().uri().toString().endsWith("ValidateOtp")) {

//            endSession(context);
        } else if (response.code() == 403) {
           // reActivate(context);
        }
        MediaType contentType;
        ResponseBody body;


        contentType = response.body().contentType();
        body = ResponseBody.create(contentType,response.body().string());
//        body = ResponseBody.create(contentType, response.body().string());
        return response.newBuilder().body(body).build();

    }


//    public static void openLogin(Context context) {
//        try {
//
//            Intent intent = new Intent(context, LoadingActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            context.startActivity(intent);
//        }catch (Exception e){}
//
//    }
//
//    public static void endSession(Context context) {
//
//        openLogin(context);
//    }
}