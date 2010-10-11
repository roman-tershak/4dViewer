package com.viewer4d.projector.from4dto3d;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.Transformer;
import com.viewer4d.geometry.Transformer4D;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;

public class PerspectiveMovableProjector extends PerspectiveProjectorOnXYZ 
implements Transformer, Movable {

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
    
    private Transformer4D transformer = new Transformer4D();
    private final MovablePoint focus;
    private double distance;


    public PerspectiveMovableProjector(double wPos, boolean perspective, boolean colorRelToWOrt) {
        super(wPos, perspective, colorRelToWOrt);
        
        this.focus = new MovablePoint(Point.ZERO);
        
        Point[] transOrts = retrieveTransOrts(UNIT_VECTORS.W, (wPos < 0 ? true : false));

        this.transformer = new Transformer4D(new Point(0, 0, 0, wPos),
                transOrts[0], transOrts[1], transOrts[2], transOrts[3]);
        
        this.distance = Math.abs(wPos);
    }
    
    public PerspectiveMovableProjector(double wPos, boolean perspective) {
        this(wPos, perspective, true);
    }
    
    @Override
    public double[] transform(double[] coords) {
        return transformer.transform(coords);
    }
    
    public void setProjector(UNIT_VECTORS unitVector, boolean forward) {
        Vector ortCamVector = retrieveCamVector(unitVector, 
                (forward ? -distance : distance));
        
        Point newCamPoint = focus.add(ortCamVector);
        Point[] transOrts = retrieveTransOrts(unitVector, forward);
        
        transformer.setPosition(newCamPoint, transOrts[0], transOrts[1], transOrts[2], transOrts[3]);
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
        
        Vector camVector = transformer.getC().sub(focus);
        camVector.setLength(distance);
        transformer.move(camVector);
    }
    
    @Override
    public void move(Vector vector) {
        focus.move(vector);
        transformer.move(vector);
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        transformer.rotate(rotationPlane, radians, focus);
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable center) {
        throw new UnsupportedOperationException("This method is not supported.");
    }

    @Override
    public void reset() {
        focus.set(Point.ZERO);
        transformer.reset();
        
        distance = transformer.getC().sub(focus).getLength();
        super.setProjectorDistance(distance);
    }

}