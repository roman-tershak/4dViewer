package com.viewer4d.geometry;

import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;


public interface Movable {

    public void move(Vector vector);
    
    public void rotate(RotationPlane4DEnum rotationPlane, double radians);
    
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable centrum);
    
    public void reset();
}
