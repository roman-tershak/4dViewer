package com.viewer4d.projector.combining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Movable;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;
import com.viewer4d.projector.Changeable;
import com.viewer4d.projector.Enabling;
import com.viewer4d.projector.Projector;

public class CombiningProjector<P extends Projector> extends AbstractEnablingProjector 
implements Changeable {

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
    
    public void reset() {
        for (Movable movable : getMovableProjectors()) {
            movable.reset();
        }
        for (Changeable changeable : getChangeableProjectors()) {
            changeable.reset();
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
