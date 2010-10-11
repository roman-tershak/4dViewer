package com.viewer4d.geometry;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;

public class Transformer4D implements Transformer, Movable {
    
    private MovablePoint c;
    private MovablePoint xt;
    private MovablePoint yt;
    private MovablePoint zt;
    private MovablePoint wt;
    
    private double[][] m;
    
    private Map<MovablePoint, double[]> initialCoords;

    public Transformer4D() {
    }

    public Transformer4D(Pointable c, Pointable xt, Pointable yt, Pointable zt, Pointable wt) {
        setPosition(c, xt, yt, zt, wt);
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
        this.c = new MovablePoint(c);
        this.xt = new MovablePoint(xt);
        this.yt = new MovablePoint(yt);
        this.zt = new MovablePoint(zt);
        this.wt = new MovablePoint(wt);
        
        precalculateTransMatrix();
    }

    @Override
    public double[] transform(double[] coords) {
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
        
        double[] xtCoords = xt.getCoords();
        double[] ytCoords = yt.getCoords();
        double[] ztCoords = zt.getCoords();
        double[] wtCoords = wt.getCoords();
        
        double[] cCoords = c.getCoords();
        
        m = new double[][] {
                {xtCoords[0], ytCoords[0], ztCoords[0], wtCoords[0]}, 
                {xtCoords[1], ytCoords[1], ztCoords[1], wtCoords[1]}, 
                {xtCoords[2], ytCoords[2], ztCoords[2], wtCoords[2]}, 
                {xtCoords[3], ytCoords[3], ztCoords[3], wtCoords[3]}, 
                {-(cCoords[0]*xtCoords[0] + cCoords[1]*xtCoords[1] + cCoords[2]*xtCoords[2] + cCoords[3]*xtCoords[3]),
                 -(cCoords[0]*ytCoords[0] + cCoords[1]*ytCoords[1] + cCoords[2]*ytCoords[2] + cCoords[3]*ytCoords[3]),
                 -(cCoords[0]*ztCoords[0] + cCoords[1]*ztCoords[1] + cCoords[2]*ztCoords[2] + cCoords[3]*ztCoords[3]),
                 -(cCoords[0]*wtCoords[0] + cCoords[1]*wtCoords[1] + cCoords[2]*wtCoords[2] + cCoords[3]*wtCoords[3])}
        };
    }

    @Override
    public void move(Vector vector) {
        c.move(vector);
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