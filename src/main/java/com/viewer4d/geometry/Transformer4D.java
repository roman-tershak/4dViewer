package com.viewer4d.geometry;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;

public class Transformer4D implements Transformer, Movable {
    
    protected static final Map<UNIT_VECTORS, Point[][]> ORT_VECTORS_TRANS_ORTS;
    
    static {
        ORT_VECTORS_TRANS_ORTS = new HashMap<UNIT_VECTORS, Point[][]>();

        ORT_VECTORS_TRANS_ORTS.put(UNIT_VECTORS.X, new Point[][] {
                { new Point(0, 0, 0, -1), new Point(0, 1, 0, 0), new Point(0, 0, 1, 0),
                        new Point(1, 0, 0, 0) },
                { new Point(0, 0, 0, 1), new Point(0, 1, 0, 0), new Point(0, 0, 1, 0),
                        new Point(-1, 0, 0, 0) } });
        ORT_VECTORS_TRANS_ORTS.put(UNIT_VECTORS.Y, new Point[][] {
                { new Point(1, 0, 0, 0), new Point(0, 0, 0, -1), new Point(0, 0, 1, 0),
                        new Point(0, 1, 0, 0) },
                { new Point(1, 0, 0, 0), new Point(0, 0, 0, 1), new Point(0, 0, 1, 0),
                        new Point(0, -1, 0, 0) } });
        ORT_VECTORS_TRANS_ORTS.put(UNIT_VECTORS.Z, new Point[][] {
                { new Point(1, 0, 0, 0), new Point(0, 1, 0, 0), new Point(0, 0, 0, -1),
                        new Point(0, 0, 1, 0) },
                { new Point(1, 0, 0, 0), new Point(0, 1, 0, 0), new Point(0, 0, 0, 1),
                        new Point(0, 0, -1, 0) } });
        ORT_VECTORS_TRANS_ORTS.put(UNIT_VECTORS.W, new Point[][] {
                { new Point(1, 0, 0, 0), new Point(0, 1, 0, 0), new Point(0, 0, 1, 0),
                        new Point(0, 0, 0, 1) },
                { new Point(-1, 0, 0, 0), new Point(0, 1, 0, 0), new Point(0, 0, 1, 0),
                        new Point(0, 0, 0, -1) } });
    }
    
    private final MovablePoint c = new MovablePoint(0, 0, 0, 0);
    private final MovablePoint xt = new MovablePoint(0, 0, 0, 0);
    private final MovablePoint yt = new MovablePoint(0, 0, 0, 0);
    private final MovablePoint zt = new MovablePoint(0, 0, 0, 0);
    private final MovablePoint wt = new MovablePoint(0, 0, 0, 0);
    
    private double[][] mf;
    private double[][] mb;
    
    private Map<MovablePoint, double[]> initialCoords;

    public Transformer4D(Pointable c, Pointable xt, Pointable yt, Pointable zt, Pointable wt) {
        setPosition(c, xt, yt, zt, wt);
        storeCoords();
    }

    public Transformer4D(UNIT_VECTORS unitVector, boolean forward, double distance, Pointable focus) {
        setPosition(unitVector, forward, distance, focus);
        storeCoords();
    }

    public MovablePoint getC() {
        return c;
    }
    public MovablePoint getXt() {
        return xt;
    }
    public MovablePoint getYt() {
        return yt;
    }
    public MovablePoint getZt() {
        return zt;
    }
    public MovablePoint getWt() {
        return wt;
    }
    
    public void setPosition(Pointable c, Pointable xt, Pointable yt, Pointable zt, Pointable wt) {
        this.c.set(c);
        this.xt.set(xt);
        this.yt.set(yt);
        this.zt.set(zt);
        this.wt.set(wt);
        
        precalculateTransMatrix();
    }

    public void setPosition(UNIT_VECTORS unitVector, boolean forward, double distance, Pointable focus) {
        Vector ortCamVector = Vector.createFrom(unitVector, 
                (forward ? -distance : distance));
        
        Point newCamPoint = focus.add(ortCamVector);
        Point[] transOrts = retrieveTransOrts(unitVector, forward);
        
        setPosition(newCamPoint, transOrts[0], transOrts[1], transOrts[2], transOrts[3]);
    }


    private static Point[] retrieveTransOrts(UNIT_VECTORS unitVector, boolean forward) {
        return ORT_VECTORS_TRANS_ORTS.get(unitVector)[forward ? 0 : 1];
    }

