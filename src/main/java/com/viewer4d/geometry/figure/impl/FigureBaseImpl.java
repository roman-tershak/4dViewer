package com.viewer4d.geometry.figure.impl;

import java.util.Collection;
import java.util.HashSet;

import com.viewer4d.geometry.figure.Cell;
import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Face;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.Vertex;

public class FigureBaseImpl implements Figure {

    private Collection<Cell> cells;
    private Collection<Face> faces;
    private Collection<Edge> edges;
    private Collection<Vertex> vertices;

    public FigureBaseImpl(Collection<Face> faces, Collection<Edge> edges) {
        this.faces = faces;
        this.edges = edges;
    }
    
    @Override
    public Collection<Cell> getCells() {
        if (cells == null) {
            setCells();
        }
        return cells;
    }
    
    protected void setCells() {
        cells = new HashSet<Cell>();
    }
    
    @Override
    public Collection<Face> getFaces() {
        if (faces == null) {
            faces = new HashSet<Face>();
        }
        return faces;
    }

    @Override
    public Collection<Edge> getEdges() {
        if (edges == null) {
            edges = new HashSet<Edge>();
            setEdgesFromFaces();
        }
        return edges;
    }

    @Override
    public Collection<Vertex> getVertices() {
        if (vertices == null) {
            vertices = new HashSet<Vertex>();
            setVerticesFromEdges();
        }
        return vertices;
    }

    private void setEdgesFromFaces() {
        Collection<Face> faces = getFaces();
        for (Face face : faces) {
            edges.addAll(face.getEdges());
        }
    }
    
    private void setVerticesFromEdges() {
        Collection<Edge> edges = getEdges();
        for (Edge edge : edges) {
            vertices.add(edge.getA());
            vertices.add(edge.getB());
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Figure: {").append(getClass().getSimpleName()).append("}");
        return sb.toString();
    }

}
