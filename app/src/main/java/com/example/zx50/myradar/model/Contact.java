package com.example.zx50.myradar.model;

import java.io.Serializable;

public class Contact implements Serializable{
    String name;
    String number;
    double latitude;
    double longitude;
    double altitude;
    int accuracy;
    double secondsSinceLastUpd;
    double secondsUntilNextUpd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public double getSecondsSinceLastUpd() {
        return secondsSinceLastUpd;
    }

    public void setSecondsSinceLastUpd(double secondsSinceLastUpd) {
        this.secondsSinceLastUpd = secondsSinceLastUpd;
    }

    public double getSecondsUntilNextUpd() {
        return secondsUntilNextUpd;
    }

    public void setSecondsUntilNextUpd(double secondsUntilNextUpd) {
        this.secondsUntilNextUpd = secondsUntilNextUpd;
    }
}
