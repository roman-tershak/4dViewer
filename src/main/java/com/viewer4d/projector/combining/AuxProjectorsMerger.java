package com.viewer4d.projector.combining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;
import com.viewer4d.projector.Projector;

public class AuxProjectorsMerger<P extends Projector> extends AbstractEnablingProjector {

    private List<P> auxProjectors = new ArrayList<P>();
    
    public AuxProjectorsMerger(P... projectors) {
        this.auxProjectors.addAll(Arrays.asList(projectors));
    }

    public List<P> getAuxProjectors() {
        return auxProjectors;
    }
    
    @Override
    public Figure project(Figure figure) {
        List<Edge> newEdges = new LinkedList<Edge>();
        
        if (isEnabled()) {
            newEdges.addAll(figure.getEdges());
        }
        
        for (Projector auxProjector : auxProjectors) {
            Figure auxProjection = auxProjector.project(figure);
            newEdges.addAll(auxProjection.getEdges());
        }
        
        return projectFigure(new FigureBaseImpl(null, newEdges));
    }

    @Override
    protected Figure projectFigure(Figure figure) {
        return figure;
    }
}
