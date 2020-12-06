package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.math.Fractal;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageManager implements Manager{
    protected ArrayList<CartesianScreenPlane> caughtPlanes = new ArrayList<>();
    private ArrayList<ImageIcon> icons = new ArrayList<>();
    private int prefWidth, prefHeight;

    protected Fractal fractal;
    protected Colorizer colorizer;

    public void addImageIcon(PlaneState planeState){
        var pS = planeState;
        caughtPlanes.add(new CartesianScreenPlane(
                prefWidth, prefHeight, pS.getXMin(), pS.getXMax(),
                pS.getYMin(), pS.getYMax()
        ));
        var currPlane = caughtPlanes.get(caughtPlanes.size()-1);
        var painter = new FractalPainter(currPlane, fractal);
        painter.col = colorizer;
        var readyImage = new BufferedImage(currPlane.getWidth(), currPlane.getHeight(), BufferedImage.TYPE_INT_RGB);
        var gRI = readyImage.getGraphics();
        gRI.drawImage(painter.getSavedImage(), 0, 0, null);
        icons.add(new ImageIcon(readyImage));
    }

    public void loadFractalData(Fractal fractal, Colorizer colorizer){
        this.fractal = fractal;
        this.colorizer = colorizer;
    }

    public ImageIcon getIcon(int index){
        return icons.get(index);
    }

    public void setPrefScreen(Dimension pref){
        prefWidth = pref.width;
        prefHeight = pref.height;
    }
}
