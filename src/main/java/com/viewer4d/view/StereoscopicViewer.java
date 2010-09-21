package com.viewer4d.view;

import static com.viewer4d.view.ViewContainer.CAMERA_DISTANCE_DEFAULT;
import static com.viewer4d.view.ViewContainer.CAMERA_EYES_DIST_DEFAULT;
import static com.viewer4d.view.ViewContainer.CAMERA_FOV_DEFAULT;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.projectors.from3dto2d.Perspective2DMonoProjector;

public class StereoscopicViewer extends AbstractViewer {
    
    static final double XY_CLIPPING_LIMIT = 0.5;
    
    private Perspective2DMonoProjector<MovablePoint> perspective2dMonoProjectorRight;
    private Perspective2DMonoProjector<MovablePoint> perspective2dMonoProjectorLeft;
    
    private double eyesDist;
    private double currentDistance;
    private double currentAzimuth;
    private double currentAltitude;
    
    private boolean distanceChanged;
    private boolean azimuthChanged;
    private boolean altitudeChanged;

    private Figure projectedFigureRight;
    private Figure projectedFigureLeft;
    
    public StereoscopicViewer() {
        initProjectors(CAMERA_DISTANCE_DEFAULT, CAMERA_EYES_DIST_DEFAULT, CAMERA_FOV_DEFAULT);
    }

