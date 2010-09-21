package com.viewer4d.view;

import java.awt.Color;

import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Pointable;


public class ColoredMonoscopicViewer extends MonoscopicViewer {

    static final int MAIN_COLOR_COEF = 108;
    static final int OPP1_COLOR_COEF = 92;
    static final int OPP2_COLOR_COEF = 80;
    
    static final int DISTANCE_BASE = 4;
    static final int OPP_COLOR_BASE = 4;
    
    public ColoredMonoscopicViewer() {
    }
    
    @Override
    protected Color getColor(Vertex vertex) {
        return getColorProportionally(vertex.getCoords()[3]);
    }

    @Override
    protected Color getColorSelected(Vertex vertex) {
        return SELECTED_COLORED_COLOR;
    }

    public static Color getColorProportionally(double w1) {
        Color color;
        if (Math.abs(w1 - 0) > Pointable.PRECISION) {
            double colorOppCoef = OPP_COLOR_BASE / (Math.abs(w1) + OPP_COLOR_BASE);
            double colorMainCoef = 2 - colorOppCoef;
            double distCoef = DISTANCE_BASE / (Math.abs(w1) + DISTANCE_BASE);
            int mainC = (int) (MAIN_COLOR_COEF * colorMainCoef * distCoef);
            int oppC1 = (int) (OPP1_COLOR_COEF * colorOppCoef * distCoef);
            int oppC2 = (int) (OPP2_COLOR_COEF * colorOppCoef * distCoef);
            if (w1 < 0) {
                color = new Color(mainC, oppC1, oppC2);
            } else {
                color = new Color(oppC2, oppC1, mainC);
            }
        } else {
            color = ZERO_W_COLOR;
        }
        return color;
    }

}
