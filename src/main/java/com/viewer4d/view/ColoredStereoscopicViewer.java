package com.viewer4d.view;

import java.awt.Color;

import com.viewer4d.geometry.Vertex;


public class ColoredStereoscopicViewer extends StereoscopicViewer {

    public ColoredStereoscopicViewer() {
    }
    
    @Override
    protected Color getColor(Vertex vertex) {
        return ColoredMonoscopicViewer.getColorProportionally(vertex.getCoords()[3]);
    }

    @Override
    protected Color getColorSelected(Vertex vertex) {
        return SELECTED_COLORED_COLOR;
    }

}
