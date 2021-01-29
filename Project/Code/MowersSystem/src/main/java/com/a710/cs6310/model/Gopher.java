package com.a710.cs6310.model;

import com.a710.cs6310.common.Point;

public class Gopher {
    private Point currentLocation;

    public Gopher() {
    }

    public Gopher(Point pos) {
        this.currentLocation = pos;
    }

    public Point getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Point currentLocation) {
        this.currentLocation = currentLocation;
    }

}
