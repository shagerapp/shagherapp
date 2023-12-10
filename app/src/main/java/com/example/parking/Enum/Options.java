package com.example.parking.Enum;

public enum Options {

    HASBOOKED("hasBooked"),
    NOTHASBOOKED("NotHasBooked"),
    SpotStatistics("SpotStatistics"),

    GroupsSpotStatistics("GroupsSpotStatistics"),
    EndGroupsSpotStatistics("EndGroupsSpotStatistics");




    private String value;

    Options(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
