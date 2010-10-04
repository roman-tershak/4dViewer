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

public class CombinedAuxAndMainProjectors<P extends Projector> extends AbstractEnablingProjector {

    private Projector projector;
    private List<P> auxProjectors = new ArrayList<P>();
    
    public CombinedAuxAndMainProjectors(P projector, P... projectors) {
        this.projector = projector;
        this.auxProjectors.addAll(Arrays.asList(projectors));
    }

    public Projector getProjector() {
        return projector;
    }
    
    public void setProjector(Projector projector) {
        this.projector = projector;
    }
    
    public List<P> getAuxProjectors() {
        return auxProjectors;
    }
    
    @Override
    public Figure project(Figure figure) {
        List<Edge> newEdges = new LinkedList<Edge>();
        
        if (isEnabled()) {
            newEdges.addAll(
                    projectFigure(figure).getEdges());
        }
        
        for (Projector auxProjector : auxProjectors) {
            Figure auxProjection = auxProjector.project(figure);
            newEdges.addAll(auxProjection.getEdges());
        }
        
        Figure combined = new FigureBaseImpl(null, newEdges);
        return projector.project(combined);
    }

    @Override
    protected Figure projectFigure(Figure figure) {
        return figure;
    }
}
