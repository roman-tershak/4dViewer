package com.viewer4d.view;

import java.awt.Color;

import com.viewer4d.geometry.Edge;
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
    protected Color getColor(Edge edge) {
        double w1 = edge.getA().getCoords()[3];
        double w2 = edge.getB().getCoords()[3];
        return getColorProportionally(w1, w2);
    }

    @Override
    protected Color getColorSelected(Edge edge) {
        return SELECTED_COLORED_COLOR;
    }

    public static Color getColorProportionally(double w1, double w2) {
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

    public static Color getColorBackwardProportionally(double w1, double w2) {
        Color color;
        if (Math.abs(w1 - 0) > Pointable.PRECISION || Math.abs(w2 - 0) > Pointable.PRECISION) {
            int wc = (int) (150 / w1);
            int mainC = (int) ((wc > 255 || wc < -255 ? 255 : Math.abs(wc)) * 0.75);
            int oppC1 = (int) (mainC * 0.67);
            int oppC2 = (int) (mainC * 0.33);
            if (wc < 0) {
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
