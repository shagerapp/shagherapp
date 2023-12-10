package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookingTemp  implements Serializable {


    private transient String spotKey;

    @SerializedName("startTimeBookingTemp")
    @Expose
    private String startTimeBookingTemp;


    @SerializedName("userId")
    @Expose
    private String userId;

    public String getSpotKey() {
        return spotKey;
    }

    public String getStartTimeBookingTemp() {
        return startTimeBookingTemp;
    }

    public String getUserId() {
        return userId;
    }

    public void setSpotKey(String spotKey) {
        this.spotKey = spotKey;
    }

    public void setStartTimeBookingTemp(String startTimeBookingTemp) {
        this.startTimeBookingTemp = startTimeBookingTemp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
