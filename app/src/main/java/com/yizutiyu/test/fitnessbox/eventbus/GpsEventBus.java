package com.yizutiyu.test.fitnessbox.eventbus;

/**
 * @author
 * @data 2019/9/26
 */
public class GpsEventBus {
    private double longide;

    public GpsEventBus(double longide, double lat) {
        this.longide = longide;
        this.lat = lat;
    }

    public double getLongide() {
        return longide;
    }

    public void setLongide(double longide) {
        this.longide = longide;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lat;
}
