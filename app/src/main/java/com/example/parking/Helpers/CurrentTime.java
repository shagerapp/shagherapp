package com.example.parking.Helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CurrentTime {



    
    public  static final  String TIME_PATTREN="hh:mm a";

    public  static final  int subTime=5;
    public  static  int getHours()  {  return  Calendar.getInstance().getTime().getHours();}
    public  static  int getMinutes(){ return  Calendar.getInstance().getTime().getMinutes();}
    public  static  int getSeconds(){ return  Calendar.getInstance().getTime().getSeconds();}

    public static boolean isNotEnglishTimeFormat(String time) {
        String englishTimePattern = "^(0?[1-9]|1[0-2]):[0-5][0-9] [APap][mM]$";
        Pattern pattern = Pattern.compile(englishTimePattern);
        return !pattern.matcher(time).matches();
    }
    public static String convertARTimeToEN(String arabicTime)
    {
        if(!isNotEnglishTimeFormat(arabicTime))
            return  arabicTime;
        try
        {
            DateFormat  arabicFormat =  new SimpleDateFormat(TIME_PATTREN, new Locale("ar", "SA"));
            DateFormat  englishFormat = new SimpleDateFormat(TIME_PATTREN, Locale.ENGLISH);
            Date  time = null;
            time = arabicFormat.parse(arabicTime);
            return englishFormat.format(time);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    public static int getDurationTime(String startTime,String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTREN);
        LocalTime time1 = LocalTime.parse(startTime, formatter);
        LocalTime time2 = LocalTime.parse(endTime, formatter);
        long hours = ChronoUnit.HOURS.between(time1, time2);
        long minutes = ChronoUnit.MINUTES.between(time1, time2) % 60;
        long seconds = ChronoUnit.SECONDS.between(time1, time2) % 60;
        String diff = hours + ":" + minutes + ":" + seconds; // the duration in hh:mm:ss format
        System.out.println(diff);
        return  (int)hours;
    }
    public static int getDurationTime(String endTime) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_PATTREN);
            Date date = new Date();
            String currentTime = simpleDateFormat.format(date);
            return  getDurationTime(currentTime,endTime);
    }

    public static String getTimeAfterAddHours(String time,int countHours) {

        SimpleDateFormat simpleDateFormat =new  SimpleDateFormat(TIME_PATTREN);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date date = simpleDateFormat.parse(time);
            // Create a Calendar object and set it to the parsed date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // Add the desired number of hours to the Calendar object
            calendar.add(Calendar.HOUR_OF_DAY, countHours);
            // Get the updated Date object from the Calendar
            Date updatedDate = calendar.getTime();
            // Format the updated Date object to obtain the time in the desired format
            String formattedTime = simpleDateFormat.format(updatedDate);
            System.out.println("Updated time: " + formattedTime);
            return  formattedTime;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static String convertTimeFrom24To12(int hours,int minutes) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_PATTREN);
        Date date =new Date();
        date.setHours(hours);
        date.setMinutes(minutes);
        String newTime = simpleDateFormat.format(date);
        System.out.println(newTime);
        return  newTime;
    }
    public static String getCurrentDate(String format) {

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(currentDate);
    }


    public static String getCurrentTime(String format) {

        if(format=="")
            format=TIME_PATTREN;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format,Locale.ENGLISH);
        return formatter.format(LocalTime.now());

    }

    public  static  boolean CheckTime(String startTime,String endTime)
    {

        DateFormat sdf = new SimpleDateFormat(TIME_PATTREN);
        try
        {
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(endTime);
            return  (((Date) date1).before(date2));

        } catch (ParseException e){

            e.printStackTrace();
        }
        return  false;
    }


    public  static long getCurrentTimeInMillis()
    {
        Date date = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, date.getHours());
        cal.set(Calendar.MINUTE, date.getMinutes());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTimeInMillis();
    }

    public  static long ConvertTimeToMillis(int hour,int minute)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTimeInMillis();
    }

    public  static long getTimeDifference(String time1,String time2)
    {
        try {
//            time1=convertARTimeToEN(time1);
//            time2=convertARTimeToEN(time2);
            DateFormat dateFormat = new SimpleDateFormat(TIME_PATTREN, Locale.US);
            Date date1 = dateFormat.parse(time1);
            Date date2 = dateFormat.parse(time2);
            long timeDifference = date2.getTime() - date1.getTime();
            return timeDifference;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
