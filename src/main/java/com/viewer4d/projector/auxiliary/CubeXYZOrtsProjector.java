package com.viewer4d.projector.auxiliary;

import java.util.Arrays;
import java.util.HashSet;

import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Face;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.Vertex;
import com.viewer4d.geometry.figure.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;

public class CubeXYZOrtsProjector extends AbstractEnablingProjector {

    protected static final double EDGE_LENGHT = 0.5;
    
    protected final Vertex VA1 = new Vertex(EDGE_LENGHT, EDGE_LENGHT, EDGE_LENGHT, 0);
    protected final Vertex VA2 = new Vertex(-EDGE_LENGHT, EDGE_LENGHT, EDGE_LENGHT, 0);
    protected final Vertex VA3 = new Vertex(-EDGE_LENGHT, -EDGE_LENGHT, EDGE_LENGHT, 0);
    protected final Vertex VA4 = new Vertex(EDGE_LENGHT, -EDGE_LENGHT, EDGE_LENGHT, 0);
                              
    protected final Vertex VB1 = new Vertex(EDGE_LENGHT, EDGE_LENGHT, -EDGE_LENGHT, 0);
    protected final Vertex VB2 = new Vertex(-EDGE_LENGHT, EDGE_LENGHT, -EDGE_LENGHT, 0);
    protected final Vertex VB3 = new Vertex(-EDGE_LENGHT, -EDGE_LENGHT, -EDGE_LENGHT, 0);
    protected final Vertex VB4 = new Vertex(EDGE_LENGHT, -EDGE_LENGHT, -EDGE_LENGHT, 0);
    
    protected final Edge A1A2 = new Edge(VA1, VA2);
    protected final Edge A2A3 = new Edge(VA2, VA3);
    protected final Edge A3A4 = new Edge(VA3, VA4);
    protected final Edge A4A1 = new Edge(VA4, VA1);
                                                 
    protected final Edge B1B2 = new Edge(VB1, VB2);
    protected final Edge B2B3 = new Edge(VB2, VB3);
    protected final Edge B3B4 = new Edge(VB3, VB4);
    protected final Edge B4B1 = new Edge(VB4, VB1);
                                                 
    protected final Edge A1B1 = new Edge(VA1, VB1);
    protected final Edge A2B2 = new Edge(VA2, VB2);
    protected final Edge A3B3 = new Edge(VA3, VB3);
    protected final Edge A4B4 = new Edge(VA4, VB4);
    
    protected final Face AAAA = new Face(A1A2, A2A3, A3A4, A4A1);
    protected final Face BBBB = new Face(B1B2, B2B3, B3B4, B4B1);
    protected final Face AABB = new Face(A1A2, A1B1, B1B2, A2B2);
    protected final Face ABBA = new Face(A2A3, A2B2, B2B3, A3B3);
    protected final Face BBAA = new Face(A3A4, A3B3, B3B4, A4B4);
    protected final Face BAAB = new Face(A4A1, A4B4, B4B1, A1B1);
    
    protected final Figure XYZ_CUBE = new FigureBaseImpl( 
            new HashSet<Face>(Arrays.asList(AAAA, BBBB, AABB, ABBA, BBAA, BAAB)),
            null);
    
    public CubeXYZOrtsProjector(boolean enabled) {
        super(enabled);
    }

    @Override
    protected Figure projectFigure(Figure figure) {
        return XYZ_CUBE;
    }

}
