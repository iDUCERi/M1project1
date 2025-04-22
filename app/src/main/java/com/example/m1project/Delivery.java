package com.example.m1project;

import android.location.Location;

public class Delivery extends User{

    private String transportType;
    private Location location; //live location
    private boolean isAvailable;// breaktime

    public Delivery(String name, String gmail, String phone, String password,String passwordAgain ,String city, String transportType, Location laction, boolean ifAvaible) {
            super(name, gmail, phone, password,passwordAgain,city);
        this.transportType = transportType;
        this.location = laction;
        this.isAvailable = ifAvaible;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

}
