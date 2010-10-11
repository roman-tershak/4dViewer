package com.viewer4d.projector.selector;

import java.util.HashMap;
import java.util.Map;

import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Vertex;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;

public class SelectorCuttingHalfOfOrt extends AbstractEnablingSelector {

    private static interface Clipper {
        double[] clipCoords(double[] coords1, double[] coords2);
    }
    
    private static final Map<UNIT_VECTORS, Clipper> clippers;
    static {
        clippers = new HashMap<UNIT_VECTORS, Clipper>();
        clippers.put(UNIT_VECTORS.X, new ClipperByX());
        clippers.put(UNIT_VECTORS.Y, new ClipperByY());
        clippers.put(UNIT_VECTORS.Z, new ClipperByZ());
        clippers.put(UNIT_VECTORS.W, new ClipperByW());
    }
    
    // Instance members
    private UNIT_VECTORS unitVector;
    private boolean negative;

    public SelectorCuttingHalfOfOrt(UNIT_VECTORS unitVector, boolean negative) {
        this.unitVector = unitVector;
        this.negative = negative;
    }
    
    public void setUnitVector(UNIT_VECTORS unitVector) {
        this.unitVector = unitVector;
    }
    public void setNegative(boolean negative) {
        this.negative = negative;
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        double[] aCoords = edge.getA().getCoords();
        double[] bCoords = edge.getB().getCoords();
        
        double aClipCoord = 0;
        double bClipCoord = 0;
        switch (unitVector) {
        case X:
            aClipCoord = aCoords[0];
            bClipCoord = bCoords[0];
            break;
        case Y:
            aClipCoord = aCoords[1];
            bClipCoord = bCoords[1];
            break;
        case Z:
            aClipCoord = aCoords[2];
            bClipCoord = bCoords[2];
            break;
        case W:
            aClipCoord = aCoords[3];
            bClipCoord = bCoords[3];
            break;
        }
        if (!negative) {
            aClipCoord = -aClipCoord;
            bClipCoord = -bClipCoord;
        }
        
        Clipper clipper = clippers.get(unitVector);
        
        return clipEdge(edge, aClipCoord, bClipCoord, clipper);
    }

    private Edge clipEdge(Edge edge, double aClipCoord, double bClipCoord, Clipper clipper) {
        Vertex a = edge.getA();
        Vertex b = edge.getB();
        
        double[] aCoords = a.getCoords();
        double[] bCoords = b.getCoords();
        
        if (aClipCoord >= 0 && bClipCoord >= 0) {
            return edge;
            
        } else if (aClipCoord < 0 && bClipCoord >= 0) {
            return new Edge(
                    new Vertex(clipper.clipCoords(aCoords, bCoords)), 
                    b);
            
        } else if (aClipCoord >= 0 && bClipCoord < 0) {
            return new Edge(
                    a, 
                    new Vertex(clipper.clipCoords(bCoords, aCoords)));
            
        } else {
            return null;
        }
    }

    // Clippers
    private static class ClipperByX implements Clipper {
        @Override
        public double[] clipCoords(double[] coords1, double[] coords2) {
            double k = coords2[0]/(coords2[0] - coords1[0]);
            
            double xn = 0;
            double yn = (coords1[1] - coords2[1]) * k + coords2[1];
            double zn = (coords1[2] - coords2[2]) * k + coords2[2];
            double wn = (coords1[3] - coords2[3]) * k + coords2[3];
            
            return new double[] {xn, yn, zn, wn};
        }
    }
    
    private static class ClipperByY implements Clipper {
        @Override
        public double[] clipCoords(double[] coords1, double[] coords2) {
            double k = coords2[1]/(coords2[1] - coords1[1]);
            
            double xn = (coords1[0] - coords2[0]) * k + coords2[0];
            double yn = 0;
            double zn = (coords1[2] - coords2[2]) * k + coords2[2];
            double wn = (coords1[3] - coords2[3]) * k + coords2[3];
            
            return new double[] {xn, yn, zn, wn};
        }
    }
    
    private static class ClipperByZ implements Clipper {
        @Override
        public double[] clipCoords(double[] coords1, double[] coords2) {
            double k = coords2[2]/(coords2[2] - coords1[2]);
            
            double xn = (coords1[0] - coords2[0]) * k + coords2[0];
            double yn = (coords1[1] - coords2[1]) * k + coords2[1];
            double zn = 0;
            double wn = (coords1[3] - coords2[3]) * k + coords2[3];
            
            return new double[] {xn, yn, zn, wn};
        }
    }
    
    private static class ClipperByW implements Clipper {
        @Override
        public double[] clipCoords(double[] coords1, double[] coords2) {
            double k = coords2[3]/(coords2[3] - coords1[3]);
            
            double xn = (coords1[0] - coords2[0]) * k + coords2[0];
            double yn = (coords1[1] - coords2[1]) * k + coords2[1];
            double zn = (coords1[2] - coords2[2]) * k + coords2[2];
            double wn = 0;
            
            return new double[] {xn, yn, zn, wn};
        }
    }
    
}
