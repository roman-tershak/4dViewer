package com.viewer4d.geometry.simple;


public class Space {

    public static final double PRECISION_DEFAULT = Pointable.PRECISION;
    
    private final Point p1;
    private final Point p2;
    private final Point p3;
    private final Point p4;
    
    private final double precision;
    
    private double x1;
    private double y1;
    private double z1;
    private double w1;
    
    private double det11;
    private double det12;
    private double det13;
    private double det14;
    
    private int hashCode;

    public Space(Pointable p1, Pointable p2, Pointable p3, Pointable p4) {
        this(p1, p2, p3, p4, PRECISION_DEFAULT);
    }

    public Space(Pointable p1, Pointable p2, Pointable p3, Pointable p4, double precision) {
        this.p1 = new Point(p1.getCoords());
        this.p2 = new Point(p2.getCoords());
        this.p3 = new Point(p3.getCoords());
        this.p4 = new Point(p4.getCoords());
        
        this.precision = precision;
        
        precalculateDeterminant();
    }

    public boolean liesOn(Pointable p) {
        double[] pCoords = p.getCoords();
        double det = (pCoords[0]-x1)*det11 - (pCoords[1]-y1)*det12 + (pCoords[2]-z1)*det13 - (pCoords[3]-w1)*det14;
        if (Math.abs(det) <= precision) {
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * Space equation in XYZW space
     * |xn-x1 yn-y1 zn-z1 wn-w1|
     * |x2-x1 y2-y1 z2-z1 w2-w1| = 0
     * |x3-x1 y3-y1 z3-z1 w3-w1|
     * |x4-x1 y4-y1 z4-z1 w4-w1|
     * 
     * 3x3 matrix 
     * | a11  a12  a13 |
     * | a21  a22  a23 |
     * | a31  a32  a33 |
     * and its determinant
     * D = a11a22a33 - a11a23a32 - a12a21a33 + a12a23a31 + a13a21a32 - a13a22a31
     * 
     * 4x4 matrix 
     * | a11  a12  a13  a14 |
     * | a21  a22  a23  a24 |
     * | a31  a32  a33  a34 |
     * | a41  a42  a43  a44 |
     * and its determinant
     * D = a11*D11 - a12*D12 + a13*D13 - a14D14
     */
    private void precalculateDeterminant() {
        double[] p1Coords = p1.getCoords();
        double[] p2Coords = p2.getCoords();
        double[] p3Coords = p3.getCoords();
        double[] p4Coords = p4.getCoords();
        
        x1 = p1Coords[0];
        y1 = p1Coords[1];
        z1 = p1Coords[2];
        w1 = p1Coords[3];
        
        double x21 = p2Coords[0] - x1;
        double y21 = p2Coords[1] - y1;
        double z21 = p2Coords[2] - z1;
        double w21 = p2Coords[3] - w1;
        double x31 = p3Coords[0] - x1;
        double y31 = p3Coords[1] - y1;
        double z31 = p3Coords[2] - z1;
        double w31 = p3Coords[3] - w1;
        double x41 = p4Coords[0] - x1;
        double y41 = p4Coords[1] - y1;
        double z41 = p4Coords[2] - z1;
        double w41 = p4Coords[3] - w1;
        
        det11 = y21*z31*w41 - y21*w31*z41 - z21*y31*w41 + z21*w31*y41 + w21*y31*z41 - w21*z31*y41;
        det12 = x21*z31*w41 - x21*w31*z41 - z21*x31*w41 + z21*w31*x41 + w21*x31*z41 - w21*z31*x41;
        det13 = x21*y31*w41 - x21*w31*y41 - y21*x31*w41 + y21*w31*x41 + w21*x31*y41 - w21*y31*x41;
        det14 = x21*y31*z41 - x21*z31*y41 - y21*x31*z41 + y21*z31*x41 + z21*x31*y41 - z21*y31*x41;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            double koef = 1 / Math.sqrt(det11*det11 + det12*det12 + det13*det13 + det14*det14);
            double det11n = Math.abs(det11 * koef);
            double det12n = Math.abs(det12 * koef);
            double det13n = Math.abs(det13 * koef);
            double det14n = Math.abs(det14 * koef);
            
            final int prime = 31;
            int result = 1;
            long temp;
            
            temp = roundToPrecision(det11n);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = roundToPrecision(det12n);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = roundToPrecision(det13n);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = roundToPrecision(det14n);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            
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
        Space other = (Space) obj;
        if (liesOn(other.p1) && liesOn(other.p2) && liesOn(other.p3) && liesOn(other.p4)) {
            return true;
        } else {
            return false;
        }
    }

    private long roundToPrecision(double d) {
        double scale = 10d;
        return Double.doubleToLongBits(
                ((double) Math.round(d / scale / precision)) * scale * precision);
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Space 3D:(");
        sb.append(p1).append(", ");
        sb.append(p2).append(", ");
        sb.append(p3).append(", ");
        sb.append(p4).append(")");
        return sb.toString();
    }
    
}
