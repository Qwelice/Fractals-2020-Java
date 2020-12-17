package ru.smak.gui;

import ru.smak.gui.graphics.CommonPanel;
import ru.smak.gui.graphics.painters.FinishedListener;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.gui.graphics.painters.SelectionPainter;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.coordinates.Converter;
import ru.smak.gui.graphics.fractalcolors.ColorScheme2;
import ru.smak.gui.graphics.video.mediaprocessor.CatchListener;
import ru.smak.gui.graphics.video.mediaprocessor.MediaProcessor;
import ru.smak.math.Mandelbrot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    //GraphicsPanel mainPanel;
    CommonPanel commonPanel;

    static final Dimension MIN_SIZE = new Dimension(450, 350);
    static final Dimension MIN_FRAME_SIZE = new Dimension(720, 500);

    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(MIN_FRAME_SIZE);
        setTitle("Фракталы");

        //mainPanel = new GraphicsPanel();
        commonPanel =  new CommonPanel();

        commonPanel.graphicsPanel.setBackground(Color.WHITE);
        //mainPanel.setBackground(Color.WHITE);

        var button = new JButton("Открыть");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commonPanel.videoPanel.changeVisible();
                commonPanel.graphicsPanel.repaint();
            }
        });

        GroupLayout gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
                .addComponent(commonPanel, (int)(MIN_SIZE.height*0.8), MIN_SIZE.height, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addGroup(gl.createParallelGroup()
                        .addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(4)
                        .addComponent(commonPanel, MIN_SIZE.width, MIN_SIZE.width, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(4)
        );
        pack();
        var plane = new CartesianScreenPlane(
                commonPanel.graphicsPanel.getWidth(),
                commonPanel.graphicsPanel.getHeight(),
                -2, 1, -1, 1
        );

        var m = new Mandelbrot();
        var c = new ColorScheme2();
        var fp = new FractalPainter(plane, m);
        fp.col = c;
        fp.addFinishedListener(new FinishedListener() {
            @Override
            public void finished() {
                commonPanel.graphicsPanel.repaint();
            }
        });
        commonPanel.graphicsPanel.addPainter(fp);
        var sp = new SelectionPainter(commonPanel.graphicsPanel.getGraphics());

        commonPanel.graphicsPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                plane.setWidth(commonPanel.graphicsPanel.getWidth());
                plane.setHeight(commonPanel.graphicsPanel.getHeight());
                sp.setGraphics(commonPanel.graphicsPanel.getGraphics());
                commonPanel.graphicsPanel.repaint();
            }
        });
        commonPanel.graphicsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                sp.setVisible(true);
                sp.setStartPoint(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                sp.setVisible(false);
                var r = sp.getSelectionRect();
                var xMin = Converter.xScr2Crt(r.x, plane);
                var xMax = Converter.xScr2Crt(r.x+r.width, plane);
                var yMin  = Converter.yScr2Crt(r.y+r.height, plane);
                var yMax = Converter.yScr2Crt(r.y, plane);
                plane.xMin = xMin;
                plane.xMax = xMax;
                plane.yMin = yMin;
                plane.yMax = yMax;
                commonPanel.graphicsPanel.repaint();
            }
        });

        commonPanel.graphicsPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                sp.setCurrentPoint(e.getPoint());
            }
        });

        commonPanel.videoPanel.addCatchListener(new CatchListener() {
            @Override
            public void timeToCatch(MediaProcessor mediaProcessor) {
                commonPanel.videoPanel.setData(m, c);

                mediaProcessor.catchImage(plane);
                mediaProcessor.setVideoScreen(new Dimension(MIN_FRAME_SIZE));
            }
        });
    }
}
