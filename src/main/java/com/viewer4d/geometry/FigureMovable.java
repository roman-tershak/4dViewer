package com.viewer4d.geometry;

import com.viewer4d.geometry.simple.Pointable;

public interface FigureMovable extends Figure, Movable {

    public Pointable getCentrum();
    
}
