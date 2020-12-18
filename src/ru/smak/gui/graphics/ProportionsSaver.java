package ru.smak.gui.graphics;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public class ProportionsSaver {
    private int baseWidth;
    private int baseHeight;
    private double baseXMin;
    private double baseXMax;
    private double baseYMin;
    private double baseYMax;
    private boolean savingOn = false;

    public ProportionsSaver(CartesianScreenPlane plane, int width, int height){
        baseWidth = width;
        baseHeight = height;
        baseXMin = plane.xMin;
        baseXMax = plane.xMax;
        baseYMin = plane.yMin;
        baseYMax = plane.yMax;
    }

    public void setSaving(boolean state){
        savingOn = state;
    }

    public void maintain(CartesianScreenPlane plane, int newWidth, int newHeight){
        if(savingOn){
            var wM = baseWidth;
            var hM = baseHeight;
            var XmaxPlane = baseXMax;
            var XminPlane = baseXMin;
            var YmaxPlane = baseYMax;
            var YminPlane = baseYMin;

            var kW = (float)newWidth/(float)wM;
            var kH = (float)newHeight/(float)hM;
            var ration0 = (float)newWidth/(float)newHeight;
            //var ration = kW/kH;

            if(kW<1 || kH<1){
                if (ration0<=1.5)
                {
                    var ymin = plane.yMin;
                    plane.yMin = (plane.yMax+plane.yMin)/2-(plane.xMax-plane.xMin)*(1/ration0)/2;
                    plane.yMax = (plane.yMax+ymin)/2+(plane.xMax-plane.xMin)*(1/ration0)/2;
                    plane.xMin = XminPlane;
                    plane.xMax = XmaxPlane;
                }
                else{
                    plane.yMin = YminPlane;
                    plane.yMax = YmaxPlane;
                    var xmin = plane.xMin;
                    plane.xMin = (plane.xMax + plane.xMin)/2 - (plane.yMax-plane.yMin)*(ration0)/2;
                    plane.xMax = (plane.xMax + xmin)/2 + (plane.yMax-plane.yMin)*(ration0)/2;
                }
            }
            else{
                plane.xMin = XminPlane - (kW-1)*(XmaxPlane-XminPlane)/2;
                plane.xMax = XmaxPlane + (kW-1)*(XmaxPlane-XminPlane)/2;
                plane.yMin = YminPlane - (kH-1)*(YmaxPlane-YminPlane)/2;
                plane.yMax = YmaxPlane + (kH-1)*(YmaxPlane-YminPlane)/2;
            }
        }
    }

    public void maintain(CartesianScreenPlane plane, double xMin, double xMax, double yMin, double yMax){
        if(savingOn){
            var pWidth =  xMax - xMin;
            var pHeight = yMax - yMin;
            var pRatio = (float)plane.getHeight()/(float)plane.getWidth();
            if (pWidth*pRatio>pHeight){
                var pNewHeight = pWidth*pRatio;
                plane.xMin = xMin;
                plane.yMin = yMin-Math.abs((pNewHeight-pHeight)/2);
                plane.xMax = xMin+pWidth;
                plane.yMax = yMin+pNewHeight-Math.abs((pNewHeight-pHeight)/2);
            }
            else{
                var pNewWidth = pHeight/pRatio;
                plane.xMin = xMin - Math.abs((pNewWidth-pWidth)/2);
                plane.yMin = yMin;
                plane.xMax = xMin+pNewWidth-Math.abs((pNewWidth-pWidth)/2);
                plane.yMax = yMin+pHeight;
            }
        }
        else{
            plane.xMin = xMin;
            plane.xMax = xMax;
            plane.yMin = yMin;
            plane.yMax = yMax;
        }
    }
}
