package com.viewer4d.projector.from4dto3d;

import com.viewer4d.geometry.Vertex;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Changeable;

public class PerspectiveProjectorOnXYZAlongW extends AbstractProjectingProjector implements Changeable {

    protected static final double MIN_4D_DISTANCE = 3.3333333;
    
    private double w;
    
    public PerspectiveProjectorOnXYZAlongW(double w) {
        this.w = w;
    }

    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double[] coords = vertex.getCoords();

        double wKoef = w / (w - coords[3]);
        double nx = coords[0] * wKoef;
        double ny = coords[1] * wKoef;
        double nz = coords[2] * wKoef;
        double nw = coords[3];

        return new Vertex(nx, ny, nz, nw);
    }
    
    @Override
    public void change(int delta) {
        double storeW = w;
        double koef = delta < 0 ? 1.02 : 0.98;
        w *= koef;
        if (Math.abs(w) < MIN_4D_DISTANCE) {
            w = storeW;
        }
    }
}
