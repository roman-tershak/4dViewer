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
import com.viewer4d.geometry.simple.Point;

public abstract class AbstractViewer implements Viewer {

    static final Point XT = new Point(0, 1, 0);
    static final Point YT = new Point(0, 0, 1);
    static final Point ZT = new Point(-1, 0, 0);
    static final Point CENTRUM = new Point(0, 0, 0);
    
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
        double[] aCoords = edge.getA().getCoords();
        double[] bCoords = edge.getB().getCoords();
        
        int x1 = centerX + (int) (aCoords[0] * ratio);
        int y1 = centerY - (int) (aCoords[1] * ratio);
        int x2 = centerX + (int) (bCoords[0] * ratio);
        int y2 = centerY - (int) (bCoords[1] * ratio);
        
        Color c1;
        Color c2;
        if (edge.isSelected()) {
            c1 = getColorSelected(edge);
            c2 = getColorSelected(edge);
        } else {
            c1 = getColor(edge);
            c2 = getColor(edge);

        }
        
        GradientPaint gradientPaint = new GradientPaint(
                x1, y1, c1, x2, y2, c2);
        g2d.setPaint(gradientPaint);
        
        g2d.drawLine(x1, y1, x2, y2);
    }

    protected abstract Color getColor(Edge edge);
    
    protected abstract Color getColorSelected(Edge edge);

    public double checkAltitudeLimit(double deltaAltitude, double currAltitude) {
        if (currAltitude + deltaAltitude > ALTITUDE_LIMIT) {
            deltaAltitude = ALTITUDE_LIMIT - currAltitude;
        } else if (currAltitude + deltaAltitude < -ALTITUDE_LIMIT) {
            deltaAltitude = -ALTITUDE_LIMIT - currAltitude;
        }
        return deltaAltitude;
    }
}
