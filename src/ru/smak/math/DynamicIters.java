package ru.smak.math;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class DynamicIters {

    public static int getIters(CartesianScreenPlane p){
        return (int)(Math.min(10000,200*(Math.abs(-2-1)/Math.abs(p.xMax-p.xMin))*(Math.abs(-1-1)/Math.abs(p.yMax-p.yMin))));
    }


}
