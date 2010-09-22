package com.viewer4d.projector.auxiliary;

import static com.viewer4d.geometry.simple.Pointable.PRECISION;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Face;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.projector.AbstractEnablingProjector;

public class Simple3DSpaceIntersectorAtZeroW extends AbstractEnablingProjector {

    public static final double ZERO_W_DEFAULT = 0.0;
    
    private double w = ZERO_W_DEFAULT;
    
    private Map<Face, Set<Point>> facesInterPoints = new HashMap<Face, Set<Point>>();
    private Map<Point, Vertex> pointVertices = new HashMap<Point, Vertex>();
    
    @Override
    protected Figure projectFigure(Figure figure) {
        facesInterPoints.clear();

        for (Edge edge : figure.getEdges()) {
            addEdgeInterPoints(edge);
        }

        return new FigureBaseImpl(null, formEdgeSet());
    }

    private void addEdgeInterPoints(Edge edge) {
        Point[] interPoints = getEdgeInterPoints(edge);
        
        if (interPoints == null) {
            return;
        }
        
        Set<Face> edgeFaces = edge.getFaces();
        if (edgeFaces.size() == 0) {
            edgeFaces.add(new Face((Set<Edge>) null));
        }
        
        for (Face face : edgeFaces) {
            Set<Point> faceInterPointsSet = facesInterPoints.get(face);
            if (faceInterPointsSet == null) {
                faceInterPointsSet = new HashSet<Point>();
                facesInterPoints.put(face, faceInterPointsSet);
            }
            if (interPoints[0] != null) {
                faceInterPointsSet.add(interPoints[0]);
            }
            if (interPoints[1] != null) {
                faceInterPointsSet.add(interPoints[1]);
            }
        }
    }
    
    private Point[] getEdgeInterPoints(Edge edge) {
        MovablePoint a = edge.getA();
        MovablePoint b = edge.getB();
        double[] aCoords = a.getCoords();
        double[] bCoords = b.getCoords();
        
        double aw = aCoords[3];
        double bw = bCoords[3];
        
        double ndw = w - PRECISION;
        double pdw = w + PRECISION;
        
        if (aw < ndw && bw < ndw || aw > pdw && bw > pdw) {
            return null;
        }
        
        Point[] result = {null, null};
        
        double ax = aCoords[0], ay = aCoords[1], az = aCoords[2];
        double bx = bCoords[0], by = bCoords[1], bz = bCoords[2];
        
        if (aw < ndw && bw > pdw || bw < ndw && aw > pdw ) {
            double awm = Math.abs(aw);
            double bwm = Math.abs(bw);
            double nx = ax + awm * (bx - ax)/(awm + bwm);
            double ny = ay + awm * (by - ay)/(awm + bwm);
            double nz = az + awm * (bz - az)/(awm + bwm);
            result[0] = new Point(nx, ny, nz);
        } else {
            if (aw >= ndw && aw <= pdw) {
                result[0] = new Point(aCoords);
            }
            if (bw >= ndw && bw <= pdw) {
                result[1] = new Point(bCoords);
            }
        }
        return result;
    }

    private Set<Edge> formEdgeSet() {
        Set<Edge> result = new HashSet<Edge>();
        
        pointVertices.clear();
        
        for (Map.Entry<Face, Set<Point>> faceEntry : facesInterPoints.entrySet()) {
            Face face = faceEntry.getKey();
            Set<Point> interPoints = faceEntry.getValue();
            
            int size = interPoints.size();
            if (size == 2) {
                Iterator<Point> it = interPoints.iterator();
                
                result.add(new Edge(
                        getVertexForPoint(it.next()),
                        getVertexForPoint(it.next())));
            } else if (size > 2) {
                addEntireFace(face, result);
            }
        }
        return result;
    }

    private void addEntireFace(
            Face face, Set<Edge> edges) {
        for (Edge edge : face.getEdges()) {
            Point a = new Point(edge.getA().getCoords());
            Point b = new Point(edge.getB().getCoords());
            
            edges.add(new Edge(
                    getVertexForPoint(a),
                    getVertexForPoint(b)));
        }
    }

    private Vertex getVertexForPoint(Point point) {
        Vertex vertex = pointVertices.get(point);
        if (vertex == null) {
            vertex = new Vertex(point.getCoords());
            pointVertices.put(point, vertex);
        }
        return vertex;
    }

}
