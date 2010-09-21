package com.viewer4d.geometry.simple;

public interface Dimensional {

    public static enum UNIT_VECTORS {
        X, Y, Z, W
    }
    
    public double[] getCoords();

}