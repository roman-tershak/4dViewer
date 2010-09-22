package com.viewer4d.projector.from4dto3d;

import java.util.HashSet;
import java.util.Set;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.FigureMovable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureMovableImpl;
import com.viewer4d.projector.Projector;

@Deprecated
public class IsometricProjectorOnXYZ implements Projector {

    private static final double XW_PROJECTION_RAD = Math.PI/2;
    private static final double YW_PROJECTION_RAD = 0.2;
    private static final double ZW_PROJECTION_RAD = 0.4;

    public IsometricProjectorOnXYZ() {
    }

    @Override
    public Figure project(Figure figure) {
        Set<Edge> newEdges = new HashSet<Edge>();
        for (Edge edge : figure.getEdges()) {
            newEdges.add(new Edge(
                    new Vertex(edge.getA().getCoords()), 
                    new Vertex(edge.getB().getCoords())));
        }
        FigureMovable figureMovable = new FigureMovableImpl(null, newEdges);
        
        figureMovable.rotate(RotationPlane4DEnum.XW, XW_PROJECTION_RAD);
        figureMovable.rotate(RotationPlane4DEnum.YW, YW_PROJECTION_RAD);
        figureMovable.rotate(RotationPlane4DEnum.ZW, ZW_PROJECTION_RAD);
        
        return figureMovable;
    }
    
}
