package com.viewer4d.projector.from4dto3d;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Dimensional;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Changeable;

public class PerspectiveMovableProjector extends AbstractProjectingProjector implements Changeable {

    protected static final double MIN_4D_DISTANCE = 3.3333333;
    
    protected static final Point XT = new Point(1, 0, 0, 0);
    protected static final Point YT = new Point(0, 1, 0, 0);
    protected static final Point ZT = new Point(0, 0, 1, 0);
    protected static final Point WT = new Point(0, 0, 0, 1);
    
    private final MovablePoint c;
    private final MovablePoint xt;
    private final MovablePoint yt;
    private final MovablePoint zt;
    private final MovablePoint wt;
    
    private Map<MovablePoint, double[]> initialCoords;
    private double[][] m;


    public PerspectiveMovableProjector(double distance) {
        this.c = new MovablePoint(0, 0, 0, distance);
        this.xt = new MovablePoint(XT);
        this.yt = new MovablePoint(YT);
        this.zt = new MovablePoint(ZT);
        this.wt = new MovablePoint(WT);
        
        precalculateTransMatrix();
        storeCoords();
    }
    
    protected MovablePoint getC() {
        return c;
    }
    
    protected MovablePoint getXt() {
        return xt;
    }
    
    protected MovablePoint getYt() {
        return yt;
    }

    protected MovablePoint getZt() {
        return zt;
    }

    protected MovablePoint getWt() {
        return wt;
    }
    
    public void setProjector(UNIT_VECTORS vector, boolean forward) {
        reset();
        
        double[][] rotationMatrix = getRotationMatrix(vector, forward);
        
        getC().rotate(rotationMatrix, Point.ZERO);
        getXt().rotate(rotationMatrix, Point.ZERO);
        getYt().rotate(rotationMatrix, Point.ZERO);
        getZt().rotate(rotationMatrix, Point.ZERO);
        getWt().rotate(rotationMatrix, Point.ZERO);
        
        precalculateTransMatrix();
    }
    
    private double[][] getRotationMatrix(UNIT_VECTORS vector, boolean forward) {
        RotationPlane4DEnum rotationPlane = null;
        double radians = forward ? -Math.PI/2 : Math.PI/2;
        
        switch (vector) {
        case X:
            rotationPlane = RotationPlane4DEnum.XW;
            break;
        case Y:
            rotationPlane = RotationPlane4DEnum.YW;
            break;
        case Z:
            rotationPlane = RotationPlane4DEnum.ZW;
            break;
        case W:
            rotationPlane = RotationPlane4DEnum.XW;
            if (forward) {
                radians = 0;
            } else {
                radians = Math.PI;
            }
            break;
        }
        return rotationPlane.getRotationMatrix(radians);
    }

    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double[] ppCoords = projectPerspective(
                transform(vertex)).getCoords();
    
        return new Vertex(ppCoords[0], ppCoords[1], ppCoords[2], vertex.getCoords()[3]);
    }

    public Point transform(Dimensional p) {
        double[] coords = p.getCoords();
        double px = coords[0];
        double py = coords[1];
        double pz = coords[2];
        double pw = coords[3];
        
        double nx = px*m[0][0] + py*m[1][0] + pz*m[2][0] + pw*m[3][0] + m[4][0];
        double ny = px*m[0][1] + py*m[1][1] + pz*m[2][1] + pw*m[3][1] + m[4][1];
        double nz = px*m[0][2] + py*m[1][2] + pz*m[2][2] + pw*m[3][2] + m[4][2];
        double nw = px*m[0][3] + py*m[1][3] + pz*m[2][3] + pw*m[3][3] + m[4][3];
        
        return new Point(nx, ny, nz, nw);
    }

    public void reset() {
        restoreCoords();
        
        precalculateTransMatrix();
    }

    @Override
    public void change(int delta) {
        double koef = delta < 0 ? 1.02 : 0.98;
        double[] cCoords = c.getCoords();
        double[] storeCoords = cCoords.clone();
        
        cCoords[0] *= koef;
        cCoords[1] *= koef;
        cCoords[2] *= koef;
        cCoords[3] *= koef;
        
        if (getProjectorDistance() < MIN_4D_DISTANCE) {
            c.setCoords(storeCoords);
        } else {
            precalculateTransMatrix();
        }
    }

    protected double getProjectorDistance() {
        double[] cCoords = c.getCoords();
        double x = cCoords[0];
        double y = cCoords[1];
        double z = cCoords[2];
        double w = cCoords[3];
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }

    private Point projectPerspective(Dimensional dimensional) {
        double camDist = getProjectorDistance();
        double[] coords = dimensional.getCoords();
        
        double koef = Math.abs(camDist / coords[3]);
        
        double nx = coords[0] * koef;
        double ny = coords[1] * koef;
        double nz = coords[2] * koef;
        double nw = coords[3];
        
        return new Point(nx, ny, nz, nw);
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

}