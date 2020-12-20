package ru.smak;

import ru.smak.gui.graphics.coordinates.*;

import java.awt.*;

public class SaveProportions {
    int wM = 0;
    int hM = 0;
    double XmaxPlane = 0;
    double XminPlane = 0;
    double YmaxPlane = 0;
    double YminPlane = 0;
    CartesianScreenPlane plane ;
    public SaveProportions( double XmaxPlane, double XminPlane, double YmaxPlane, double YminPlane,
                                int PanelWidh,int PanelHaight){

        this.XmaxPlane = XmaxPlane;
        this.XminPlane = XminPlane;
        this.YmaxPlane = YmaxPlane;
        this.YminPlane = YminPlane;
        wM = PanelWidh;
        hM = PanelHaight;
    }

    public void Go(int NewPanelWidh, int NewPanelHaight ,CartesianScreenPlane plane){
        var kW = (float)NewPanelWidh/(float)wM;
        var kH = (float)NewPanelHaight/(float)hM;
        var ration0 = (float)NewPanelWidh/(float)NewPanelHaight;
        var ration = kW/kH;

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

     public void newScal(Rectangle r, int PanelWidh,int PanelHaight, CartesianScreenPlane pplane){
         var xMin = Converter.xScr2Crt(r.x,pplane);
         var xMax = Converter.xScr2Crt(r.x+r.width,pplane);

         var yMin = Converter.yScr2Crt(r.y+r.height,pplane);
         var yMax = Converter.yScr2Crt(r.y,pplane);

         var pWidh =  xMax - xMin;
         var pHaight = yMax - yMin;
         var pRatio = (float)pplane.getHeight()/(float)pplane.getWidth();
         if (pWidh*pRatio>pHaight){
             var pNewHaight = pWidh*pRatio;
             pplane.xMin = xMin;
             pplane.yMin = yMin-Math.abs((pNewHaight-pHaight)/2);
             pplane.xMax = xMin+pWidh;
             pplane.yMax = yMin+pNewHaight-Math.abs((pNewHaight-pHaight)/2);
         }
         else{
             var pNewWidh = pHaight/pRatio;
             pplane.xMin = xMin - Math.abs((pNewWidh-pWidh)/2);
             pplane.yMin = yMin;
             pplane.xMax = xMin+pNewWidh-Math.abs((pNewWidh-pWidh)/2);
             pplane.yMax = yMin+pHaight;
         }
         wM = PanelWidh;
         hM = PanelHaight;
         XmaxPlane = pplane.xMax;
         XminPlane = pplane.xMin;
         YmaxPlane = pplane.yMax;
         YminPlane = pplane.yMin;
     }

     public double getPlaneXmin(){
        return XminPlane;
     }

    public double getPlaneXmax(){
        return XmaxPlane;
    }

    public double getPlaneYmin(){
        return YminPlane;
    }

    public double getPlaneYmax(){
        return YmaxPlane;
    }
}
