package com.viewer4d.projectors;

import java.util.HashSet;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.impl.FigureBaseImpl;


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
