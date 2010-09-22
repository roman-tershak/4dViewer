package com.viewer4d.projector.from3dto2d;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.projector.AbstractProjectingProjector;

public class Perspective2DMonoProjector<P extends Pointable> extends AbstractProjectingProjector {

    private static final double XY_CLIPPING_LIMIT = 1.0D;
    
    private P c;
    private P xt;
    private P yt;
    private P zt;
    private double fov;
    private double xyClippingLimit;
    
    private double[] m;

    public Perspective2DMonoProjector(P c, P xt, P yt, P zt, 
            double fov, double xyClippingLimit) {
        this.c = c;
        this.xt = xt;
        this.yt = yt;
        this.zt = zt;
        this.fov = fov;
        this.xyClippingLimit = xyClippingLimit;
        precalculateTransMatrix();
    }

    public Perspective2DMonoProjector(P c, 
            P xt, P yt, P zt, double fov) {
        this(c, xt, yt, zt, fov, XY_CLIPPING_LIMIT);
    }

    public P getC() {
        return c;
    }
    public void setC(P c) {
        this.c = c;
        precalculateTransMatrix();
    }
    
    public P getXt() {
        return xt;
    }
    public void setXt(P xt) {
        this.xt = xt;
        precalculateTransMatrix();
    }
    
    public P getYt() {
        return yt;
    }
    public void setYt(P yt) {
        this.yt = yt;
        precalculateTransMatrix();
    }
    
    public P getZt() {
        return zt;
    }
    public void setZt(P zt) {
        this.zt = zt;
        precalculateTransMatrix();
    }
    
    public double getFov() {
        return fov;
    }
    public void setFov(double fov) {
        this.fov = fov;
    }
    
    public void precalculateTransMatrix() {
        double[] xtCoords = xt.getCoords();
        double[] ytCoords = yt.getCoords();
        double[] ztCoords = zt.getCoords();
        double[] cCoords = c.getCoords();
        m = new double[] {
                xtCoords[0], ytCoords[0], ztCoords[0],
                xtCoords[1], ytCoords[1], ztCoords[1],
                xtCoords[2], ytCoords[2], ztCoords[2],
                -(cCoords[0] * xtCoords[0] + cCoords[1] * xtCoords[1] + cCoords[2] * xtCoords[2]),
                -(cCoords[0] * ytCoords[0] + cCoords[1] * ytCoords[1] + cCoords[2] * ytCoords[2]),
                -(cCoords[0] * ztCoords[0] + cCoords[1] * ztCoords[1] + cCoords[2] * ztCoords[2])
        };
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        return doXYClipping(
                super.projectEdge(edge), 
                edge);
    }
    
    @Override
    protected Vertex projectVertex(Vertex vertex) {
        double[] tCoords = transform(vertex.getCoords());
        
        tCoords[0] = tCoords[0] / tCoords[2] / fov;
        tCoords[1] = tCoords[1] / tCoords[2] / fov;
        
        return new Vertex(tCoords);
    }
    
    private double[] transform(double[] coords) {
        double px = coords[0];
        double py = coords[1];
        double pz = coords[2];
        double pw = coords[3];
        
        double nx = px * m[0] + py * m[3] + pz * m[6] + m[9];
        double ny = px * m[1] + py * m[4] + pz * m[7] + m[10];
        double nz = px * m[2] + py * m[5] + pz * m[8] + m[11];
        
        return new double[] {nx, ny, nz, pw};
    }

