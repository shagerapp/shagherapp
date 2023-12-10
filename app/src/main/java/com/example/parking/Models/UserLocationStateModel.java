package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserLocationStateModel implements Serializable {

    @SerializedName("userId")
    @Expose
    private    String  userId="";

    @SerializedName("bookingId")
    @Expose
    private    String  bookingId="";

    @SerializedName("Latitude")
    @Expose
    private   Double latitude;

    @SerializedName("Longitude")
    @Expose
    private  Double longitude;

    public String getUserId() {
        return userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
