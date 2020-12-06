package ru.smak.gui.graphics;

import ru.smak.gui.graphics.components.GraphicsPanel;
import ru.smak.gui.graphics.video.VideoPanel;

import javax.swing.*;

public class CommonPanel extends JPanel {
    public final GraphicsPanel graphicsPanel;
    public final VideoPanel videoPanel;

    public CommonPanel(){
        graphicsPanel = new GraphicsPanel();
        videoPanel = new VideoPanel();

        GroupLayout groupLayout = new GroupLayout(this);

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addComponent(graphicsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(5)
                .addComponent(videoPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
        );
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(graphicsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(5)
                        .addComponent(videoPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(5)
        );
        setLayout(groupLayout);
    }
}
