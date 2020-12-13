package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class PlanePair {
    private CartesianScreenPlane first = null;
    private CartesianScreenPlane second = null;

    public PlanePair(CartesianScreenPlane first, CartesianScreenPlane second){
        this.first = first;
        this.second = second;
    }
    public CartesianScreenPlane getFirst(){
        return first;
    }
    public CartesianScreenPlane getSecond(){
        return second;
    }
    public void setFirst(CartesianScreenPlane plane){
        first = plane;
    }
    public void setSecond(CartesianScreenPlane plane){
        second = plane;
    }
}
