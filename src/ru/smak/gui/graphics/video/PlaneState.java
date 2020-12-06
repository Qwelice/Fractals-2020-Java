package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class PlaneState {
    private final double xMin;
    private final double xMax;
    private final double yMin;
    private final double yMax;

    public PlaneState(CartesianScreenPlane plane){
        xMin = plane.xMin;
        xMax = plane.xMax;
        yMin = plane.yMin;
        yMax = plane.yMax;
    }
    public PlaneState(double xMin, double xMax, double yMin, double yMax){
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }
    public double getXMin(){
        return xMin;
    }
    public double getXMax(){
        return xMax;
    }
    public double getYMin(){
        return yMin;
    }
    public double getYMax(){
        return yMax;
    }
}
