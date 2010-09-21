package com.viewer4d.projectors.combining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.projectors.AbstractEnablingProjector;
import com.viewer4d.projectors.Changeable;
import com.viewer4d.projectors.Enabling;
import com.viewer4d.projectors.Projector;

public class CombiningProjector<P extends Projector> extends AbstractEnablingProjector 
implements Movable, Changeable {

    private List<P> projectors = new ArrayList<P>();
    
    public CombiningProjector(P... projectors) {
        this.projectors.addAll(Arrays.asList(projectors));
    }

    public List<P> getProjectors() {
        return projectors;
    }
    
    @Override
    protected Figure projectFigure(Figure figure) {
        List<Edge> newEdges = new LinkedList<Edge>();

        for (Projector projector : projectors) {
            Figure projection = projector.project(figure);
            newEdges.addAll(projection.getEdges());
        }

        return new FigureBaseImpl(null, newEdges);
    }
    
    @Override
    public void move(Vector vector) {
        for (Movable movable : getMovableProjectors()) {
            movable.move(vector);
        }
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        for (Movable movable : getMovableProjectors()) {
            movable.rotate(rotationPlane, radians);
        }
    }

    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable centrum) {
        for (Movable movable : getMovableProjectors()) {
            movable.rotate(rotationPlane, radians, centrum);
        }
    }
    
    @Override
    public void reset() {
        for (Movable movable : getMovableProjectors()) {
            movable.reset();
        }
    }
    
    @Override
    public void change(int delta) {
        for (Changeable changeable : getChangeableProjectors()) {
            if (changeable instanceof Enabling) { 
                if (((Enabling) changeable).isEnabled()) {
                    changeable.change(delta);
                }
            } else {
                changeable.change(delta);
            }
        }
    }
    
    protected List<Movable> getMovableProjectors() {
        List<Movable> result = new ArrayList<Movable>();
        for (Projector projector : projectors) {
            if (projector instanceof Movable) {
                result.add((Movable) projector);
            }
        }
        return result;
    }
    
    protected List<Changeable> getChangeableProjectors() {
        List<Changeable> result = new ArrayList<Changeable>();
        for (Projector projector : projectors) {
            if (projector instanceof Changeable) {
                result.add((Changeable) projector);
            }
        }
        return result;
    }
}
