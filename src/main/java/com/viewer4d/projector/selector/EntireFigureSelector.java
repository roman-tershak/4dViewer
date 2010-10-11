package com.viewer4d.projector.selector;

import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Figure;

public class EntireFigureSelector extends AbstractEnablingSelector {

    @Override
    protected Figure projectFigure(Figure figure) {
        return figure;
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        return edge;
    }

}
