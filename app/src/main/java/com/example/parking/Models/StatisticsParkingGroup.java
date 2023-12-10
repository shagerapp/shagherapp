package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatisticsParkingGroup implements Serializable {

    @SerializedName("groupId")
    @Expose
    private    String  groupId;
    @SerializedName("groupName")
    @Expose
    private    String  groupName;
    @SerializedName("countAvailable")
    @Expose
    private    long  countAvailable;
    @SerializedName("countNotAvailable")
    @Expose
    private    long  countNotAvailable;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCountAvailable(long countAvailable) {
        this.countAvailable = countAvailable;
    }

    public void setCountNotAvailable(long countNotAvailable) {
        this.countNotAvailable = countNotAvailable;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getCountAvailable() {
        return countAvailable;
    }

    public long getCountNotAvailable() {
        return countNotAvailable;
    }








}
