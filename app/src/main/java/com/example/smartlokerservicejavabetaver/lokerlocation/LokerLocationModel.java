package com.example.smartlokerservicejavabetaver.lokerlocation;

public class LokerLocationModel{
    private String name;
    private String locationUrl;
    private String locationstatus;


    public LokerLocationModel(String name, String locationUrl, String locationstatus) {
        this.name = name;
        this.locationUrl = locationUrl;
        this.locationstatus = locationstatus;
    }

    public String getName() {
        return name;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public String getLocationstatus() {
        return locationstatus;
    }
}
