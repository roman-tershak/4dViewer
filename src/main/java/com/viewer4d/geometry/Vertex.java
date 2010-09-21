package com.viewer4d.geometry;

import java.util.Arrays;

import com.viewer4d.geometry.simple.MovablePoint;


public class Vertex extends MovablePoint {

    public Vertex(double x, double y) {
        super(x, y);
    }

    public Vertex(double x, double y, double z) {
        super(x, y, z);
    }

    public Vertex(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public Vertex(double... coords) {
        super(coords);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("V").append(Arrays.toString(getCoords()));
        return sb.toString();
    }
    
}
