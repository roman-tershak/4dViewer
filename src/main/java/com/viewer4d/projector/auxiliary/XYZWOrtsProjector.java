package com.viewer4d.projector.auxiliary;

import java.util.Arrays;
import java.util.HashSet;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureBaseImpl;
import com.viewer4d.projector.AbstractEnablingProjector;

public class XYZWOrtsProjector extends AbstractEnablingProjector {

    protected static final double ORT_LEN = 2.2;
    
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
    
    protected final Edge LX11 = new Edge(new Vertex(ORT_LEN-0.06, 0, 0.12, 0), new Vertex(ORT_LEN,      0, 0.03, 0));
    protected final Edge LX12 = new Edge(new Vertex(ORT_LEN,      0, 0.12, 0), new Vertex(ORT_LEN-0.06, 0, 0.03, 0));
    protected final Edge LX21 = new Edge(new Vertex(ORT_LEN-0.06, 0, 0, 0.12), new Vertex(ORT_LEN,      0, 0, 0.03));
    protected final Edge LX22 = new Edge(new Vertex(ORT_LEN,      0, 0, 0.12), new Vertex(ORT_LEN-0.06, 0, 0, 0.03));
    
    protected final Edge LY11 = new Edge(new Vertex(-0.11, ORT_LEN, 0, 0), new Vertex(-0.08, ORT_LEN-0.045, 0, 0));
    protected final Edge LY12 = new Edge(new Vertex(-0.05, ORT_LEN, 0, 0), new Vertex(-0.11, ORT_LEN-0.09,  0, 0));
    protected final Edge LY21 = new Edge(new Vertex(0, ORT_LEN, 0,  0.11), new Vertex(0, ORT_LEN-0.045, 0,  0.08));
    protected final Edge LY22 = new Edge(new Vertex(0, ORT_LEN, 0,  0.05), new Vertex(0, ORT_LEN-0.09,  0,  0.11));

    protected final Edge LZ11 = new Edge(new Vertex(0, 0.05, ORT_LEN,      0), new Vertex(0, 0.11, ORT_LEN,      0));
    protected final Edge LZ12 = new Edge(new Vertex(0, 0.11, ORT_LEN,      0), new Vertex(0, 0.05, ORT_LEN-0.09, 0));
    protected final Edge LZ13 = new Edge(new Vertex(0, 0.05, ORT_LEN-0.09, 0), new Vertex(0, 0.11, ORT_LEN-0.09, 0));
    protected final Edge LZ21 = new Edge(new Vertex(0, 0, ORT_LEN,      0.05), new Vertex(0, 0, ORT_LEN,      0.11));
    protected final Edge LZ22 = new Edge(new Vertex(0, 0, ORT_LEN,      0.11), new Vertex(0, 0, ORT_LEN-0.09, 0.05));
    protected final Edge LZ23 = new Edge(new Vertex(0, 0, ORT_LEN-0.09, 0.05), new Vertex(0, 0, ORT_LEN-0.09, 0.11));
    
    protected final Figure XYZW_ORTS = new FigureBaseImpl(null, 
            new HashSet<Edge>(Arrays.asList(X, Y, Z, W, 
                    LX11, LX12, LX21, LX22, LY11, LY12, LY21, LY22, LZ11, LZ12, LZ13, LZ21, LZ22, LZ23)));
    
    public XYZWOrtsProjector(boolean enabled) {
        super(enabled);
    }
    
    @Override
    protected Figure projectFigure(Figure figure) {
        return XYZW_ORTS;
    }

}
