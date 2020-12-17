package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

import java.awt.image.BufferedImage;

public class MediaTools {
    public static void fillPlane(CartesianScreenPlane filling, CartesianScreenPlane filler){
        filling.setWidth(filler.getWidth());
        filling.setHeight(filler.getHeight());
        filling.xMin = filler.xMin;
        filling.xMax = filler.xMax;
        filling.yMin = filler.yMin;
        filling.yMax = filler.yMax;
    }

    public static void fillPlane(CartesianScreenPlane filling, CartesianScreenPlane filler, int prefWidth, int prefHeight){
        filling.setWidth(prefWidth);
        filling.setHeight(prefHeight);
        filling.xMin = filler.xMin;
        filling.xMax = filler.xMax;
        filling.yMin = filler.yMin;
        filling.yMax = filler.yMax;
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType){
        BufferedImage image;
        if(sourceImage.getType() == targetType)
            return sourceImage;
        image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
        var gI = image.getGraphics();
        gI.drawImage(sourceImage, 0, 0, null);
        return image;
    }
}
