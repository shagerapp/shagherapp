package com.example.parking.Helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.FragmentActivity;

import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Models.Coordinate;
import com.example.parking.Models.Spot;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {



    public static boolean isServiceRunning(Class<?> serviceClass, Context activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        java.util.List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }
    public static Bitmap DrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public  static   String FormatDateAndTime(String format, TimePicker timePicker)
    {

        Date date = Calendar.getInstance().getTime();
        int day = date.getDay();
        int month =date.getMonth();
        int year = date.getYear();
        int hour = (timePicker.getHour());
        int minute = timePicker.getMinute();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,minute);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }
    public  static   Map<Integer, Double>   getDistanceArray(LatLng currentLocation,ArrayList<Spot> parkingArray)
    {
        Map<Integer, Double> dist= new HashMap<Integer, Double>();
        for (int i = 0; i < parkingArray.size(); i++)
        {
            Coordinate point1=parkingArray.get(i).getCoordinates();
            if(point1!=null && currentLocation!=null)
            {
//
                double ds = calculateDistance(currentLocation,new LatLng(point1.getLatitude(),
                        point1.getLongitude()));//  Math.sqrt(Math.pow(currentLocation.latitude - point1.getLatitude(), 2) + Math.pow(currentLocation.longitude - point1.getLongitude(), 2));
                Log.d("ds", ds+"");
                dist.put(i, ds);
            }
        }

        return  dist;
    }

    public static   void setReadOnly(EditText editText,boolean allowWrite)
    {
        editText.setEnabled(allowWrite);
        editText.setFocusable(allowWrite);
        editText.setFocusableInTouchMode(allowWrite);
    }

    public static Spot findNearestPoint(List<Spot> spots, LatLng specificPoint) {
        Spot nearestPoint = null;
        int minDistance =100 ;

        for (Spot item : spots) {
            int distance =(int) calculateDistance(specificPoint,new LatLng(item.getCoordinates().getLatitude(),
                    item.getCoordinates().getLongitude()));
            Log.d("distance", distance+"");

            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = item;
            }
        }

        return nearestPoint;
    }

    public static double calculateDistance(LatLng point1, LatLng point2) {

        double xDiff = point2.latitude - point1.latitude;
        double yDiff = point2.longitude - point1.longitude;


//        String numberString1 = String.valueOf(xDiff);
//        String numberString2 = String.valueOf(yDiff);
//        // استخراج الأرقام العشرية المطلوبة
//        String decimalPart1 = numberString1.substring(numberString1.indexOf('.') + 1, numberString1.indexOf('.') + 1 + 6);
//        String decimalPart2 = numberString2.substring(numberString2.indexOf('.') + 1, numberString2.indexOf('.') + 1 + 6);
//        double xn= Double.valueOf(decimalPart1);
//        double yn= Double.valueOf(decimalPart2);
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public  static  int getNearestParkingIndex(LatLng currentLocation,ArrayList<Spot> parkingArray)
    {
        Map<Integer, Double>  dist=getDistanceArray(currentLocation,parkingArray);
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(dist.entrySet());
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        for (Map.Entry<Integer, Double> entry : list)
        {
            Integer key = entry.getKey();
            if (parkingArray.get(key).getStatus()== ParkingStatus.Available.ordinal())
                return  key;
        }
        return  -1;
    }

    public  static String getTempLocalStorageValue(FragmentActivity activity,String key)
    {
        SharedPreferences preferences =activity.getSharedPreferences("TempData", Context.MODE_PRIVATE);
        if(preferences.contains(key))
        {
            String value= preferences.getString(key, "not found value ");
            return  value;
        }

        return  null;
    }

    public static void setEditTextError(EditText editText, String message)
    {
        if(editText!=null)
        {
            editText.setError(message);
              editText.requestFocus();
        }
    }

    public static void removeEditTextError(EditText editText)
    {
        if(editText!=null && (editText.hasFocusable() || editText.hasFocus()))
        {
            editText.setError(null);
//            editText.clearFocus();
        }

    }


    public  static boolean setEndTime(FragmentActivity activity,long value)
    {
            SharedPreferences preferences = activity.getSharedPreferences("Time", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("EndTime", value);
            return editor.commit();
    }

    public  static long getEndTime(FragmentActivity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences("Time", Context.MODE_PRIVATE);
        if(preferences.contains("EndTime"))
            return preferences.getLong("EndTime",0);
        return -1;
    }

    public  static boolean RemoveEndTime(FragmentActivity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences("Time", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.contains("EndTime"))
            if(preferences.contains("EndTime"))
            {  editor.remove("EndTime");
                return  editor.commit();
            }

        return  false;
    }



}
