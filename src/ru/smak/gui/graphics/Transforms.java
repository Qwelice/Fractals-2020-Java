package ru.smak.gui.graphics;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;

import java.util.ArrayDeque;

public class Transforms {
    private static final ArrayDeque<CartesianScreenPlane> areas = new ArrayDeque<>();
    private static CartesianScreenPlane base = new CartesianScreenPlane(0, 0, 0, 0, 0, 0);
    private static int areasLimit = 10;
    private static ProportionsSaver saver;

    public static void setLimit(int lim){
        areasLimit = lim;
    }

    public static void addArea(CartesianScreenPlane plane){
        if(areas.size() == 9)
            areas.removeFirst();
        areas.add(new CartesianScreenPlane(plane.getWidth(), plane.getHeight(), plane.xMin, plane.xMax, plane.yMin, plane.yMax));
    }

    public static void compose(CartesianScreenPlane plane){
        base.setWidth(plane.getWidth());
        base.setHeight(plane.getHeight());
        base.xMin = plane.xMin;
        base.xMax = plane.xMax;
        base.yMin = plane.yMin;
        base.yMax = plane.yMax;
        saver = new ProportionsSaver(plane, plane.getWidth(), plane.getHeight());
        saver.setSaving(true);
    }

    public static void executeLast(CartesianScreenPlane plane){
        if(areas.size() > 0){
            var a = areas.pollLast();
            saver.maintain(plane, a.xMin, a.xMax, a.yMin, a.yMax);
        }
    }

    public static void toHome(CartesianScreenPlane plane){
        saver.maintain(plane, base.xMin, base.xMax, base.yMin, base.yMax);
        if(areas.size() > 0)
            areas.clear();
    }
}
