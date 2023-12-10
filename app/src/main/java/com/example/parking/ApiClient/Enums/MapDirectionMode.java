package com.example.parking.ApiClient.Enums;

public enum MapDirectionMode {

    DRIVING("driving"),
    WALKING("walking"),
    BICYCLING("bicycling"),
    TRANSIT("transit");


    private String value;

    MapDirectionMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
