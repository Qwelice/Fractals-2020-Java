package ru.smak.math;

public class Julia implements Fractal{
    private int maxIters = 200;
    private double r2 = 4;
    private Complex c = new Complex();

    public void setMaxIters(int value){
        maxIters = Math.max(5, value);
    }

    public void setR(int value){
        var r = Math.max(0.1, value);
        r2 = r*r;
    }

    public void setC(double x, double y){
        c.setRe(x);
        c.setIm(y);
    }

    @Override
    public double isInSet(Complex z) {
        for (int i = 0; i<maxIters; i++){
            z.timesAssign(z);
            z.plusAssign(c);
            if (z.abs2() > r2)
                return i - Math.log(Math.log(z.abs())/Math.log(maxIters))/Math.log(2.0);
        }
        return 1.0F;
    }
}
