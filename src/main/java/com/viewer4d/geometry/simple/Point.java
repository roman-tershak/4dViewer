package com.viewer4d.geometry.simple;

import java.util.Arrays;

public final class Point extends Pointable {

    public static final Point ZERO = new Point(0, 0, 0, 0);
    
    // Instance members
    private int hashCode = 0;

    public Point(double x, double y) {
        super(x, y);
    }

    public Point(double x, double y, double z) {
        super(x, y, z);
    }
    
    public Point(double x, double y, double z, double w) {
        super(x, y, z, w);
    }
    
    public Point(double[] coords) {
        super(coords);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            final int prime = 31;
            int result = 1;
            double[] coords = getCoords();
            for (int i = 0; i < coords.length; i++) {
                long temp = Double.doubleToLongBits(coords[i]);
                result = prime * result + (int) (temp ^ (temp >>> 32));
            }
            hashCode = result;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pointable other = (Pointable) obj;

        double[] thisCoords = getCoords();
        double[] otherCoords = other.getCoords();
        
        for (int i = 0; i < thisCoords.length; i++) {
            if (Double.doubleToLongBits(thisCoords[i]) != 
                    Double.doubleToLongBits(otherCoords[i]))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Point:").append(Arrays.toString(getCoords()));
        return sb.toString();
    }
}