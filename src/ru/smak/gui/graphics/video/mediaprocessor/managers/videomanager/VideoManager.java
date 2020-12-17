package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.gui.graphics.video.mediaprocessor.managers.Manager;
import ru.smak.math.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoManager extends Manager {
    private ArrayList<CartesianScreenPlane> planes;
    private final ArrayDeque<BufferedImage> pictures = new ArrayDeque<>();

    private String outputFileName = "D:\\myVideo.mp4";

    private int videoTime = 10;
    private int frameRate = 30;

    private boolean consumerStop = false;

    public void consumerStop(){
        consumerStop = true;
    }

    private Thread consumer = null;
    private Thread loader = null;

    public void setOutputFileName(String name){
        if(!name.isEmpty() && name.endsWith(".mp4"))
            outputFileName = name;
    }

    public void setVideoTime(int videoTime){
        this.videoTime = videoTime;
    }

    public void setFrameRate(int frameRate){
        this.frameRate = frameRate;
    }

    public void createVideo(ArrayList<CartesianScreenPlane> planes){
        pack(planes);
    }

    private void pack(ArrayList<CartesianScreenPlane> planes){
        this.planes = planes;
        constructConsumer();
        consumer.start();
        constructLoader();
        loader.start();
    }

    private void constructConsumer(){
        consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                IMediaWriter writer = ToolFactory.makeWriter(outputFileName);
                writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, prefWidth, prefHeight);
                long nextFrameTime = 0;
                long dt = Global.DEFAULT_TIME_UNIT.convert(frameRate, TimeUnit.MILLISECONDS);
                ArrayList<BufferedImage> readyImg = new ArrayList<>();
                System.out.println("Encoding is started");
                while(!consumerStop){
                    synchronized (pictures){
                        if(pictures.size() > 0 && pictures.size() % frameRate != 0){
                            try{
                                pictures.wait();
                            }catch (InterruptedException exception){}
                        }
                        readyImg.clear();
                        while(pictures.size() > 0){
                            var img = pictures.poll();
                            readyImg.add(img);
                        }
                        pictures.notify();
                    }
                    for(var img : readyImg){
                        var corrImg = MediaTools.convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
                        var gCI = corrImg.getGraphics();
                        gCI.drawImage(img, 0, 0, null);
                        writer.encodeVideo(0, corrImg, nextFrameTime, Global.DEFAULT_TIME_UNIT);
                        nextFrameTime += dt;
                    }
                }
                writer.close();
                System.out.println("Encoding is done!");
            }
        });
    }

    private void constructLoader(){
        loader = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder strB = new StringBuilder();
                var framesCount = videoTime * frameRate;
                var prev = new CartesianScreenPlane(0, 0, 0, 0, 0, 0);
                var painter = new FractalPainter(prev, fractal);
                painter.col = colorizer;
                for(int i = 1; i < planes.size(); i++){
                    if(strB.length() > 0)
                        strB.delete(0, strB.length());
                    MediaTools.fillPlane(prev, planes.get(i-1), prefWidth, prefHeight);
                    var dXMin = (planes.get(i).xMin - prev.xMin ) / (1.0 * framesCount);
                    var dXMax = (planes.get(i).xMax - prev.xMax) / (1.0 * framesCount);
                    var dYMin = (planes.get(i).yMin - prev.yMin) / (1.0 * framesCount);
                    var dYMax = (planes.get(i).yMax - prev.yMax) / (1.0 * framesCount);
                    strB.append("New plane progress: |");
                    System.out.println(strB.toString());
                    for(int j = 0; j < framesCount; j++){
                        if(pictures.size() >= frameRate){
                            synchronized (pictures){
                                pictures.notify();
                                strB.append("=");
                                System.out.println(strB.toString());
                                try{
                                    pictures.wait();
                                }catch (InterruptedException exception){}
                            }
                        }
                        var img = painter.getSavedImage();
                        synchronized (pictures){
                            pictures.add(img);
                        }
                        prev.xMin += dXMin;
                        prev.xMax += dXMax;
                        prev.yMin += dYMin;
                        prev.yMax += dYMax;
                    }
                    strB.append("|\t : Complete!");
                    System.out.println(strB.toString());
                }
                consumerStop();
                synchronized (pictures){
                    pictures.notify();
                }
            }
        });
    }

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
}
