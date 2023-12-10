package com.example.parking.ApiClient.models.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionRouteModel {



    @SerializedName("bounds")
    @Expose
    private DirectionRouteBoundsModel bounds;

    @SerializedName("copyrights")
    @Expose
    private String copyrights;

    @SerializedName("legs")
    @Expose
    private List<DirectionLegModel> legs;

    @SerializedName("overview_polyline")
    @Expose
    private DirectionPolylineModel overview_polyline;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("warnings")
    @Expose
    private List<Object> warnings;

    @SerializedName("waypoint_order")
    @Expose
    private List<Object> waypoint_order;

    public List<DirectionLegModel> getLegs() {
        return legs;
    }

    public void setLegs(List<DirectionLegModel> legs) {
        this.legs = legs;
    }

    public DirectionPolylineModel getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(DirectionPolylineModel polylineModel) {
        this.overview_polyline = polylineModel;
    }

    public void setBounds(DirectionRouteBoundsModel bounds) {
        this.bounds = bounds;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    public void setWaypoint_order(List<Object> waypoint_order) {
        this.waypoint_order = waypoint_order;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public DirectionRouteBoundsModel getBounds() {
        return bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public List<Object> getWaypoint_order() {
        return waypoint_order;
    }
}
