package com.viewer4d.geometry.simple;

import java.util.Arrays;

public class Vector implements Dimensional {

    private final double[] coords = new double[] {0, 0, 0, 0};
    
    public Vector(double x, double y) {
        this(x, y, 0, 0);
    }
    
    public Vector(double x, double y, double z) {
        this(x, y, z, 0);
    }
    
    public Vector(double x, double y, double z, double w) {
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
        coords[3] = w;
    }

    public Vector(double[] coords) {
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }

    @Override
    public double[] getCoords() {
        return coords;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Vector:").append(Arrays.toString(getCoords()));
        return sb.toString();
    }
}
