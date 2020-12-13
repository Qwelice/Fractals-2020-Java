package ru.smak.gui.graphics.video.mediaprocessor.managers;

import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.math.Fractal;

import java.awt.*;

public abstract class Manager {
    protected int prefWidth, prefHeight;
    protected Fractal fractal;
    protected Colorizer colorizer;

    protected abstract void loadFractalData(Fractal fractal, Colorizer colorizer);
    protected abstract void setPrefScreen(Dimension pref);
}
