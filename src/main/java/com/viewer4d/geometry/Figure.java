package com.viewer4d.geometry;

import java.util.Collection;

public interface Figure {

    public Collection<Cell> getCells();
    
    public Collection<Face> getFaces();
    
    public Collection<Edge> getEdges();
    
    public Collection<Vertex> getVertices();
}
