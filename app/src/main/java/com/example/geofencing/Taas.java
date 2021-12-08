package com.example.geofencing;

import java.io.Serializable;

public class Taas {
    double lat;
    double lon;

    public Taas(double lat,double lon){
        this.lat = lat;
        this.lon = lon;
    }
    public double getlat(){
        return lat;
    }

    public void setlat(double lat) {
        this.lat = lat;
    }

    public double getlon(){
        return lon;
    }

    public void setlon(double lon) {
        this.lon = lon;
    }
}
