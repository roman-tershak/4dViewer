package com.viewer4d.projectors.from4dto3d;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Dimensional;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.projectors.AbstractProjectingProjector;
import com.viewer4d.projectors.Changeable;

public class PerspectiveMovableProjector extends AbstractProjectingProjector implements Movable, Changeable {

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

    public PerspectiveMovableProjector(MovablePoint c, MovablePoint xt, MovablePoint yt,
            MovablePoint zt, MovablePoint wt) {
        this.c = c;
        this.xt = xt;
        this.yt = yt;
        this.zt = zt;
        this.wt = wt;
        
        storeCoords();
        
        precalculateTransMatrix();
    }

    public PerspectiveMovableProjector(double distance) {
        this(
                new MovablePoint(0, 0, 0, distance), 
                new MovablePoint(XT),
                new MovablePoint(YT),
                new MovablePoint(ZT),
                new MovablePoint(WT)
        );
    }

    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double[] ppCoords = projectPerspective(
                transform(
                        vertex)).getCoords();

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

    @Override
    public void move(Vector vector) {
        c.move(vector.getCoords());
        precalculateTransMatrix();
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable centrum) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);

        xt.rotate(rotationMatrix, Point.ZERO);
        yt.rotate(rotationMatrix, Point.ZERO);
        zt.rotate(rotationMatrix, Point.ZERO);
        wt.rotate(rotationMatrix, Point.ZERO);
        c.rotate(rotationMatrix, centrum);
        
        precalculateTransMatrix();
    }
    
    @Override
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
    
    public double getProjectorDistance() {
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

    private void precalculateTransMatrix() {
        
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
