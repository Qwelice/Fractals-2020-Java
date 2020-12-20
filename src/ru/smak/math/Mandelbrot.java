package ru.smak.math;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class Mandelbrot implements Fractal{

    private int maxIters = 200;
    private int dynamicIters = 200;
    private double r2 = 4;
    private static DynamicIters d = new DynamicIters();
    private boolean dynDetalization = true;


    public void setDynDetalization(boolean b){
        dynDetalization = b;
    }
    public void findDynIters(CartesianScreenPlane p){
        if(dynDetalization ){
            dynamicIters = d.getIters(p);
        }
    }
    public void setMaxIters(int value){
        maxIters = Math.max(5, value);
    }

    public void setR(int value){
        var r = Math.max(0.1, value);
        r2 = r*r;
    }

    @Override
    public double isInSet(Complex c) {
        final var z = new Complex();
        if(dynDetalization) {
            for (int i = 0; i < dynamicIters; i++) {
                z.timesAssign(z);
                z.plusAssign(c);
                if (z.abs2() > r2)
                    return i - Math.log(Math.log(z.abs()) / Math.log(dynamicIters)) / Math.log(2.0);
            }
            return 1.0F;
        }else{
            for (int i = 0; i < maxIters; i++) {
                z.timesAssign(z);
                z.plusAssign(c);
                if (z.abs2() > r2)
                    return i - Math.log(Math.log(z.abs()) / Math.log(maxIters)) / Math.log(2.0);
            }
            return 1.0F;
        }
    }
}
