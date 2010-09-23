package com.viewer4d.geometry.simple;

public abstract class Pointable implements Dimensional {

    public static final double PRECISION = 1e-12;

    private final double[] coords = new double[] {0, 0, 0, 0};
    
    public Pointable(double x, double y) {
        this(x, y, 0, 0);
    }
    
    public Pointable(double x, double y, double z) {
        this(x, y, z, 0);
    }
    
    public Pointable(double x, double y, double z, double w) {
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
        coords[3] = w;
    }

    public Pointable(double[] coords) {
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }

    @Override
    public double[] getCoords() {
        return coords;
    }

    public boolean compareWith(Pointable other, double delta) {
        return compare(this, other, delta);
    }

    public static boolean compare(Pointable a, Pointable b, double delta) {
        if (a == null || b == null) {
            return false;
        }
        double[] aCoords = a.coords;
        double[] bCoords = b.coords;
        if (aCoords.length != bCoords.length) {
            return false;
        }
        for (int i = 0; i < aCoords.length; i++) {
            if ((Math.abs(aCoords[i] - bCoords[i])) > delta) {
                return false;
            }
        }
        return true;
    }

    public Vector sub(Pointable point) {
        double[] vector = new double[] {0, 0, 0, 0};
        double[] pCoords = point.getCoords();
        
        for (int i = 0; i < coords.length; i++) {
            vector[i] = coords[i] - pCoords[i];
        }
        return new Vector(vector);
    }

    public Point add(Vector vector) {
        double[] newCoords = new double[] {0, 0, 0, 0};
        double[] vCoords = vector.getCoords();
        
        for (int i = 0; i < coords.length; i++) {
            newCoords[i] = coords[i] + vCoords[i];
        }
        return new Point(newCoords);
    }

}