package com.viewer4d.geometry.figure;

import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.simple.Pointable;

public interface FigureMovable extends Figure, Movable {

    public Pointable getCentrum();
    
}
