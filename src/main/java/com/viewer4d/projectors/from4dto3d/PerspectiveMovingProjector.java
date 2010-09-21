package com.viewer4d.projectors.from4dto3d;


import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.Point;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;

public class PerspectiveMovingProjector extends PerspectiveMovableProjector implements Movable {

    public PerspectiveMovingProjector(double distance) {
        super(distance);
    }

    @Override
    public void move(Vector vector) {
        getC().move(vector.getCoords());
        precalculateTransMatrix();
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable centrum) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);

        getC().rotate(rotationMatrix, centrum);
        
        getXt().rotate(rotationMatrix, Point.ZERO);
        getYt().rotate(rotationMatrix, Point.ZERO);
        getZt().rotate(rotationMatrix, Point.ZERO);
        getWt().rotate(rotationMatrix, Point.ZERO);
        
        precalculateTransMatrix();
    }
    
    @Override
    public void reset() {
        super.reset();
    }
}
