package com.viewer4d.geometry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.viewer4d.geometry.simple.MovablePoint;

public class Edge {

    private final Vertex a;
    private final Vertex b;
    
    private Set<Face> faces = null;
    private boolean selected;

    public Edge(Vertex a, Vertex b) {
        this.a = a;
        this.b = b;
    }

    public Vertex getA() {
        return a;
    }
    
    public Vertex getB() {
        return b;
    }
    
    public MovablePoint getOpposite(MovablePoint vertex) {
        if (a.equals(vertex)) {
            return b;
        } else if (b.equals(vertex)) {
            return a;
        } else {
            throw new IllegalArgumentException("This vertex (" + vertex +
            		") does not belong to this edge.");
        }
    }
    
    public Set<Face> getFaces() {
        if (faces == null) {
            faces = new HashSet<Face>();
        }
        return faces;
    }

    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("E");
        sb.append(Arrays.toString(a.getCoords())).append("--");
        sb.append(Arrays.toString(b.getCoords()));
        return sb.toString();
    }
}
