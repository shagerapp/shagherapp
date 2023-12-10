package com.example.parking.ApiClient.interfaces;

import java.util.HashMap;

import retrofit2.http.Body;

public interface IBookingStatistics {
    void onBookingStatisticsResponse(@Body HashMap<String,Integer> statistics);
}
