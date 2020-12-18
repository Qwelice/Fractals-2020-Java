package ru.smak.gui;

import ru.smak.gui.graphics.*;
import ru.smak.gui.graphics.components.GraphicsPanel;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.coordinates.Converter;
import ru.smak.gui.graphics.fractalcolors.ColorScheme2;
import ru.smak.math.Mandelbrot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    GraphicsPanel mainPanel;

    static final Dimension MIN_SIZE = new Dimension(450, 350);
    static final Dimension MIN_FRAME_SIZE = new Dimension(600, 500);
    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(MIN_FRAME_SIZE);
        setTitle("Фракталы");

        mainPanel = new GraphicsPanel();

        mainPanel.setBackground(Color.WHITE);

        GroupLayout gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addComponent(mainPanel, (int)(MIN_SIZE.height*0.8), MIN_SIZE.height, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addGroup(gl.createParallelGroup()
                        .addComponent(mainPanel, MIN_SIZE.width, MIN_SIZE.width, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(4)
        );
        pack();
        var plane = new CartesianScreenPlane(
                mainPanel.getWidth(),
                mainPanel.getHeight(),
                -2, 1, -1, 1
        );

        var m = new Mandelbrot();
        var c = new ColorScheme2();


        var fp = new FractalPainter(plane, m);
        fp.col = c;
        fp.addFinishedListener(new FinishedListener() {
            @Override
            public void finished() {
                mainPanel.repaint();
            }
        });
        mainPanel.addPainter(fp);
        var sp = new SelectionPainter(mainPanel.getGraphics());
        ProportionsSaver saver = new ProportionsSaver(plane, mainPanel.getWidth(), mainPanel.getHeight());
        Transforms.compose(plane);
        saver.setSaving(true);

        mainPanel.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                saver.maintain(plane, mainPanel.getWidth(), mainPanel.getHeight());
                plane.setWidth(mainPanel.getWidth());
                plane.setHeight(mainPanel.getHeight());
                sp.setGraphics(mainPanel.getGraphics());
                mainPanel.repaint();
            }
        });
        boolean readyTo = false;
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z){
                    Transforms.executeLast(plane);
                    mainPanel.repaint();
                }
                if(e.getKeyCode() == KeyEvent.VK_H){
                    Transforms.toHome(plane);
                    saver.maintain(plane, mainPanel.getWidth(), mainPanel.getHeight());
                    mainPanel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        mainPanel.addMouseListener(new MouseAdapter() {
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
                if(r != null && r.width > 3 && r.height > 3){
                    var xMin = Converter.xScr2Crt(r.x,plane);
                    var xMax = Converter.xScr2Crt(r.x+r.width,plane);
                    var yMin = Converter.yScr2Crt(r.y+r.height,plane);
                    var yMax = Converter.yScr2Crt(r.y,plane);
                    Transforms.addArea(plane);
                    saver.maintain(plane, xMin, xMax, yMin, yMax);
                    mainPanel.repaint();
                }
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                sp.setCurrentPoint(e.getPoint());
            }
        });
    }
}
