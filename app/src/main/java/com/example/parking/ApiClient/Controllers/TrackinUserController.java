package com.example.parking.ApiClient.Controllers;

import android.content.Context;
import android.widget.Toast;

import com.example.parking.ApiClient.WebServices.RetrofitAPI;
import com.example.parking.ApiClient.WebServices.RetrofitClient;
import com.example.parking.ApiClient.interfaces.ITrackingUserResponse;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.Models.UserLocationStateModel;

import retrofit2.Call;

public class TrackinUserController implements ITrackingUserResponse {

    private Context context;
    private RetrofitAPI retrofitAPI;
    private ITrackingUserResponse callback;
    public TrackinUserController(Context context) {

        this.context = context;
        this.retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
    }





    public void checkUserLocationState(UserLocationStateModel model,ITrackingUserResponse callback)
    {

        this.callback=callback;
//        StringBuilder  url=new StringBuilder();
//        url.append(AppConfig.API_URL).append("getUserLocationState/").append("?");
//        url.append("userId=").append(userId);
//        url.append("&").append("latitude=").append(location.latitude);
//        url.append("&").append("longitude=").append(location.longitude);
        Call<BaseResponse> call = ApiRequest.getService().checkUserLocationState(model);
        new ApiRequest().executeRequest(call,this);
    }

    @Override
    public void onSuccess(BaseResponse response) {


        if(callback==null)
            return;

        if(response==null)
            callback.onFailure("!! response is null ");
        else if(response.getResultCode()==200){
//            Toast.makeText(context, response.getResult(), Toast.LENGTH_SHORT).show();
            callback.onSuccess(response);
        }
        else
            callback.onFailure(response.getResultMessage());

    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
