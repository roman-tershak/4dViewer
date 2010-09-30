package com.viewer4d.projector.selector;

import java.util.LinkedList;
import java.util.List;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;



public abstract class AbstractEnablingSelector extends AbstractEnablingProjector {

    @Override
    protected Figure projectFigure(Figure figure) {
        List<Edge> newEdges = new LinkedList<Edge>();
        for (Edge edge : figure.getEdges()) {
            
            Edge projectEdge = projectEdge(edge);
            if (projectEdge != null) {
                projectEdge.setSelection(edge.getSelection());
                newEdges.add(projectEdge);
            }
        }
        return new FigureBaseImpl(null, newEdges);
    }

    protected abstract Edge projectEdge(Edge edge);
}
