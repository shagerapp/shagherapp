package com.example.parking.ApiClient.models.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionLegModel {


    @SerializedName("distance")
    @Expose
    private DirectionDistanceModel distance;
    @SerializedName("duration")
    @Expose
    private DirectionDurationModel duration;
    @SerializedName("end_address")
    @Expose
    private String endAddress;
    @SerializedName("end_location")
    @Expose
    private EndLocationModel endLocation;
    @SerializedName("start_address")
    @Expose
    private String startAddress;
    @SerializedName("start_location")
    @Expose
    private StartLocationModel startLocation;
    @SerializedName("steps")
    @Expose
    private List<DirectionStepModel> steps = null;

    @SerializedName("traffic_speed_entry")
    @Expose
    private List<String> traffic_speed_entry ;


    @SerializedName("via_waypoint")
    @Expose
    private List<String> via_waypoint ;




    public DirectionDistanceModel getDistance() {
        return distance;
    }

    public void setDistance(DirectionDistanceModel distance) {
        this.distance = distance;
    }

    public DirectionDurationModel getDuration() {
        return duration;
    }

    public void setDuration(DirectionDurationModel duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public EndLocationModel getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(EndLocationModel endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public StartLocationModel getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocationModel startLocation) {
        this.startLocation = startLocation;
    }

    public List<DirectionStepModel> getSteps() {
        return steps;
    }

    public void setSteps(List<DirectionStepModel> steps) {
        this.steps = steps;
    }

    public List<String> getTraffic_speed_entry() {
        return traffic_speed_entry;
    }

    public List<String> getVia_waypoint() {
        return via_waypoint;
    }


    public void setTraffic_speed_entry(List<String> traffic_speed_entry) {
        this.traffic_speed_entry = traffic_speed_entry;
    }

    public void setVia_waypoint(List<String> via_waypoint) {
        this.via_waypoint = via_waypoint;
    }


}
