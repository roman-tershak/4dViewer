package com.viewer4d.projector.from4dto3d;

import com.viewer4d.geometry.Vertex;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Changeable;

public class FrontIsometricProjectorOnXYZAlongW extends AbstractProjectingProjector implements Changeable {

    protected static final double W_KOEF_DEFAULT = 0.5 / Math.sqrt(2);
    protected static final double MIN_W_KOEF = 0.0;
    protected static final double MAX_W_KOEF = 2 * W_KOEF_DEFAULT;
    
    private double wKoef = W_KOEF_DEFAULT;

    public FrontIsometricProjectorOnXYZAlongW() {
    }

    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double[] coords = vertex.getCoords();
        
        double wShift = coords[3] * wKoef;
        
        double nx = coords[0] - wShift;
        double ny = coords[1] + wShift;
        double nz = coords[2] + wShift;
        double nw = coords[3];

        return new Vertex(nx, ny, nz, nw);
    }

    @Override
    public void change(int delta) {
        double storeWKoef = wKoef;
        wKoef += (double)delta / 100;
        if (wKoef < MIN_W_KOEF || wKoef > MAX_W_KOEF) {
            wKoef = storeWKoef;
        }
    }
}
