package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.video.mediaprocessor.CatchListener;
import ru.smak.gui.graphics.video.mediaprocessor.MediaProcessor;
import ru.smak.math.Fractal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VideoPanel extends JPanel {
    private final JScrollPane content;
    private final JList images;
    private final DefaultListModel<ImageIcon> dlm;
    private final ButtonsPanel buttonsPanel;

    private final MediaProcessor mediaProcessor;

    private final JFileChooser fileChooser;

    private final ArrayList<CatchListener> catchListeners = new ArrayList<>();

    public VideoPanel(){
        setVisible(false);
        // Настройка списка, в котором будут храниться захваченные изображения
        dlm = new DefaultListModel<>();
        images = new JList(dlm);
        content = new JScrollPane(images);
        buttonsPanel = new ButtonsPanel();

        mediaProcessor = new MediaProcessor();

        fileChooser = new JFileChooser();

        GroupLayout groupLayout = new GroupLayout(this);

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(content, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(5)
                        .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(5)
        );
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addComponent(content, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(5)
                .addComponent(buttonsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(5)
        );
        setLayout(groupLayout);
        buttonsPanel.catchImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyCatchListeners(mediaProcessor);
                dlm.add(dlm.size(), mediaProcessor.getImage(dlm.size()));
            }
        });
        buttonsPanel.recordVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dlm.size() >= 2)
                    mediaProcessor.createVideo();
            }
        });
        buttonsPanel.getOutputPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.showSaveDialog(VideoPanel.this);
                try{
                    var name = fileChooser.getSelectedFile().getPath();
                    mediaProcessor.setVideoOutput(name);
                }catch (NullPointerException exception){}
            }
        });
    }

    public void setData(Fractal fractal, Colorizer colorizer){
        mediaProcessor.loadFractalData(fractal, colorizer);
        mediaProcessor.setImageScreen(new Dimension(
                content.getWidth() - 5,
                (int)(0.45 * content.getHeight())
        ));
    }

    public void changeVisible(){
        setVisible(!isVisible());
    }

    public void addCatchListener(CatchListener listener){
        catchListeners.add(listener);
    }
    public void removeCatchListener(CatchListener listener){
        catchListeners.remove(listener);
    }
    public void notifyCatchListeners(MediaProcessor mediaProcessor){
        for(var l : catchListeners)
            l.timeToCatch(mediaProcessor);
    }
}
