package com.viewer4d.projector.selector;

import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Figure;

public class EntireFigureSelector extends AbstractEnablingSelector {

    public EntireFigureSelector(boolean enabled) {
        super(enabled);
    }
    
    @Override
    protected Figure projectFigure(Figure figure) {
        return figure;
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        return edge;
    }

}
