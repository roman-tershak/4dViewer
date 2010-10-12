package com.viewer4d.projector.from4dto3d;

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
    
    private Transformer4D transformer;
    private final MovablePoint focus;
    private double distance;


    public PerspectiveMovableProjector(double wPos, boolean perspective, boolean colorRelToWOrt) {
        super(wPos, perspective, colorRelToWOrt);
        
        focus = new MovablePoint(Point.ZERO);
        distance = Math.abs(wPos);
        transformer = new Transformer4D(UNIT_VECTORS.W, (wPos < 0 ? true : false), distance, focus);
    }
    
    public PerspectiveMovableProjector(double wPos, boolean perspective) {
        this(wPos, perspective, true);
    }
    
    @Override
    public double[] transform(double[] coords) {
        return transformer.transform(coords);
    }
    
    public void setProjector(UNIT_VECTORS unitVector, boolean forward) {
        transformer.setPosition(unitVector, forward, distance, focus);
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