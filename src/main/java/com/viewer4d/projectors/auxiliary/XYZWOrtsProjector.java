package com.viewer4d.projectors.auxiliary;

import java.util.Arrays;
import java.util.HashSet;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projectors.AbstractEnablingProjector;

public class XYZWOrtsProjector extends AbstractEnablingProjector {

    protected static final double ORT_LEN = 2.05;
    
    protected final Vertex XA = new Vertex(ORT_LEN, 0, 0, 0); 
    protected final Vertex XB = new Vertex(-ORT_LEN, 0, 0, 0);
    protected final Vertex YA = new Vertex(0, ORT_LEN, 0, 0);
    protected final Vertex YB = new Vertex(0, -ORT_LEN, 0, 0);
    protected final Vertex ZA = new Vertex(0, 0, ORT_LEN, 0);
    protected final Vertex ZB = new Vertex(0, 0, -ORT_LEN, 0);
    protected final Vertex WA = new Vertex(0, 0, 0, ORT_LEN);
    protected final Vertex WB = new Vertex(0, 0, 0, -ORT_LEN);
    
    protected final Edge X = new Edge(XA, XB);
    protected final Edge Y = new Edge(YA, YB);
    protected final Edge Z = new Edge(ZA, ZB);
    protected final Edge W = new Edge(WA, WB);
    
    protected final Edge LX1 = new Edge(new Vertex(ORT_LEN-0.06, 0, 0.12, 0), new Vertex(ORT_LEN, 0, 0.03, 0));
    protected final Edge LX2 = new Edge(new Vertex(ORT_LEN, 0, 0.12, 0), new Vertex(ORT_LEN-0.06, 0, 0.03, 0));
    protected final Edge LY1 = new Edge(new Vertex(-0.11, ORT_LEN, 0, 0), new Vertex(-0.08, ORT_LEN-0.045, 0, 0));
    protected final Edge LY2 = new Edge(new Vertex(-0.05, ORT_LEN, 0, 0), new Vertex(-0.11, ORT_LEN-0.09, 0, 0));
    protected final Edge LZ1 = new Edge(new Vertex(0, 0.05, ORT_LEN, 0), new Vertex(0, 0.11, ORT_LEN, 0));
    protected final Edge LZ2 = new Edge(new Vertex(0, 0.11, ORT_LEN, 0), new Vertex(0, 0.05, ORT_LEN-0.09, 0));
    protected final Edge LZ3 = new Edge(new Vertex(0, 0.05, ORT_LEN-0.09, 0), new Vertex(0, 0.11, ORT_LEN-0.09, 0));
    
    protected final Figure XYZW_ORTS = new FigureBaseImpl(null, 
            new HashSet<Edge>(Arrays.asList(X, Y, Z, W, LX1, LX2, LY1, LY2, LZ1, LZ2, LZ3)));
    
    public XYZWOrtsProjector(boolean enabled) {
        super(enabled);
    }
    
    @Override
    protected Figure projectFigure(Figure figure) {
        return XYZW_ORTS;
    }

}
