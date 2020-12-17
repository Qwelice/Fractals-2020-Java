package ru.smak.gui.graphics.video.mediaprocessor;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.video.mediaprocessor.managers.ImageManager;
import ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager.VideoManager;
import ru.smak.math.Fractal;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MediaProcessor {
    private ImageManager imageManager = null;
    private VideoManager videoManager = null;

    private final ArrayList<CartesianScreenPlane> planes = new ArrayList<>();

    public MediaProcessor(){
        imageManager = new ImageManager();
        videoManager = new VideoManager();
    }

    public void loadFractalData(Fractal fractal, Colorizer colorizer){
        imageManager.loadFractalData(fractal, colorizer);
        videoManager.loadFractalData(fractal, colorizer);
    }

    public void setVideoOutput(String name){
        videoManager.setOutputFileName(name);
    }

    public void setImageScreen(Dimension pref){
        imageManager.setPrefScreen(pref);
    }

    public void setVideoScreen(Dimension pref){
        videoManager.setPrefScreen(pref);
    }

    public void setScreens(Dimension prefImgDim, Dimension prefVideoDim){
        imageManager.setPrefScreen(prefImgDim);
        videoManager.setPrefScreen(prefVideoDim);
    }

    public void catchImage(CartesianScreenPlane plane){
        planes.add(new CartesianScreenPlane(
                plane.getWidth(), plane.getHeight(),
                plane.xMin, plane.xMax, plane.yMin, plane.yMax
        ));
    }

    public ImageIcon getImage(int index){
        return imageManager.createImage(planes.get(index));
    }

    public void createVideo(){
        if(planes.size() >= 2){
            videoManager.createVideo(planes);
        }
    }
}
