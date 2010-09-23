package com.viewer4d.geometry.simple;

import java.util.Arrays;

public class MovablePoint extends Pointable {

    public MovablePoint(double x, double y) {
        super(x, y);
    }

    public MovablePoint(double x, double y, double z) {
        super(x, y, z);
    }

    public MovablePoint(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public MovablePoint(double[] coords) {
        super(coords);
    }
    
    public MovablePoint(Pointable point) {
        this(point.getCoords());
    }

    public void setCoords(double[] coords) {
        resetCoords();
        System.arraycopy(coords, 0, getCoords(), 0, coords.length);
    }

    public void setCoords(double x, double y, double z, double w) {
        double[] coords = getCoords();
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
        coords[3] = w;
    }

    public void set(Pointable point) {
        double[] pCoords = point.getCoords();
        double[] thisCoords = getCoords();
        System.arraycopy(pCoords, 0, thisCoords, 0, thisCoords.length);
    }

    public void move(double... vector) {
        double[] coords = getCoords();
        for (int i = 0; i < vector.length; i++) {
            coords[i] += vector[i];
        }
    }

    public void move(Vector vector) {
        move(vector.getCoords());
    }

    public void rotate(double[][] rotationMatrix, Pointable centrum) {
        double[] thisCoords = getCoords();
        double[] centrumCoords = centrum.getCoords();
        
        double dx = thisCoords[0] - centrumCoords[0];
        double dy = thisCoords[1] - centrumCoords[1];
        double dz = thisCoords[2] - centrumCoords[2];
        double dw = thisCoords[3] - centrumCoords[3];
    
        double[] rotRow;
    
        rotRow = rotationMatrix[0];
        double ndx = (dx * rotRow[0]) + (dy * rotRow[1]) + (dz * rotRow[2]) + (dw * rotRow[3]);
    
        rotRow = rotationMatrix[1];
        double ndy = (dx * rotRow[0]) + (dy * rotRow[1]) + (dz * rotRow[2]) + (dw * rotRow[3]);
    
        rotRow = rotationMatrix[2];
        double ndz = (dx * rotRow[0]) + (dy * rotRow[1]) + (dz * rotRow[2]) + (dw * rotRow[3]);
    
        rotRow = rotationMatrix[3];
        double ndw = (dx * rotRow[0]) + (dy * rotRow[1]) + (dz * rotRow[2]) + (dw * rotRow[3]);
    
        thisCoords[0] = ndx + centrumCoords[0];
        thisCoords[1] = ndy + centrumCoords[1];
        thisCoords[2] = ndz + centrumCoords[2];
        thisCoords[3] = ndw + centrumCoords[3];
    }

    protected void resetCoords() {
        double[] thisCoords = getCoords();
        thisCoords[0] = 0;
        thisCoords[1] = 0;
        thisCoords[2] = 0;
        thisCoords[3] = 0;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Point (Movable): ").append(Arrays.toString(getCoords()));
        return sb.toString();
    }
}