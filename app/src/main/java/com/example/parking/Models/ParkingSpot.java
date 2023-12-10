package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ParkingSpot implements Serializable {
    // Fields++

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("groupId")
    @Expose
    private String groupId;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("index")
    @Expose
    private int index;

    @SerializedName("coordinates")
    @Expose
    private Coordinate coordinates;

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public int getStatus() {
        return status;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public int getIndex() {
        return index;
    }
}
//public class ParkingSpot implements Serializable {
//
//    @SerializedName("name")
//    @Expose
//    private   String  name;
//    @SerializedName("groupId")
//    @Expose
//    private  String  groupId;
//
//    @SerializedName("status")
//    @Expose
//    private int status;
//
//    @SerializedName("index")
//    @Expose
//    private  int  index;
//
//    @SerializedName("coordinates")
//    @Expose
//    private  Coordinate coordinates;
//
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setGroupId(String groupId) {
//        this.groupId = groupId;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public void setCoordinates(Coordinate coordinates) {this.coordinates = coordinates;}
//
//    public void setIndex(int index) {this.index = index;}
//
//
//
//
//
//    public String getName() {
//        return name;
//    }
//
//    public String getGroupId() {
//        return groupId;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public Coordinate getCoordinates() {
//        return coordinates;
//    }
//
//    public int getIndex() {return  this.index ;}
//}
