package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ParkingGroups implements Serializable {

    @SerializedName("key")
    @Expose
        private   transient  String  key;

        @SerializedName("name")
        @Expose
        private    String  name;

        @SerializedName("coordinates")
        @Expose
        private    Coordinate coordinates;

        @SerializedName("address")
        @Expose
        private   String  address;



        public void setName(String name) {
            this.name = name;
        }



        public void setCoordinates(Coordinate coordinates) {
            this.coordinates = coordinates;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }


        public Coordinate getCoordinates() {
            return coordinates;
        }

        public String getAddress() {
            return address;
        }


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }



}
