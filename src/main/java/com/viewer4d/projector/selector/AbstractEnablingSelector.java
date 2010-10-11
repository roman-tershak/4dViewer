package com.viewer4d.projector.selector;

import static com.viewer4d.geometry.Selection.*;

import java.util.LinkedList;
import java.util.List;

import com.viewer4d.geometry.Selection;
import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;



public abstract class AbstractEnablingSelector extends AbstractEnablingProjector {

    private boolean notCuttingSelected;
    
    public AbstractEnablingSelector(boolean notCuttingSelected) {
        super();
        this.notCuttingSelected = notCuttingSelected;
    }

    public AbstractEnablingSelector() {
        this(false);
    }
    
    public boolean isNotCuttingSelected() {
        return notCuttingSelected;
    }
    public void setNotCuttingSelected(boolean notCuttingSelected) {
        this.notCuttingSelected = notCuttingSelected;
    }

    @Override
    protected Figure projectFigure(Figure figure) {
        List<Edge> newEdges = new LinkedList<Edge>();
        for (Edge edge : figure.getEdges()) {
            
            Selection edgeSelection = edge.getSelection();
            if (isNotCuttingSelected() && 
                    (edgeSelection != null && edgeSelection != NOTSELECTED)) {
                newEdges.add(edge);
            } else {
                Edge projectEdge = projectEdge(edge);
                if (projectEdge != null) {
                    if (projectEdge != edge) {
                        projectEdge.getFaces().addAll(edge.getFaces());
                        projectEdge.setSelection(edgeSelection);
                    }
                    newEdges.add(projectEdge);
                }
            }
        }
        return new FigureBaseImpl(null, newEdges);
    }

    protected abstract Edge projectEdge(Edge edge);
}
