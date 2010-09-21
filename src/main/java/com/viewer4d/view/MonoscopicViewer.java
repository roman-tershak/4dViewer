package com.viewer4d.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.projectors.from3dto2d.Perspective2DMonoProjector;

public class MonoscopicViewer extends AbstractViewer {
    
    static final double XY_CLIPPING_LIMIT = 1.0;
    
    private Perspective2DMonoProjector<MovablePoint> perspective2dMonoProjector;
    private Figure projectedFigure;

    public MonoscopicViewer() {
        initProjector(ViewContainer.CAMERA_DISTANCE_DEFAULT, ViewContainer.CAMERA_FOV_DEFAULT);
    }
    
    private void initProjector(double distance, double fov) {
        this.perspective2dMonoProjector = new Perspective2DMonoProjector<MovablePoint>(
                new MovablePoint(distance, 0, 0), 
                new MovablePoint(XT), 
                new MovablePoint(YT), 
                new MovablePoint(ZT), 
                fov, XY_CLIPPING_LIMIT);
    }

    private void setProjectorsOnX(double distance, double fov) {
        this.perspective2dMonoProjector.getC().setCoords(new double[] {distance, 0, 0}); 
        this.perspective2dMonoProjector.getXt().setCoords(XT.getCoords()); 
        this.perspective2dMonoProjector.getYt().setCoords(YT.getCoords()); 
        this.perspective2dMonoProjector.getZt().setCoords(ZT.getCoords()); 
        this.perspective2dMonoProjector.setFov(fov);
    }

    @Override
    public double getCurrentDistance() {
        double[] cCoords = perspective2dMonoProjector.getC().getCoords();
        double x = cCoords[0];
        double y = cCoords[1];
        double z = cCoords[2];
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public double getCurrentAzimuth() {
        double[] cCoords = perspective2dMonoProjector.getC().getCoords();
        return Math.atan2(cCoords[1], cCoords[0]);
    }

    @Override
    public double getCurrentAltitude() {
        double[] cCoords = perspective2dMonoProjector.getC().getCoords();
        double x = cCoords[0];
        double y = cCoords[1];
        double z = cCoords[2];
        return Math.atan2(z, Math.sqrt(x*x + y*y));
    }
    
    @Override
    public void setPosition(double distance, double azimuth, double altitude) {
        setProjectorsOnX(distance, getFov());
        
        rotateProjector(RotationPlane4DEnum.XZ, checkAltitudeLimit(altitude, 0));
        rotateProjector(RotationPlane4DEnum.XY, azimuth);
        
        perspective2dMonoProjector.precalculateTransMatrix();
    }
    
    @Override
    public double getFov() {
        return perspective2dMonoProjector.getFov();
    }
    
    @Override
    public void setFov(double fov) {
        perspective2dMonoProjector.setFov(fov);
    }
    
    @Override
    public void setDistance(double distance) {
        double currDistance = getCurrentDistance();
        double koef = distance / currDistance;
        
        double[] cCoords = perspective2dMonoProjector.getC().getCoords();
        cCoords[0] *= koef;
        cCoords[1] *= koef;
        cCoords[2] *= koef;
        
        perspective2dMonoProjector.precalculateTransMatrix();
    }
    
    @Override
    public void changeFov(double delta) {
        perspective2dMonoProjector.setFov(perspective2dMonoProjector.getFov() + delta);
    }
    
    @Override
    public void rotateCamera(double deltaAzimuth, double deltaAltitude) {
        double currAzimuth = getCurrentAzimuth();
        double currAltitude = getCurrentAltitude();
        
        rotateProjector(RotationPlane4DEnum.XY, -currAzimuth);
        rotateProjector(RotationPlane4DEnum.XZ, checkAltitudeLimit(deltaAltitude, currAltitude));
        rotateProjector(RotationPlane4DEnum.XY, currAzimuth + deltaAzimuth);
        
        perspective2dMonoProjector.precalculateTransMatrix();
    }

    private void rotateProjector(RotationPlane4DEnum rotationPlane, double radians) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);
        perspective2dMonoProjector.getC().rotate(rotationMatrix, CENTRUM);
        perspective2dMonoProjector.getXt().rotate(rotationMatrix, CENTRUM);
        perspective2dMonoProjector.getYt().rotate(rotationMatrix, CENTRUM);
        perspective2dMonoProjector.getZt().rotate(rotationMatrix, CENTRUM);
    }

    @Override
    public void doProjection(Figure figure) {
        projectedFigure = perspective2dMonoProjector.project(figure);
    }
    
    @Override
    public void paintOn(JPanel panel, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        int width = panel.getWidth();
        int height = panel.getHeight();
        
        int cx = width / 2;
        int cy = height / 2;
        double ratio = width > height ? height : width;
        
        paintFigure(projectedFigure, cx, cy, ratio, g2d);
    }

    @Override
    protected Color getColor(Edge edge) {
        return PAINT_COLOR;
    }


    @Override
    protected Color getColorSelected(Edge edge) {
        return SELECTED_BW_COLOR;
    }
}
