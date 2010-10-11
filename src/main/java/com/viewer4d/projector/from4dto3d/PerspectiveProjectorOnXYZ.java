package com.viewer4d.projector.from4dto3d;

import com.viewer4d.geometry.Transformer;
import com.viewer4d.geometry.figure.Vertex;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Changeable;

public class PerspectiveProjectorOnXYZ extends AbstractProjectingProjector implements Transformer, Changeable {

    protected static final double MIN_4D_DISTANCE = 3.3333333;
    
    private double wPos;
    private double storeWPos;
    private boolean perspective;
    private boolean colorRelToWOrt;
    

    public PerspectiveProjectorOnXYZ(double wPos, boolean perspective, boolean colorRelToW) {
        this.wPos = wPos;
        this.storeWPos = wPos;
        this.perspective = perspective;
        this.colorRelToWOrt = colorRelToW;
    }

    public PerspectiveProjectorOnXYZ(double wPos, boolean perspective) {
        this(wPos, perspective, true);
    }

    public double getProjectorDistance() {
        return Math.abs(wPos);
    }
    protected void setProjectorDistance(double distance) {
        wPos = wPos < 0 ? -distance : distance;
    }
    
    public boolean isPerspective() {
        return perspective;
    }
    public void setPerspective(boolean perspective) {
        this.perspective = perspective;
    }
    
    public boolean isColorRelToWOrt() {
        return colorRelToWOrt;
    }
    public void setColorRelToWOrt(boolean colorRelToWOrt) {
        this.colorRelToWOrt = colorRelToWOrt;
    }
    
    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double nx, ny, nz, nw;
        
        double[] vCoords = vertex.getCoords();
        double[] tCoords = transform(vCoords);
        
        double perspKoef;
        double distance = getProjectorDistance();
        if (isPerspective()) {
            perspKoef = distance / tCoords[3];
        } else {
            perspKoef = wPos < 0 ? 1 : -1;
        }
        
        nx = tCoords[0] * perspKoef;
        ny = tCoords[1] * perspKoef;
        nz = tCoords[2] * perspKoef;
        nw = isColorRelToWOrt() ? vCoords[3] : (tCoords[3] - distance);
        
        return new Vertex(nx, ny, nz, nw);
    }
    
    public double[] transform(double[] coords) {
        return new double[] {coords[0], coords[1], coords[2], coords[3] - wPos};
    }
    
    @Override
    public void change(int delta) {
        double koef = delta < 0 ? 1.02 : 0.98;
        double dist = getProjectorDistance();
        dist *= koef;
        if (Math.abs(dist) >= MIN_4D_DISTANCE) {
            setProjectorDistance(dist);
        }
    }
    
    @Override
    public void reset() {
        wPos = storeWPos;
    }
}
