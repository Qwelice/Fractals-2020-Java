package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.fractalcolors.Colorizer;
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

    private final ImageManager imageManager;
    private final VideoManager videoManager;

    private final ArrayList<CatchListener> catchListeners = new ArrayList<>();

    public VideoPanel(){
        setVisible(false);
        // Настройка списка, в котором будут храниться захваченные изображения
        dlm = new DefaultListModel<>();
        images = new JList(dlm);
        content = new JScrollPane(images);
        buttonsPanel = new ButtonsPanel();

        imageManager = new ImageManager();
        videoManager = new VideoManager();

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
                notifyCatchListeners(imageManager, videoManager);
                dlm.add(dlm.size(), imageManager.getIcon(dlm.size()));
            }
        });
        buttonsPanel.recordVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dlm.size() >= 2)
                    videoManager.createVideo();
            }
        });
    }

    public void setData(Fractal fractal, Colorizer colorizer){
        imageManager.loadFractalData(fractal, colorizer);
        imageManager.setPrefScreen(new Dimension(
                content.getWidth() - 5,
                (int)(0.45 * content.getHeight())
        ));
        videoManager.loadFractalData(fractal, colorizer);
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
    public void notifyCatchListeners(ImageManager iManager, VideoManager videoManager){
        for(var l : catchListeners)
            l.timeToCatch(iManager, videoManager);
    }
}
