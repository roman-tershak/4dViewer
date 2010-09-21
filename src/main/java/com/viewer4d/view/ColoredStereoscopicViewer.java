package com.viewer4d.view;

import java.awt.Color;

import com.viewer4d.geometry.Edge;


public class ColoredStereoscopicViewer extends StereoscopicViewer {

    public ColoredStereoscopicViewer() {
    }
    
    @Override
    protected Color getColor(Edge edge) {
        double w1 = edge.getA().getCoords()[3];
        double w2 = edge.getB().getCoords()[3];
        return ColoredMonoscopicViewer.getColorProportionally(w1, w2);
    }

    @Override
    protected Color getColorSelected(Edge edge) {
        return SELECTED_COLORED_COLOR;
    }

}
