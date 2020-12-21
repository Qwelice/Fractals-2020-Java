package ru.smak.gui.menu;

import ru.smak.gui.graphics.FractalPainter;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.math.Fractal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {
    private String saveFileName = "D:\\myImage.png";

    private void setSaveFileName(String name){
        saveFileName = name;
    }

    private final JFileChooser fileChooser;

    private FractalPainter painter;
    private CartesianScreenPlane plane;
    private Fractal fractal;
    private Colorizer colorizer;

    public ImageSaver(CartesianScreenPlane plane, Fractal fractal, Colorizer colorizer){
        this.plane = plane;
        painter = new FractalPainter(plane, fractal);
        setColorizer(colorizer);
        fileChooser = new JFileChooser();
    }

    public void setFractal(Fractal fractal){
        this.fractal = fractal;
        painter.setFractal(fractal);
    }

    public void setColorizer(Colorizer colorizer){
        this.colorizer = colorizer;
        painter.col = colorizer;
    }

    public void saveImage(){
        fileChooser.showSaveDialog(null);
        try{
            var name = fileChooser.getSelectedFile().getPath();
            String format;
            if(name.endsWith(".png") || name.endsWith(".PNG")){
                setSaveFileName(name);
                format = "PNG";
            }
            else if(name.endsWith(".jpg") || name.endsWith(".JPG")){
                setSaveFileName(name);
                format = "JPEG";
            }
            else{
                JOptionPane.showMessageDialog(null, "Ошибка в имени файла!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException();
            }
            var img = getImage();
            try{
                ImageIO.write(img, format, new File(saveFileName));
            }catch(IOException ex){}
        }catch (NullPointerException exception){}
    }

    private BufferedImage getImage(){
        var image = painter.getSavedImage();
        var newImg = new BufferedImage(image.getWidth(), image.getHeight() + 30, BufferedImage.TYPE_INT_RGB);
        var gNI = newImg.getGraphics();
        gNI.drawImage(image, 0, 0, null);
        gNI.drawString("xMin: " + Math.round(plane.xMin * 100)/100., 30, newImg.getHeight() - 20);
        gNI.drawString("xMax: " + Math.round(plane.xMax * 100)/100., newImg.getWidth() - 60, newImg.getHeight() - 20);
        gNI.drawString("yMin: " + Math.round(plane.yMin * 100)/100., 30, newImg.getHeight() - 2);
        gNI.drawString("yMax: " + Math.round(plane.yMax * 100)/100., newImg.getWidth() - 60, newImg.getHeight() - 2);
        return newImg;
    }
}