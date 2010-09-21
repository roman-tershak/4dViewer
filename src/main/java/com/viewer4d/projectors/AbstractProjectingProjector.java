package com.viewer4d.projectors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureBaseImpl;



public abstract class AbstractProjectingProjector extends AbstractEnablingProjector {

    private Map<Vertex, Vertex> projectedVertices = new HashMap<Vertex, Vertex>();
    
    public AbstractProjectingProjector() {
    }
    
    protected void storeProjectedVertex(Vertex origin, Vertex projected) {
        projectedVertices.put(origin, projected);
    }
    
    protected Vertex retrieveProjectedVertex(Vertex origin) {
        return projectedVertices.get(origin);
    }
    
    protected void removeProjectedVertex(Vertex origin) {
        projectedVertices.remove(origin);
    }

    @Override
    protected Figure projectFigure(Figure figure) {
        projectedVertices.clear();
        
        List<Edge> newEdges = new LinkedList<Edge>();
        for (Edge edge : figure.getEdges()) {
            
            Edge projectEdge = projectEdge(edge);
            if (projectEdge != null) {
                projectEdge.setSelected(edge.isSelected());
                newEdges.add(projectEdge);
            }
        }
        return new FigureBaseImpl(null, newEdges);
    }

    protected Edge projectEdge(Edge edge) {
        Vertex a = edge.getA();
        Vertex b = edge.getB();
        
        Vertex va = retrieveProjectedVertex(a);
        if (va == null) {
            va = projectVertex(a);
            storeProjectedVertex(a, va);
        }
        
        Vertex vb = retrieveProjectedVertex(b);
        if (vb == null) {
            vb = projectVertex(b);
            storeProjectedVertex(b, vb);
        }

        return new Edge(va, vb);
    }

    protected abstract Vertex projectVertex(Vertex vertex);
}
