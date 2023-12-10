package com.example.parking.ApiClient.Enums;

public enum UserLocationState {

    INSIDE("inSide"),
    OUTSIDE("outSide");

    private String value;

    UserLocationState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
