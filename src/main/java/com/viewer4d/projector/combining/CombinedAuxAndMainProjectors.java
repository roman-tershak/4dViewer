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
import com.viewer4d.projector.from4dto3d.PerspectiveProjectorOnXYZAlongW;
import com.viewer4d.projector.selector.Simple3DSpaceIntersectorAtZeroW;

public class CombinedAuxAndMainProjectors extends AbstractEnablingProjector {

    private Projector projector;
    private List<Projector> auxProjectors = new ArrayList<Projector>();
    
    public CombinedAuxAndMainProjectors(double w) {
        this.projector = new PerspectiveProjectorOnXYZAlongW(w);
        this.auxProjectors.add(new Simple3DSpaceIntersectorAtZeroW());
    }
    
    public CombinedAuxAndMainProjectors(Projector projector) {
        this.projector = projector;
        this.auxProjectors.add(new Simple3DSpaceIntersectorAtZeroW());
    }

    public CombinedAuxAndMainProjectors(Projector projector, Projector... projectors) {
        this.projector = projector;
        this.auxProjectors.addAll(Arrays.asList(projectors));
    }

    public Projector getProjector() {
        return projector;
    }
    
    public void setProjector(Projector projector) {
        this.projector = projector;
    }
    
    public List<Projector> getAuxProjectors() {
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
