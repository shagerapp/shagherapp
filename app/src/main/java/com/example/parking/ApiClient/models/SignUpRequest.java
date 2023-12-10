package com.example.parking.ApiClient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpRequest
{


    @SerializedName("userId")
    @Expose
    private  String userId;

    @SerializedName("role")
    @Expose
    private  String role;


    @SerializedName("token")
    @Expose
    private  String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }


}
