package com.viewer4d.projector;

import java.util.HashSet;

import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.impl.FigureBaseImpl;


public abstract class AbstractEnablingProjector implements Projector, Enabling {

    protected final Figure EMPTY_FIGURE = new FigureBaseImpl(null, new HashSet<Edge>());
    
    private boolean enabled;
    
    public AbstractEnablingProjector() {
        this(true);
    }
    
    public AbstractEnablingProjector(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Figure project(Figure figure) {
        if (isEnabled()) {
            return projectFigure(figure);
        } else {
            return EMPTY_FIGURE;
        }
    }

    protected abstract Figure projectFigure(Figure figure);
    
}
