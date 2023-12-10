package com.example.parking.ApiClient.Controllers;

import android.content.Context;
import android.graphics.Color;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.Enums.MapDirectionMode;
import com.example.parking.ApiClient.IDirectionCallBack;
import com.example.parking.ApiClient.WebServices.RetrofitAPI;
import com.example.parking.ApiClient.WebServices.RetrofitClient;
import com.example.parking.ApiClient.interfaces.IResponce;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionLegModel;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionResponseModel;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionRouteModel;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionStepModel;
import com.example.parking.Helpers.DestinceOptions;
import com.example.parking.R;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsDirectionsController  implements IResponce {

    private Context context;
    private RetrofitAPI retrofitAPI;
    private  double thresholdDistance = 3;

    public MapsDirectionsController(Context context) {

        this.context = context;
        this.retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
    }

    public double getThresholdDistance() { return  thresholdDistance;}
    public void setThresholdDistance(double threshold) { thresholdDistance=threshold;}


    public String getDirectionsUrl(LatLng origin, LatLng dest,MapDirectionMode mode) {

        StringBuilder url=null;
        if(origin!=null && dest!=null && mode!=null)
        {
            url=new StringBuilder();
            url.append(AppConfig.MAPS_DIRECTION_URL).append("json").append("?");
            url.append("&").append("origin=").append(origin.latitude).append(",").append(origin.longitude);
            url.append("&").append("destination=").append(dest.latitude).append(",").append(dest.longitude);
            url.append("&").append("mode=").append(mode.getValue());
            url.append("&").append("key=").append(context.getResources().getString(R.string.MAP_KEY));
        }
        return (url!=null)?url.toString():null;
    }

    public void getDirections(LatLng original, LatLng destination, MapDirectionMode mode, boolean isLocationPermissionOk, IDirectionCallBack callback) {

        if (isLocationPermissionOk)
        {

            callback.onStartDirection(null);
//            Log.d("original",String.valueOf(original));
            String url = getDirectionsUrl(original,destination,mode);

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    if (response.errorBody() == null)
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getDirectionRouteModels().size() > 0)
                            {
                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);
                                DirectionLegModel legModel = routeModel.getLegs().get(0);
                                LatLng  startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                LatLng  endLocation = new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng());
                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions()
                                        .width(25)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);
                                List<PatternItem> pattern;
                                if (mode.equals("walking")) {
                                    pattern = Arrays.asList(new Dot(), new Gap(10));
                                    options.jointType(JointType.ROUND);
                                }
                                else
                                {
                                    pattern = Arrays.asList(new Dash(30));
                                }
                                options.pattern(pattern);
                                for (DirectionStepModel stepModel : legModel.getSteps()) {
                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
                                    }
                                }
                                options.addAll(stepList);
                                double distance= DestinceOptions.calculateDistance(original.latitude, original.longitude,destination.latitude, destination.longitude);
                                callback.onResponseDirection(response.body(),legModel,options,startLocation,endLocation,distance);
                            }
                            else
                                callback.onDirectionFailure( "No route find");
                        }
                        else
                            callback.onDirectionFailure( "No route find");
                    }
                    else
                        callback.onDirectionFailure( response);

                    callback.onStopDirection(null);
                }

                @Override
                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {
//                    callback.onStopDirection(null);
                    callback.onDirectionFailure(t.getMessage());
                }
            });
        }

    }

    public List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }



    @Override
    public void onSuccess(Object call) {

    }

    @Override
    public void onFailure(String call) {

    }
}
