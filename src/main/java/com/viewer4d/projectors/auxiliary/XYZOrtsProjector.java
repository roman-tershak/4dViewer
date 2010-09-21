package com.viewer4d.projectors.auxiliary;

import java.util.Arrays;
import java.util.HashSet;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projectors.AbstractEnablingProjector;

public class XYZOrtsProjector extends AbstractEnablingProjector {

    protected static final int ORT_LEGHT = 100000;
    
    protected final Vertex XA = new Vertex(ORT_LEGHT, 0, 0, 0); 
    protected final Vertex XB = new Vertex(-ORT_LEGHT, 0, 0, 0);
    protected final Vertex YA = new Vertex(0, ORT_LEGHT, 0, 0);
    protected final Vertex YB = new Vertex(0, -ORT_LEGHT, 0, 0);
    protected final Vertex ZA = new Vertex(0, 0, ORT_LEGHT, 0);
    protected final Vertex ZB = new Vertex(0, 0, -ORT_LEGHT, 0);
    
    protected final Edge X = new Edge(XA, XB);
    protected final Edge Y = new Edge(YA, YB);
    protected final Edge Z = new Edge(ZA, ZB);
    
    protected final Figure XYZ_ORTS = new FigureBaseImpl(null, 
            new HashSet<Edge>(Arrays.asList(X, Y, Z)));
    
    @Override
    protected Figure projectFigure(Figure figure) {
        return XYZ_ORTS;
    }

}
