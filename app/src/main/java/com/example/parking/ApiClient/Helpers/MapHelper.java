package com.example.parking.ApiClient.Helpers;

import com.google.android.gms.maps.model.LatLng;

public class MapHelper {

   public static float calculateBearingBetweenPoints(LatLng point1,LatLng point2)
   {
       double x1 = point1.latitude;
       double y1 = point1.longitude;
       double x2 = point2.latitude;
       double y2 = point2.longitude;
       double angle = Math.atan2(y2 - y1, x2 - x1);

      return (float) Math.toDegrees(angle);
   }
}
