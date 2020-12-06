package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class FramesPair {
    private CartesianScreenPlane first;
    private CartesianScreenPlane second;

    public FramesPair(CartesianScreenPlane p1, CartesianScreenPlane p2){
        this.first = p1;
        this.second = p2;
    }

    public CartesianScreenPlane getFirst(){
        return first;
    }
    public CartesianScreenPlane getSecond(){
        return second;
    }
}
