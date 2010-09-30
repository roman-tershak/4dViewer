package com.viewer4d.geometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Cell {

    private Set<Face> faces;
    private Collection<Cell> siblings;
    
    public Cell() {
    }
    
    public Set<Face> getFaces() {
        initFaces();
        return faces;
    }

    public void addFace(Face face) {
        initFaces();
        faces.add(face);
        face.addCell(this);
    }

    public Collection<Cell> getSiblings() {
        if (siblings == null) {
            siblings = new HashSet<Cell>();
            for (Face face : getFaces()) {
                for (Cell cell : face.getCells()) {
                    if (cell != this) {
                        siblings.add(cell);
                    }
                }
            }
        }
        return siblings;
    }

    public void setSelection(Selection selection) {
        for (Face face : getFaces()) {
            face.setSelection(selection);
        }
    }
    
    private void initFaces() {
        if (faces == null) {
            faces = new HashSet<Face>();
        }
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(128);
        sb.append("C").append(getFaces());
        return sb.toString();
    }
}
