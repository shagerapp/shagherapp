package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserProfile implements Serializable {


    private  String  userId;

    @SerializedName("phone")
    @Expose
    private  String  phone;

    @SerializedName("cars")
    @Expose
    private ArrayList<String> cars;

    @SerializedName("role")
    @Expose
    private  String  role;


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCars(ArrayList<String> cars) {
        this.cars = cars;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<String> getCars() {
        return cars;
    }

    public String getRole() {
        return role;
    }


}
