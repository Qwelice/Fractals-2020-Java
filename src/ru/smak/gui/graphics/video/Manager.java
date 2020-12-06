package ru.smak.gui.graphics.video;

import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.math.Fractal;

import java.awt.*;

public interface Manager {
    void loadFractalData(Fractal fractal, Colorizer colorizer);
    void setPrefScreen(Dimension pref);
}
