package com.viewer4d.projector.from4dto3d;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;

public class PerspectiveMovableProjector extends PerspectiveProjectorOnXYZ 
implements Movable {

    protected static final double MIN_4D_DISTANCE = 3.3333333;
    
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
    
    private final MovablePoint focus;
    private final MovablePoint c;
    private final MovablePoint xt;
    private final MovablePoint yt;
    private final MovablePoint zt;
    private final MovablePoint wt;
    
    private double distance;
    
    private Map<MovablePoint, double[]> initialCoords;
    private double[][] m;


    public PerspectiveMovableProjector(double wPos, boolean perspective, boolean colorRelToWOrt) {
        super(wPos, perspective, colorRelToWOrt);
        
        this.focus = new MovablePoint(Point.ZERO);
        this.c = new MovablePoint(0, 0, 0, wPos);
        
        Point[] transOrts = retrieveTransOrts(UNIT_VECTORS.W, (wPos < 0 ? true : false));
        
        this.xt = new MovablePoint(transOrts[0]);
        this.yt = new MovablePoint(transOrts[1]);
        this.zt = new MovablePoint(transOrts[2]);
        this.wt = new MovablePoint(transOrts[3]);
        
        this.distance = Math.abs(wPos);
        
        storeCoords();
        precalculateTransMatrix();
    }
    
    public PerspectiveMovableProjector(double wPos, boolean perspective) {
        this(wPos, perspective, true);
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

    public void setProjector(UNIT_VECTORS unitVector, boolean forward) {
        Vector ortCamVector = retrieveCamVector(unitVector, 
                (forward ? -distance : distance));
        
        Point newCamPoint = focus.add(ortCamVector);
        c.set(newCamPoint);
        
        Point[] transOrts = retrieveTransOrts(unitVector, forward);
        xt.set(transOrts[0]);
        yt.set(transOrts[1]);
        zt.set(transOrts[2]);
        wt.set(transOrts[3]);
        
        precalculateTransMatrix();
    }

    private static Point[] retrieveTransOrts(UNIT_VECTORS unitVector, boolean forward) {
        return ORT_VECTORS_TRANS_ORTS.get(unitVector)[forward ? 0 : 1];
    }

    private Vector retrieveCamVector(UNIT_VECTORS unitVector, double distance) {
        return Vector.createFrom(unitVector, distance);
    }

    @Override
    public double getProjectorDistance() {
        return distance;
    }
    
    @Override
    protected final void setProjectorDistance(double distance) {
        this.distance = distance;
        super.setProjectorDistance(distance);
        
        Vector camVector = c.sub(focus);
        camVector.setLength(distance);
        c.set(focus.add(camVector));
        
        precalculateTransMatrix();
    }
    
    @Override
    public void move(Vector vector) {
        c.move(vector);
        focus.move(vector);
        precalculateTransMatrix();
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);

        c.rotate(rotationMatrix, focus);
        
        xt.rotate(rotationMatrix, Point.ZERO);
        yt.rotate(rotationMatrix, Point.ZERO);
        zt.rotate(rotationMatrix, Point.ZERO);
        wt.rotate(rotationMatrix, Point.ZERO);
        
        precalculateTransMatrix();
    }
    
    @Override
    public void reset() {
        restoreCoords();
        
        distance = c.sub(focus).getLength();
        super.setProjectorDistance(distance);
        
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
        focus.set(Point.ZERO);
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