package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterationToken {

    @SerializedName("UserId")
    @Expose
    private  String UserId;

    @SerializedName("Token")
    @Expose
    private  String Token;


    public String getUserId() {
        return UserId;
    }

    public String getToken() {
        return Token;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setToken(String token) {
        Token = token;
    }
}
