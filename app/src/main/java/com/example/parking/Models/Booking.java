package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Booking implements Serializable {


    private transient String key;

    @SerializedName("UserId")
    @Expose
    private   String userId;
    @SerializedName("SpotId")
    @Expose
    private  String  spotId;
    @SerializedName("GroupId")
    @Expose
    private  String  groupId;

    @SerializedName("CarPlate")
    @Expose
    private  String  carPlate="";

    @SerializedName("Date")
    @Expose
    private  String  date="";



    @SerializedName("Status")
    @Expose
    private  boolean  status;

    @SerializedName("StartTime")
    @Expose
    private  String  startTime;

    @SerializedName("EndTime")
    @Expose
    private  String  endTime;

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    @SerializedName("Notify")
    @Expose
    private  int  notifiy=0;


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setNotifiy(int notifiy) {
        this.notifiy = notifiy;
    }

    public String getUserId() {
        return userId;
    }

    public String getSpotId() {
        return spotId;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean getStatus() {
        return status;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getNotifiy() {
        return notifiy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
