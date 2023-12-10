package com.example.parking.ApiClient.models.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DirectionRouteBoundsModel {

    @SerializedName("northeast")
    @Expose
    private LocationModel northeast;


    @SerializedName("southwest")
    @Expose
    private LocationModel southwest;

    public LocationModel getNortheast() {
        return northeast;
    }

    public LocationModel getSouthwest() {
        return southwest;
    }


    public void setNortheast(LocationModel northeast) {
        this.northeast = northeast;
    }

    public void setSouthwest(LocationModel southwest) {
        this.southwest = southwest;
    }
}
