package com.example.parking.Enum;

public enum Roles {


    USER("user"),
    ADMIN("admin");


    private String value;

    Roles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
