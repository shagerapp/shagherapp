package com.example.parking.Models;

import java.io.Serializable;

public class Spot extends  ParkingSpot implements Serializable {

    private transient String id;

    public  void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
