package ru.smak.gui.graphics.video;

import javax.swing.*;

public class ButtonsPanel extends JPanel {
    public final JButton getOutputPath;
    public final JButton catchImage;
    public final JButton recordVideo;

    public ButtonsPanel(){
        // Кнопки взаимодействия
        getOutputPath = new JButton("Выбрать папку");
        catchImage = new JButton("Захватить");
        recordVideo = new JButton("Запись");

        GroupLayout groupLayout = new GroupLayout(this);

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addComponent(getOutputPath, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(4)
                .addComponent(catchImage, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(4)
                .addComponent(recordVideo, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(5)
        );
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(5)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(getOutputPath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(4)
                        .addComponent(catchImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(4)
                        .addComponent(recordVideo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(5)
        );
        setLayout(groupLayout);
    }
}
