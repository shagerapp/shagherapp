package com.example.parking.ApiClient.models.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionResponseModel {

    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GeocodedWaypointsModel> geocoded_waypoints=null;

    @SerializedName("routes")
    @Expose
    private List<DirectionRouteModel> routes;

    @SerializedName("status")
    @Expose
    private String status;


    public List<DirectionRouteModel> getDirectionRouteModels() {
        return routes;
    }

    public void setDirectionRouteModels(List<DirectionRouteModel> directionRouteModels) {
        this.routes = directionRouteModels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GeocodedWaypointsModel> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public List<DirectionRouteModel> getRoutes() {
        return routes;
    }

    public void setGeocoded_waypoints(List<GeocodedWaypointsModel> geocoded_waypoints) {
        this.geocoded_waypoints = geocoded_waypoints;
    }

    public void setRoutes(List<DirectionRouteModel> routes) {
        this.routes = routes;
    }
}
