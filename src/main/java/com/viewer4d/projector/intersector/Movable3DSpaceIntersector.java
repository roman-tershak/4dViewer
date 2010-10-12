package com.viewer4d.projector.intersector;

import java.util.HashSet;
import java.util.Set;

import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.Transformer4D;
import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.Vertex;
import com.viewer4d.geometry.figure.impl.FigureBaseImpl;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;

public class Movable3DSpaceIntersector extends Simple3DSpaceIntersectorWPoint implements Movable {

    private final Transformer4D transformer4d;
    
    public Movable3DSpaceIntersector(boolean enabled) {
        super(enabled, ZERO_W_DEFAULT);
        transformer4d = new Transformer4D(UNIT_VECTORS.W, true, 0, Point.ZERO);
    }
    
    @Override
    protected Figure projectFigure(Figure figure) {
        Figure projectedFigure = super.projectFigure(figure);
        
        Set<Edge> newEdges = new HashSet<Edge>();
        for (Edge edge : projectedFigure.getEdges()) {
            Vertex a = new Vertex(transformer4d.backwardTransform(edge.getA().getCoords()));
            Vertex b = new Vertex(transformer4d.backwardTransform(edge.getB().getCoords()));
            Edge newEdge = new Edge(a, b);
            newEdges.add(newEdge);
        }
        return new FigureBaseImpl(null, newEdges);
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        double[] tACoords = transformer4d.transform(edge.getA().getCoords());
        double[] tBCoords = transformer4d.transform(edge.getB().getCoords());
        
        Edge tEdge = new Edge(new Vertex(tACoords), new Vertex(tBCoords));
        tEdge.getFaces().addAll(edge.getFaces());
        tEdge.setSelection(edge.getSelection());
        
        return tEdge;
    }
    
    @Override
    public void move(Vector vector) {
        transformer4d.move(vector);
    }

    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        transformer4d.rotate(rotationPlane, radians);
    }

    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable center) {
        transformer4d.rotate(rotationPlane, radians, center);
    }
    
    @Override
    public void reset() {
        transformer4d.reset();
    }
}