    private void initProjectors(double distance, double eyeDist, double fov) {
        this.perspective2dMonoProjectorRight = new Perspective2DMonoProjector<MovablePoint>(
                new MovablePoint(distance, 0, 0), 
                new MovablePoint(XT), 
                new MovablePoint(YT), 
                new MovablePoint(ZT), 
                fov, XY_CLIPPING_LIMIT);
        this.perspective2dMonoProjectorLeft = new Perspective2DMonoProjector<MovablePoint>(
                new MovablePoint(distance, 0, 0), 
                new MovablePoint(XT), 
                new MovablePoint(YT), 
                new MovablePoint(ZT), 
                fov, XY_CLIPPING_LIMIT);
        
        this.eyesDist = eyeDist;
        
        double eyeAgle = Math.atan((eyeDist / 2) / distance);
        
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XY, eyeAgle);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XY, -eyeAgle);
        
        distanceChanged = true;
        azimuthChanged = true;
        altitudeChanged = true;
    }

    private void setProjectorsOnX(double distance, double fov) {
        this.perspective2dMonoProjectorRight.getC().setCoords(new double[] {distance, 0, 0}); 
        this.perspective2dMonoProjectorRight.getXt().setCoords(XT.getCoords()); 
        this.perspective2dMonoProjectorRight.getYt().setCoords(YT.getCoords()); 
        this.perspective2dMonoProjectorRight.getZt().setCoords(ZT.getCoords()); 
        this.perspective2dMonoProjectorRight.setFov(fov);

        this.perspective2dMonoProjectorLeft.getC().setCoords(new double[] {distance, 0, 0}); 
        this.perspective2dMonoProjectorLeft.getXt().setCoords(XT.getCoords()); 
        this.perspective2dMonoProjectorLeft.getYt().setCoords(YT.getCoords()); 
        this.perspective2dMonoProjectorLeft.getZt().setCoords(ZT.getCoords()); 
        this.perspective2dMonoProjectorLeft.setFov(fov);
        
        double eyeAgle = Math.atan((eyesDist / 2) / distance);
        
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XY, eyeAgle);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XY, -eyeAgle);
        
        distanceChanged = true;
        azimuthChanged = true;
        altitudeChanged = true;
    }

    @Override
    public double getCurrentDistance() {
        if (distanceChanged) {
            double[] cCoords = perspective2dMonoProjectorRight.getC().getCoords();
            double x = cCoords[0];
            double y = cCoords[1];
            double z = cCoords[2];
            currentDistance = Math.sqrt(x*x + y*y + z*z);
            distanceChanged = false;
        }
        return currentDistance;
    }

    @Override
    public double getCurrentAzimuth() {
        if (azimuthChanged) {
            double[] crCoords = perspective2dMonoProjectorRight.getC().getCoords();
            double[] clCoords = perspective2dMonoProjectorLeft.getC().getCoords();
            currentAzimuth = (Math.atan2(crCoords[1], crCoords[0]) + Math.atan2(clCoords[1], clCoords[0])) / 2;
            azimuthChanged = false;
        }
        return currentAzimuth;
    }

    @Override
    public double getCurrentAltitude() {
        if (altitudeChanged) {
            double[] cCoords = perspective2dMonoProjectorRight.getC().getCoords();
            double x = cCoords[0];
            double y = cCoords[1];
            double z = cCoords[2];
            currentAltitude = Math.atan2(z, Math.sqrt(x*x + y*y));
            altitudeChanged = false;
        }
        return currentAltitude;
    }
    
    @Override
    public void setPosition(double distance, double azimuth, double altitude) {
        setProjectorsOnX(distance, getFov());
        
        double altitudeLimited = checkAltitudeLimit(altitude, 0);
        
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XZ, altitudeLimited);
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XY, azimuth);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XZ, altitudeLimited);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XY, azimuth);
        
        perspective2dMonoProjectorRight.precalculateTransMatrix();
        perspective2dMonoProjectorLeft.precalculateTransMatrix();
        
        distanceChanged = true;
        azimuthChanged = true;
        altitudeChanged = true;
    }

    @Override
    public double getFov() {
        return perspective2dMonoProjectorRight.getFov();
    }
    
    @Override
    public void setFov(double fov) {
        perspective2dMonoProjectorRight.setFov(fov);
        perspective2dMonoProjectorLeft.setFov(fov);
    }
    
    @Override
    public void doProjection(Figure figure) {
        projectedFigureRight = perspective2dMonoProjectorRight.project(figure);
        projectedFigureLeft = perspective2dMonoProjectorLeft.project(figure);
    }
    
    @Override
    public void setDistance(double distance) {
        setPosition(distance, getCurrentAzimuth(), getCurrentAltitude());
    }

    public void changeEyesDistance(double delta) {
        if (eyesDist + delta >= 0) {
            eyesDist += delta;
        }
        setPosition((getCurrentDistance()), getCurrentAzimuth(), getCurrentAltitude());
    }

    @Override
    public void changeFov(double delta) {
        setFov(getFov() + delta);
    }
    
    @Override
    public void rotateCamera(double deltaAzimuth, double deltaAltitude) {
        double currAzimuth = getCurrentAzimuth();
        double currAltitude = getCurrentAltitude();
        
        double altitudeLimited = checkAltitudeLimit(deltaAltitude, currAltitude);
        
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XY, -currAzimuth);
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XZ, altitudeLimited);
        rotateProjector(perspective2dMonoProjectorRight, RotationPlane4DEnum.XY, currAzimuth + deltaAzimuth);
        
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XY, -currAzimuth);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XZ, altitudeLimited);
        rotateProjector(perspective2dMonoProjectorLeft, RotationPlane4DEnum.XY, currAzimuth + deltaAzimuth);
        
        perspective2dMonoProjectorRight.precalculateTransMatrix();
        perspective2dMonoProjectorLeft.precalculateTransMatrix();
        
        azimuthChanged = true;
        altitudeChanged = true;
    }
    
    private void rotateProjector(Perspective2DMonoProjector<MovablePoint> projector, 
            RotationPlane4DEnum rotationPlane, double radians) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);
        projector.getC().rotate(rotationMatrix, CENTRUM);
        projector.getXt().rotate(rotationMatrix, CENTRUM);
        projector.getYt().rotate(rotationMatrix, CENTRUM);
        projector.getZt().rotate(rotationMatrix, CENTRUM);
    }

    @Override
    public void paintOn(JPanel panel, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        int width = panel.getWidth();
        int height = panel.getHeight();
        
        int cxl = width / 4;
        int cxr = width * 3 / 4;
        int cy = height / 2;
        double ratio = width / 2;
        
        paintFigure(projectedFigureRight, cxl, cy, ratio, g2d);
        paintFigure(projectedFigureLeft, cxr, cy, ratio, g2d);
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