    private Edge doXYClipping(Edge edge, Edge origin) {
        double[] aCoords = edge.getA().getCoords();
        double[] bCoords = edge.getB().getCoords();
        
        boolean xaR = aCoords[0] > xyClippingLimit;
        boolean xaL = aCoords[0] < -xyClippingLimit;
        boolean yaR = aCoords[1] > xyClippingLimit;
        boolean yaL = aCoords[1] < -xyClippingLimit;
        boolean xbR = bCoords[0] > xyClippingLimit;
        boolean xbL = bCoords[0] < -xyClippingLimit;
        boolean ybR = bCoords[1] > xyClippingLimit;
        boolean ybL = bCoords[1] < -xyClippingLimit;
        
        boolean isAIn = !(xaR || xaL || yaR || yaL);
        boolean isBIn = !(xbR || xbL || ybR || ybL);
        
        if (isAIn && isBIn) {
            return edge;
        }
        
        double[] aCCoords = null;
        double[] bCCoords = null;
        if (isAIn) {
            aCCoords = aCoords;
        } else {
            if ((xaR && !xbR) || (xaL && !xbL)) {
                aCCoords = doXClipping(xaR ? xyClippingLimit : -xyClippingLimit, aCoords, bCoords);
            }
            if (aCCoords == null && ((yaR && !ybR) || (yaL && !ybL))) {
                aCCoords = doYClipping(yaR ? xyClippingLimit : -xyClippingLimit, aCoords, bCoords);
            }
            removeProjectedVertex(origin.getA());
        }
        if (isBIn) {
            bCCoords = bCoords;
        } else {
            if ((xbR && !xaR) || (xbL && !xaL)) {
                bCCoords = doXClipping(xbR ? xyClippingLimit : -xyClippingLimit, bCoords, aCoords);
            }
            if (bCCoords == null && ((ybR && !yaR) || (ybL && !yaL))) {
                bCCoords = doYClipping(ybR ? xyClippingLimit : -xyClippingLimit, bCoords, aCoords);
            }
            removeProjectedVertex(origin.getB());
        }
        if (aCCoords != null && bCCoords != null) {
            return edge;
        } else {
            return null;
        }
    }

    private double[] doXClipping(double xcLimit, double[] coords1, double[] coords2) {
        double x1 = coords1[0];
        double y1 = coords1[1];
        double z1 = coords1[2];
        double w1 = coords1[3];

        double x2 = coords2[0];
        double y2 = coords2[1];
        double z2 = coords2[2];
        double w2 = coords2[3];

        double yc = (xcLimit - x2) * (y1 - y2)/(x1 - x2) + y2;
        if (Math.abs(xyClippingLimit - Math.abs(yc)) < Pointable.PRECISION) {
            yc = yc >= 0 ? xyClippingLimit : -xyClippingLimit;
        }
        if (yc <= xyClippingLimit && yc >= -xyClippingLimit) {
            coords1[0] = xcLimit;
            coords1[1] = yc;
            coords1[2] = (xcLimit - x2) * (z1 - z2)/(x1 - x2) + z2;
            coords1[3] = (xcLimit - x2) * (w1 - w2)/(x1 - x2) + w2;
            
            return coords1;
        } else {
            return null;
        }
    }

    private double[] doYClipping(double ycLimit, double[] coords1, double[] coords2) {
        double x1 = coords1[0];
        double y1 = coords1[1];
        double z1 = coords1[2];
        double w1 = coords1[3];

        double x2 = coords2[0];
        double y2 = coords2[1];
        double z2 = coords2[2];
        double w2 = coords2[3];
        
        double xc = (ycLimit - y2) * (x1 - x2)/(y1 - y2) + x2;
        if (Math.abs(xyClippingLimit - Math.abs(xc)) < Pointable.PRECISION) {
            xc = xc >= 0 ? xyClippingLimit : -xyClippingLimit;
        }
        if (xc <= xyClippingLimit && xc >= -xyClippingLimit) {
            coords1[0] = xc;
            coords1[1] = ycLimit;
            coords1[2] = (ycLimit - y2) * (z1 - z2)/(y1 - y2) + z2;
            coords1[3] = (ycLimit - y2) * (w1 - w2)/(y1 - y2) + w2;
            
            return coords1;
        } else {
            return null;
        }
    }

}
