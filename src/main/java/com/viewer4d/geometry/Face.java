package com.viewer4d.geometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Face {

    private final Set<Edge> edges;
    private Set<Cell> cells;
    private Set<Face> siblings;
    private Map<Edge, Set<Face>> siblingsByEdges;

    public Face(Set<Edge> edges) {
        this.edges = edges;
    }

    public Face(Edge... edges) {
        this(new HashSet<Edge>(Arrays.asList(edges)));
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Cell> getCells() {
        initCells();
        return cells;
    }
    
    public void addCell(Cell cell) {
        initCells();
        cells.add(cell);
    }

    public Collection<Face> getSiblings() {
        if (siblings == null) {
            siblings = new HashSet<Face>();
            for (Edge edge : edges) {
                for (Face face : edge.getFaces()) {
                    if (face != this) {
                        siblings.add(face);
                    }
                }
            }
        }
        return siblings;
    }

    public Set<Face> getSiblings(Edge edge) {
        if (siblingsByEdges == null) {
            siblingsByEdges = new HashMap<Edge, Set<Face>>();
        }
        Set<Face> edgeFacesSiblings = siblingsByEdges.get(edge);
        if (edgeFacesSiblings == null) {
            
            edgeFacesSiblings = new HashSet<Face>(edge.getFaces());
            edgeFacesSiblings.remove(this);
            
            siblingsByEdges.put(edge, edgeFacesSiblings);
        }
        return edgeFacesSiblings;
    }

    public void setSelection(Selection selection) {
        for (Edge edge : getEdges()) {
            edge.setSelection(selection);
        }
    }

    private void initCells() {
        if (cells == null) {
            cells = new HashSet<Cell>();
        }
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("F").append(edges);
        return sb.toString();
    }
}
