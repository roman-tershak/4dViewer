package com.viewer4d.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Selection;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;

public abstract class AbstractViewer implements Viewer {

    protected static final Point XT = new Point(0, 1, 0);
    protected static final Point YT = new Point(0, 0, 1);
    protected static final Point ZT = new Point(-1, 0, 0);
    protected static final Point CENTRUM = new Point(0, 0, 0);
    
    protected static final int DISTANCE_BASE = 4;
    protected static final int OPP_COLOR_BASE = 4;
    
    private boolean colored;
    
    public AbstractViewer(boolean colored) {
        this.colored = colored;
    }
    
    public boolean isColored() {
        return colored;
    }
    public void setColored(boolean colored) {
        this.colored = colored;
    }
    
    protected void paintFigure(Figure figure, int cx, int cy, double ratio, Graphics2D g2d) {
        Collection<Edge> edges = figure.getEdges();
        
        if (edges.size() < 100) {
            ArrayList<Edge> edgesToSort = new ArrayList<Edge>(edges);
            Collections.sort(edgesToSort, new Comparator<Edge>() {
                @Override
                public int compare(Edge edge1, Edge edge2) {
                    double[] a1Coords = edge1.getA().getCoords();
                    double[] b1Coords = edge1.getB().getCoords();
                    double z1 = a1Coords[2] <= b1Coords[2] ? a1Coords[2] : b1Coords[2];

                    double[] a2Coords = edge2.getA().getCoords();
                    double[] b2Coords = edge2.getB().getCoords();
                    double z2 = a2Coords[2] <= b2Coords[2] ? a2Coords[2] : b2Coords[2];

                    if (z1 < z2) {
                        return 1;
                    } else if (z1 > z2) {
                        return -1;
                    } else {
                        double w1 = a1Coords[3] <= b1Coords[3] ? a1Coords[3] : b1Coords[3];
                        double w2 = a2Coords[3] <= b2Coords[3] ? a2Coords[3] : b2Coords[3];
                        return w1 <= w2 ? 1 : 0;
                    }
                }
            });
            edges = edgesToSort;
        }
        
        for (Edge edge : edges) {
            paintEdge(edge, cx, cy, ratio, g2d);
        }
    }

    protected void paintEdge(Edge edge, int centerX, int centerY, double ratio, Graphics2D g2d) {
        Vertex a = edge.getA();
        Vertex b = edge.getB();
        double[] aCoords = a.getCoords();
        double[] bCoords = b.getCoords();
        
        int x1 = centerX + (int) (aCoords[0] * ratio);
        int y1 = centerY - (int) (aCoords[1] * ratio);
        int x2 = centerX + (int) (bCoords[0] * ratio);
        int y2 = centerY - (int) (bCoords[1] * ratio);
        
        Selection edgeSelection = edge.getSelection();
        Color c1 = getColor(a, edgeSelection);
        Color c2 = getColor(b, edgeSelection);
        
        GradientPaint gradientPaint = new GradientPaint(
                x1, y1, c1, x2, y2, c2);
        g2d.setPaint(gradientPaint);
        
        g2d.drawLine(x1, y1, x2, y2);
    }

    protected Color getColor(Vertex vertex, Selection selection) {
        if (isColored()) {
            switch (selection) {
            case NOTSELECTED:
                return getColorProportionally(vertex.getCoords()[3]);
            case SELECTED1:
                return SELECTED_COLORED_COLOR_1;
            case SELECTED2:
                return SELECTED_COLORED_COLOR_2;
            default:
                throw new IllegalArgumentException(" - " + selection);
            }
        } else {
            switch (selection) {
            case NOTSELECTED:
                return PAINT_BW_COLOR;
            case SELECTED1:
                return SELECTED_BW_COLOR_1;
            case SELECTED2:
                return SELECTED_BW_COLOR_2;
            default:
                throw new IllegalArgumentException(" - " + selection);
            }
        }
    }
    
    protected static double checkAltitudeLimit(double deltaAltitude, double currAltitude) {
        if (currAltitude + deltaAltitude > ALTITUDE_LIMIT) {
            deltaAltitude = ALTITUDE_LIMIT - currAltitude;
        } else if (currAltitude + deltaAltitude < -ALTITUDE_LIMIT) {
            deltaAltitude = -ALTITUDE_LIMIT - currAltitude;
        }
        return deltaAltitude;
    }

    protected static Color getColorProportionally(double w1) {
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
