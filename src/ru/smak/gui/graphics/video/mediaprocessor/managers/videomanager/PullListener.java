package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

public interface PullListener {
    void timeToPull(CartesianScreenPlane base);
}
