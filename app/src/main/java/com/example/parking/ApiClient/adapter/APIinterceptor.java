package com.example.parking.ApiClient.adapter;

import android.content.Context;
import android.util.Log;

import com.example.parking.ApiClient.Enums.RequestMethod;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;



public class APIinterceptor implements Interceptor {
    private Context context;

    private RequestMethod requestMethod;

    public   void setRequestMethod(RequestMethod requestMethod)
    {
        this.requestMethod= requestMethod;
    }


    public   RequestMethod getRequestMethod()
    {
        return requestMethod;
    }


    public APIinterceptor(RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public APIinterceptor(Context context, RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {

        Request originalRequest = chain.request();
        Request modifiedRequest = originalRequest;
        RequestBody oldBody = modifiedRequest.body();
        Buffer buffer = new Buffer();
        if(oldBody!=null)
        {
            oldBody.writeTo(buffer);
        }

        String strOldBody = buffer.readUtf8();
        String newBody = null;
        try {
            newBody = strOldBody;

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, newBody);
        if (context != null)
        {
            String token="hdjd6736euwtyywr762353245263745273645tewyter";//UserInfo.getToken(context);

//            Log.d("Tokin",token+"rrrrrrrrrrrrrrrr");
//            if (token != null)
//            {
//                Log.d("Too",token+"");
//                modifiedRequest = originalRequest.newBuilder()
////                        .addHeader("Authorization", "Bearer " +token)
//                        .build();
//

//            }

        }


        if(requestMethod==RequestMethod.POST)
            originalRequest = modifiedRequest.newBuilder().post(body).build();
        else if(requestMethod==RequestMethod.GET)
            originalRequest = modifiedRequest.newBuilder().get().build();
        else if(requestMethod==RequestMethod.PUT)
            originalRequest = modifiedRequest.newBuilder().put(body).build();
        else if(requestMethod==RequestMethod.DELETE)
            originalRequest = modifiedRequest.newBuilder().delete(body).build();

        Log.d("RequestMethod ", String.valueOf(originalRequest));
        return chain.proceed(originalRequest);
    }

}
