package com.example.parking.Helpers;

import com.example.parking.Models.StatisticsParkingGroup;

import java.util.ArrayList;

public interface ICallback {
    default void OnResponseCallback(Object response){ throw new RuntimeException("Stub!");}
   default void OnResponseCallback(Object response,Object data){ throw new RuntimeException("Stub!");};
    default void OnResponseCallback(Object response, ArrayList<StatisticsParkingGroup> data){ throw new RuntimeException("Stub!");}
}
