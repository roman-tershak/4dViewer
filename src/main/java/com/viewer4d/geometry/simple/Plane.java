package com.viewer4d.geometry.simple;

public class Plane {

    public static final double PRECISION_DEFAULT = Pointable.PRECISION;
    
    private final Point p1;
    private final Point p2;
    private final Point p3;
    
    private final double precision;
    
    private double x1;
    private double y1;
    private double z1;
    private double w1;
    
    private double[] dm = new double[24];

    
    public Plane(Pointable p1, Pointable p2, Pointable p3) {
        this(p1, p2, p3, PRECISION_DEFAULT);
    }

    public Plane(Pointable p1, Pointable p2, Pointable p3, double precision) {
        this.p1 = new Point(p1.getCoords());
        this.p2 = new Point(p2.getCoords());
        this.p3 = new Point(p3.getCoords());
        
        this.precision = precision;
        
        fillDeterminantMembers();
    }

    public boolean liesOn(Pointable pointable) {
        /*
         * Plane equation in XYZW space
         * |xn-x1 yn-y1 zn-z1|          |yn-y1 zn-z1 wn-w1|          |xn-x1 zn-z1 wn-w1|          |xn-x1 yn-y1 wn-w1|    
         * |x2-x1 y2-y1 z2-z1| = 0  &&  |y2-y1 z2-z1 w2-w1| = 0  &&  |x2-x1 z2-z1 w2-w1| = 0  &&  |x2-x1 y2-y1 w2-w1| = 0
         * |x3-x1 y3-y1 z3-z1|          |y3-y1 z3-z1 w3-w1|          |x3-x1 z3-z1 w3-w1|          |x3-x1 y3-y1 w3-w1|    
         * 
         * 3D matrix 
         * | a11  a12  a13 |
         * | a21  a22  a23 |
         * | a31  a32  a33 |
         * and its determinant
         * D = a11a22a33 - a11a23a32 - a12a21a33 + a12a23a31 + a13a21a32 - a13a22a31
         * 
         * D1 = (xn-x1)(y2-y1)(z3-z1)-(xn-x1)(z2-z1)(y3-y1)-(yn-y1)(x2-x1)(z3-z1)+(yn-y1)(z2-z1)(x3-x1)+(zn-z1)(x2-x1)(y3-y1)-(zn-z1)(y2-y1)(x3-x1)
         * D2 = (yn-y1)(z2-z1)(w3-w1)-(yn-y1)(w2-w1)(z3-z1)-(zn-z1)(y2-y1)(w3-w1)+(zn-z1)(w2-w1)(y3-y1)+(wn-w1)(y2-y1)(z3-z1)-(wn-w1)(z2-z1)(y3-y1)
         * D3 = (xn-x1)(z2-z1)(w3-w1)-(xn-x1)(w2-w1)(z3-z1)-(zn-z1)(x2-x1)(w3-w1)+(zn-z1)(w2-w1)(x3-x1)+(wn-w1)(x2-x1)(z3-z1)-(wn-w1)(z2-z1)(x3-x1)
         * D4 = (xn-x1)(y2-y1)(w3-w1)-(xn-x1)(w2-w1)(y3-y1)-(yn-y1)(x2-x1)(w3-w1)+(yn-y1)(w2-w1)(x3-x1)+(wn-w1)(x2-x1)(y3-y1)-(wn-w1)(y2-y1)(x3-x1)
         */
        double[] pCoords = pointable.getCoords();
        
        double dx = pCoords[0] - x1;
        double dy = pCoords[1] - y1;
        double dz = pCoords[2] - z1;
        double dw = pCoords[3] - w1;
        
        double D1 = dx*dm[0]  - dx*dm[1]  - dy*dm[2]  + dy*dm[3]  + dz*dm[4]  - dz*dm[5];
        double D2 = dy*dm[6]  - dy*dm[7]  - dz*dm[8]  + dz*dm[9]  + dw*dm[10] - dw*dm[11];
        double D3 = dx*dm[12] - dx*dm[13] - dz*dm[14] + dz*dm[15] + dw*dm[16] - dw*dm[17];
        double D4 = dx*dm[18] - dx*dm[19] - dy*dm[20] + dy*dm[21] + dw*dm[22] - dw*dm[23];
        
        return (Math.abs(D1) < precision && Math.abs(D2) < precision &&
                Math.abs(D3) < precision && Math.abs(D4) < precision);
    }
    
    private void fillDeterminantMembers() {
        double[] p1Coords = p1.getCoords();
        double[] p2Coords = p2.getCoords();
        double[] p3Coords = p3.getCoords();
        
        x1 = p1Coords[0];
        y1 = p1Coords[1];
        z1 = p1Coords[2];
        w1 = p1Coords[3];
        
        double baDx = p2Coords[0] - x1;
        double baDy = p2Coords[1] - y1;
        double baDz = p2Coords[2] - z1;
        double baDw = p2Coords[3] - w1;
        
        double caDx = p3Coords[0] - x1;
        double caDy = p3Coords[1] - y1;
        double caDz = p3Coords[2] - z1;
        double caDw = p3Coords[3] - w1;
        
        dm[0]  = baDy * caDz; //(y2-y1)(z3-z1)
        dm[1]  = baDz * caDy; //(z2-z1)(y3-y1)
        dm[2]  = baDx * caDz; //(x2-x1)(z3-z1)
        dm[3]  = baDz * caDx; //(z2-z1)(x3-x1)
        dm[4]  = baDx * caDy; //(x2-x1)(y3-y1)
        dm[5]  = baDy * caDx; //(y2-y1)(x3-x1)
        dm[6]  = baDz * caDw; //(z2-z1)(w3-w1)
        dm[7]  = baDw * caDz; //(w2-w1)(z3-z1)
        dm[8]  = baDy * caDw; //(y2-y1)(w3-w1)
        dm[9]  = baDw * caDy; //(w2-w1)(y3-y1)
        dm[10] = baDy * caDz; //(y2-y1)(z3-z1)
        dm[11] = baDz * caDy; //(z2-z1)(y3-y1)
        dm[12] = baDz * caDw; //(z2-z1)(w3-w1)
        dm[13] = baDw * caDz; //(w2-w1)(z3-z1)
        dm[14] = baDx * caDw; //(x2-x1)(w3-w1)
        dm[15] = baDw * caDx; //(w2-w1)(x3-x1)
        dm[16] = baDx * caDz; //(x2-x1)(z3-z1)
        dm[17] = baDz * caDx; //(z2-z1)(x3-x1)
        dm[18] = baDy * caDw; //(y2-y1)(w3-w1)
        dm[19] = baDw * caDy; //(w2-w1)(y3-y1)
        dm[20] = baDx * caDw; //(x2-x1)(w3-w1)
        dm[21] = baDw * caDx; //(w2-w1)(x3-x1)
        dm[22] = baDx * caDy; //(x2-x1)(y3-y1)
        dm[23] = baDy * caDx; //(y2-y1)(x3-x1)
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(48);
        sb.append("Plane 2D:(");
        sb.append(p1).append(", ");
        sb.append(p2).append(", ");
        sb.append(p3).append(")");
        return sb.toString();
    }
}
