package ru.smak.gui.graphics;

import ru.smak.gui.graphics.components.GraphicsPanel;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.coordinates.Converter;
import ru.smak.gui.graphics.fractalcolors.ColorScheme2;
import ru.smak.math.Fractal;
import ru.smak.math.Julia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JuliaFrame extends JFrame {
    GraphicsPanel mainPanel;
    Julia julia;
    CartesianScreenPlane plane;

    static final Dimension MIN_SIZE = new Dimension(450, 350);
    static final Dimension MIN_FRAME_SIZE = new Dimension(600, 500);

    public JuliaFrame(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(MIN_FRAME_SIZE);
        setTitle("Фракталы");

        mainPanel = new GraphicsPanel();

        julia = new Julia();

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
        plane = new CartesianScreenPlane(
                mainPanel.getWidth(),
                mainPanel.getHeight(),
                -1.5, 1.5, -1, 1
        );
        var c = new ColorScheme2();
        var fp = new FractalPainter(plane, julia);
        fp.col = c;
        fp.addFinishedListener(new FinishedListener() {
            @Override
            public void finished() {
                mainPanel.repaint();
            }
        });
        mainPanel.addPainter(fp);
        var sp = new SelectionPainter(mainPanel.getGraphics());

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                plane.setWidth(mainPanel.getWidth());
                plane.setHeight(mainPanel.getHeight());
                sp.setGraphics(mainPanel.getGraphics());
                mainPanel.repaint();
            }
        });
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(e.getButton() == MouseEvent.BUTTON1){
                    sp.setVisible(true);
                    sp.setStartPoint(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(e != null && e.getButton() == MouseEvent.BUTTON1){
                    sp.setVisible(false);
                    var r = sp.getSelectionRect();
                    if(r != null && (r.width > 3 && r.height > 3)){
                        var xMin = Converter.xScr2Crt(r.x, plane);
                        var xMax = Converter.xScr2Crt(r.x+r.width, plane);
                        var yMin  = Converter.yScr2Crt(r.y+r.height, plane);
                        var yMax = Converter.yScr2Crt(r.y, plane);
                        plane.xMin = xMin;
                        plane.xMax = xMax;
                        plane.yMin = yMin;
                        plane.yMax = yMax;
                        mainPanel.repaint();
                    }
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

    public void setJuliaParams(int x, int y){
        var dx = Converter.xScr2Crt(x, plane);
        var dy = Converter.yScr2Crt(y, plane);
        setJuliaParams(dx, dy);
    }
    public void setJuliaParams(double x, double y){
        julia.setC(x, y);
        mainPanel.repaint();
    }
}
