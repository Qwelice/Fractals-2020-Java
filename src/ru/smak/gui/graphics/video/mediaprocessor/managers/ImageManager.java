package ru.smak.gui.graphics.video.mediaprocessor.managers;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.math.Fractal;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageManager extends Manager {

    @Override
    public void loadFractalData(Fractal fractal, Colorizer colorizer) {
        this.fractal = fractal;
        this.colorizer = colorizer;
    }

    @Override
    public void setPrefScreen(Dimension pref) {
        prefWidth = pref.width;
        prefHeight = pref.height;
    }

    public ImageIcon createImage(CartesianScreenPlane plane){
        var origWidth = plane.getWidth();
        var origHeight = plane.getHeight();
        plane.setWidth(prefWidth);
        plane.setHeight(prefHeight);
        var painter = new FractalPainter(plane, fractal);
        painter.col = colorizer;
        var readyImage = new BufferedImage(plane.getWidth(), plane.getHeight(), BufferedImage.TYPE_INT_RGB);
        var gRI = readyImage.getGraphics();
        gRI.drawImage(painter.getSavedImage(), 0, 0, null);
        plane.setWidth(origWidth);
        plane.setHeight(origHeight);
        return new ImageIcon(readyImage);
    }
}
