package com.viewer4d.projectors.from4dto3d;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Vertex;

public class PerspectiveProjectorOnXYZCuttingW extends PerspectiveProjectorOnXYZAlongW {

    public PerspectiveProjectorOnXYZCuttingW(double w) {
        super(w);
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        Vertex a = edge.getA();
        Vertex b = edge.getB();
        
        double[] aCoords = a.getCoords();
        double[] bCoords = b.getCoords();
        
        if (aCoords[3] >= 0 && bCoords[3] >= 0) {
            return super.projectEdge(edge);
            
        } else if (aCoords[3] < 0 && bCoords[3] >= 0) {
            return super.projectEdge(new Edge(
                    new Vertex(clipCoordsByW(aCoords, bCoords)), 
                    b));
            
        } else if (aCoords[3] >= 0 && bCoords[3] < 0) {
            return super.projectEdge(new Edge(
                    a, 
                    new Vertex(clipCoordsByW(bCoords, aCoords))));
            
        } else {
            return null;
        }
    }

    private double[] clipCoordsByW(double[] coords1, double[] coords2) {
        double x1 = coords1[0];
        double y1 = coords1[1];
        double z1 = coords1[2];
        double w1 = coords1[3];
        
        double x2 = coords2[0];
        double y2 = coords2[1];
        double z2 = coords2[2];
        double w2 = coords2[3];
        
        double xn = (x1 - x2) * w2/(w2 - w1) + x2;
        double yn = (y1 - y2) * w2/(w2 - w1) + y2;
        double zn = (z1 - z2) * w2/(w2 - w1) + z2;
        double wn = 0;
        
        return new double[] {xn, yn, zn, wn};
    }
}
