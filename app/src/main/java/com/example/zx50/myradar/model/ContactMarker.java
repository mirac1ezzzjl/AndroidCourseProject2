package com.example.zx50.myradar.model;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;

public class ContactMarker {
    Marker marker = null;
    Polyline line = null;
    Marker distanceMarker = null;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Polyline getLine() {
        return line;
    }

    public void setLine(Polyline line) {
        this.line = line;
    }

    public Marker getDistanceMarker() {
        return distanceMarker;
    }

    public void setDistanceMarker(Marker distanceMarker) {
        this.distanceMarker = distanceMarker;
    }

}
