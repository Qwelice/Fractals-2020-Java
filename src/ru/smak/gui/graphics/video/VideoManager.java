package ru.smak.gui.graphics.video;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.math.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoManager implements Manager{
    private int prefWidth, prefHeight;
    private ArrayList<CartesianScreenPlane> caughtPlanes = new ArrayList<>();
    private ArrayList<BufferedImage> frames = new ArrayList<>();
    private double frameRate = 25;
    private int videoTime = 15;

    private ArrayList<Thread> threads = new ArrayList<>();
    private ArrayList<FramesCreator> fCreators = new ArrayList<>();
    private int packingCount = 0;

    private String outputFileName = "D:\\myVideo.mp4";
    public void setOutputFileName(String fileName){
        if(!fileName.isEmpty())
            outputFileName = fileName;
    }

    private boolean workingOn = false;
    public boolean isWorking(){
        return workingOn;
    }

    private Fractal fractal;
    private Colorizer colorizer;

    public void createVideo(){
        prepareFrames();
        pack();
        encode();
    }

    private void prepareFrames(){
        if(caughtPlanes.size() > 0){
            workingOn = true;
            var creatorsCount = Runtime.getRuntime().availableProcessors();
            var imgCount = caughtPlanes.size();
            fCreators.clear();
            threads.clear();
            if(imgCount >= creatorsCount){
                for(int i = 0; i < creatorsCount; i++){
                    fCreators.add(new FramesCreator(i));
                    threads.add(new Thread(fCreators.get(i)));
                }
            }
            else{
                for(int i = 0; i < imgCount; i++){
                    fCreators.add(new FramesCreator(i));
                    threads.add(new Thread(fCreators.get(i)));
                }
            }
            var j = 0;
            for(int i = 0; i < caughtPlanes.size(); i++){
                if(j == fCreators.size())
                    j = 0;
                fCreators.get(j).addConsider(null);
                j++;
            }
            var r = 0;
            for(int i = 0; i < fCreators.size(); i++){
                var f = fCreators.get(i);
                var size = f.consider.size();
                for(int k = 0; k < size; k++){
                    var p = caughtPlanes.get(r);
                    f.consider.set(k, new CartesianScreenPlane(
                            p.getWidth(), p.getHeight(),
                            p.xMin, p.xMax, p.yMin, p.yMax
                    ));
                    if(i > 0){
                        f.consider.add(0, new CartesianScreenPlane(
                                caughtPlanes.get(r-1).getWidth(), caughtPlanes.get(r-1).getHeight(),
                                caughtPlanes.get(r-1).xMin, caughtPlanes.get(r-1).xMax,
                                caughtPlanes.get(r-1).yMin, caughtPlanes.get(r-1).yMax
                        ));
                    }
                    r++;
                }
            }

            for(var t : threads)
                t.start();

            try{
                for(var t : threads)
                    t.join();
            }catch (InterruptedException exception){

            }
        }
    }

    private void pack(){
        threads.clear();
        for(var f : fCreators){
            f.buffer.forEach(img->{
                var correctImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                var gCI = correctImg.getGraphics();
                gCI.drawImage(img, 0, 0, null);
                frames.add(correctImg);
            });
            f.buffer.clear();
        }
    }

    public void encode(){
        var frameCount = frameRate * videoTime;
        IMediaWriter writer = ToolFactory.makeWriter(outputFileName);
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                prefWidth, prefHeight);
        long startTime = System.nanoTime();
        frames.forEach(img->{
            writer.encodeVideo(0, img, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            try{
                Thread.sleep((long)(1000/frameCount));
            }catch (InterruptedException ex){

            }
        });
        writer.close();
        frames.clear();
    }

    public void addPlane(PlaneState plane){
        caughtPlanes.add(new CartesianScreenPlane(
                prefWidth, prefHeight, plane.getXMin(), plane.getXMax(),
                plane.getYMin(), plane.getYMax()
        ));
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

    class FramesCreator implements Runnable{
        private int index;
        private int countToDo;
        private ArrayList<CartesianScreenPlane> consider = new ArrayList<>();
        private ArrayList<BufferedImage> buffer = new ArrayList<>();

        public FramesCreator(int index){
            this.index = index;
        }

        public void addConsider(CartesianScreenPlane plane){
            consider.add(plane);
        }
        public void removeConsider(CartesianScreenPlane plane){
            consider.remove(plane);
        }

        public void fillPlane(CartesianScreenPlane filling, CartesianScreenPlane filler){
            filling.setWidth(filler.getWidth());
            filling.setHeight(filler.getHeight());
            filling.xMin = filler.xMin;
            filling.xMax = filler.xMax;
            filling.yMin = filler.yMin;
            filling.yMax = filler.yMax;
        }

        @Override
        public void run() {
            var frameCount = frameRate * videoTime;

            var plane = new CartesianScreenPlane(0, 0,
                    0, 0, 0, 0);
            var painter = new FractalPainter(plane, fractal);
            painter.col = colorizer;

            for(int i = 1; i < consider.size(); i++){
                var pPrev = consider.get(i-1);
                var pCurr = consider.get(i);
                fillPlane(plane, pPrev);
                var img = painter.getSavedImage();
                buffer.add(img);
                var dXMin = (pCurr.xMin - pPrev.xMin)/frameCount;
                var dXMax = (pCurr.xMax - pPrev.xMax)/frameCount;
                var dYMin = (pCurr.yMin - pPrev.yMin)/frameCount;
                var dYMax = (pCurr.yMax - pPrev.yMax)/frameCount;
                for(int j = 0; j < frameCount; j++){
                    plane.xMin += dXMin;
                    plane.xMax += dXMax;
                    plane.yMin += dYMin;
                    plane.yMax += dYMax;
                    img = painter.getSavedImage();
                    buffer.add(img);
                }
            }
        }
    }
}
