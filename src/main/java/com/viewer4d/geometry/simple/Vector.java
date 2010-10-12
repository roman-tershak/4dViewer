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

    public double getLength() {
        return Math.sqrt(coords[0]*coords[0] + coords[1]*coords[1] + 
                coords[2]*coords[2] + coords[3]*coords[3]);
    }
    
    public void setLength(double distance) {
        multiply(distance / getLength());
    }
    
    public Vector negate() {
        return new Vector(-coords[0], -coords[1], -coords[2], -coords[3]);
    }
    
    public void multiply(double koef) {
        coords[0] *= koef;
        coords[1] *= koef;
        coords[2] *= koef;
        coords[3] *= koef;
    }

    public static Vector createFrom(UNIT_VECTORS unitVector, double len) {
        switch (unitVector) {
        case X:
            return new Vector(len, 0, 0, 0);
        case Y:
            return new Vector(0, len, 0, 0);
        case Z:
            return new Vector(0, 0, len, 0);
        case W:
            return new Vector(0, 0, 0, len);
        default:
            throw new IllegalArgumentException("Illegal or not implemented argument - " + unitVector);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Vector:").append(Arrays.toString(getCoords()));
        return sb.toString();
    }
}