    @Override
    public double[] transform(double[] coords) {
        return matrixTransform(coords, mf);
    }

    @Override
    public double[] backwardTransform(double[] coords) {
        return matrixTransform(coords, mb);
    }
    
    private double[] matrixTransform(double[] coords, double[][] m) {
        double px = coords[0];
        double py = coords[1];
        double pz = coords[2];
        double pw = coords[3];
        
        double nx = px*m[0][0] + py*m[1][0] + pz*m[2][0] + pw*m[3][0] + m[4][0];
        double ny = px*m[0][1] + py*m[1][1] + pz*m[2][1] + pw*m[3][1] + m[4][1];
        double nz = px*m[0][2] + py*m[1][2] + pz*m[2][2] + pw*m[3][2] + m[4][2];
        double nw = px*m[0][3] + py*m[1][3] + pz*m[2][3] + pw*m[3][3] + m[4][3];
        
        return new double[] {nx, ny, nz, nw};
    }
    
    protected void precalculateTransMatrix() {
        
        double[] xtcoo = xt.getCoords();
        double[] ytcoo = yt.getCoords();
        double[] ztcoo = zt.getCoords();
        double[] wtcoo = wt.getCoords();
        
        double[] cacoo = c.getCoords();
        
        mf = new double[][] {
                {xtcoo[0], ytcoo[0], ztcoo[0], wtcoo[0]}, 
                {xtcoo[1], ytcoo[1], ztcoo[1], wtcoo[1]}, 
                {xtcoo[2], ytcoo[2], ztcoo[2], wtcoo[2]}, 
                {xtcoo[3], ytcoo[3], ztcoo[3], wtcoo[3]}, 
                {-(cacoo[0]*xtcoo[0] + cacoo[1]*xtcoo[1] + cacoo[2]*xtcoo[2] + cacoo[3]*xtcoo[3]),
                 -(cacoo[0]*ytcoo[0] + cacoo[1]*ytcoo[1] + cacoo[2]*ytcoo[2] + cacoo[3]*ytcoo[3]),
                 -(cacoo[0]*ztcoo[0] + cacoo[1]*ztcoo[1] + cacoo[2]*ztcoo[2] + cacoo[3]*ztcoo[3]),
                 -(cacoo[0]*wtcoo[0] + cacoo[1]*wtcoo[1] + cacoo[2]*wtcoo[2] + cacoo[3]*wtcoo[3])}
        };
        mb = new double[][] {
                {xtcoo[0], xtcoo[1], xtcoo[2], xtcoo[3]},
                {ytcoo[0], ytcoo[1], ytcoo[2], ytcoo[3]},
                {ztcoo[0], ztcoo[1], ztcoo[2], ztcoo[3]},
                {wtcoo[0], wtcoo[1], wtcoo[2], wtcoo[3]},
                {cacoo[0], cacoo[1], cacoo[2], cacoo[3]}
        };
    }

    @Override
    public void move(Vector vector) {
        c.move(vector);
        
        precalculateTransMatrix();
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable center) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);
        
        c.rotate(rotationMatrix, center);
        
        xt.rotate(rotationMatrix, Point.ZERO);
        yt.rotate(rotationMatrix, Point.ZERO);
        zt.rotate(rotationMatrix, Point.ZERO);
        wt.rotate(rotationMatrix, Point.ZERO);
        
        precalculateTransMatrix();
    }

    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        rotate(rotationPlane, radians, Point.ZERO);
    }
    
    @Override
    public void reset() {
        restoreCoords();
        precalculateTransMatrix();
    }
    
    private void storeCoords() {
        initialCoords = new HashMap<MovablePoint, double[]>();
        initialCoords.put(c, c.getCoords().clone());
        initialCoords.put(xt, xt.getCoords().clone());
        initialCoords.put(yt, yt.getCoords().clone());
        initialCoords.put(zt, zt.getCoords().clone());
        initialCoords.put(wt, wt.getCoords().clone());
    }

    private void restoreCoords() {
        c.setCoords(initialCoords.get(c).clone());
        xt.setCoords(initialCoords.get(xt).clone());
        yt.setCoords(initialCoords.get(yt).clone());
        zt.setCoords(initialCoords.get(zt).clone());
        wt.setCoords(initialCoords.get(wt).clone());
    }
}