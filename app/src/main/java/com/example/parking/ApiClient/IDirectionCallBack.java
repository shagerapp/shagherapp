package com.example.parking.ApiClient;

import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionLegModel;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionResponseModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public interface IDirectionCallBack {
     void onResponseDirection(DirectionResponseModel dirResponse, DirectionLegModel legModel, PolylineOptions options, LatLng startLoc, LatLng endLoc, double distance);
     void onStartDirection(Object value);
     void onStopDirection(Object value);
     void onDirectionFailure(Object value);
}
