package com.viewer4d.view;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.FigureMovable;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;
import com.viewer4d.projector.AbstractEnablingProjector;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.auxiliary.CubeXYZOrtsProjector;
import com.viewer4d.projector.auxiliary.XYZWOrtsProjector;
import com.viewer4d.projector.combining.CombinedAuxAndMainProjectors;
import com.viewer4d.projector.combining.CombiningProjector;
import com.viewer4d.projector.from4dto3d.FrontIsometricProjectorOnXYZAlongW;
import com.viewer4d.projector.from4dto3d.ParallelProjectorOnXYZAlongW;
import com.viewer4d.projector.from4dto3d.PerspectiveMovableProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveMovingProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveProjectorOnXYZAlongW;
import com.viewer4d.projector.from4dto3d.PerspectiveProjectorOnXYZCuttingW;
import com.viewer4d.projector.selector.Simple3DSpaceIntersectorAtZeroW;

public class ViewContainer {

    public static final int W_4D_DISTANCE_DEFAULT = -10;
    
    public static final double CAMERA_DISTANCE_DEFAULT = 16;
    public static final double CAMERA_EYES_DIST_DEFAULT = 1.0;
    public static final double CAMERA_FOV_DEFAULT = Math.tan(Math.PI/12);
    public static final double CAMERA_AZIMUTH_DEFAULT = -Math.PI/6;
    public static final double CAMERA_ALTITUDE_DEFAULT = Math.PI/8;
    
    public static final double CAMERA_DISTANCE_LIMIT = 4;

    private static final double ONE_ROTATE_STEP = Math.PI/400;
    private static final double ONE_MOVE_STEP = 0.02;
    
    
    private FigureMovable figure;
    private Figure projection3d;
    
    private CombinedAuxAndMainProjectors projectorMain;
    private CombiningProjector<AbstractProjectingProjector> combining4DProjector;
    private AbstractEnablingProjector ortsProjector;
    private AbstractEnablingProjector cubeOrtsProjector;
    private Simple3DSpaceIntersectorAtZeroW spaceIntersector;
    
    private Viewer viewer;

    private FigureCellSelector cellSelector;
    
    private boolean needRepaint;
    
    private boolean ortsAreEnabled;
    private boolean cubeOrtsAreEnabled;
    private boolean xyzAreEnable;


    public ViewContainer(FigureMovable figure) {
        this.figure = figure;
        
        combining4DProjector = new CombiningProjector<AbstractProjectingProjector>(
                new PerspectiveProjectorOnXYZAlongW(W_4D_DISTANCE_DEFAULT),
                new ParallelProjectorOnXYZAlongW(),
                new FrontIsometricProjectorOnXYZAlongW(),
                new PerspectiveMovingProjector(W_4D_DISTANCE_DEFAULT),
                new PerspectiveMovableProjector(W_4D_DISTANCE_DEFAULT),
                new PerspectiveProjectorOnXYZCuttingW(W_4D_DISTANCE_DEFAULT)
        );
        
        ortsProjector = new XYZWOrtsProjector(true);
        cubeOrtsProjector = new CubeXYZOrtsProjector(false);
        spaceIntersector = new Simple3DSpaceIntersectorAtZeroW();
        
        projectorMain = new CombinedAuxAndMainProjectors(
                combining4DProjector,
                spaceIntersector,
                ortsProjector,
                cubeOrtsProjector
        );
        
        ortsAreEnabled = ortsProjector.isEnabled();
        cubeOrtsAreEnabled = cubeOrtsProjector.isEnabled();
        xyzAreEnable = ortsAreEnabled || cubeOrtsAreEnabled;
        
        cellSelector = new FigureCellSelector(figure, false);
        
        setViewer(new ColoredMonoscopicViewer());
        enable4DProjector(0);
    }

    public FigureMovable getFigure() {
        return figure;
    }
    
    public void setFigure(FigureMovable figure) {
        boolean selectModeOn = cellSelector.isSelectModeOn();
        
        figure.reset();
        combining4DProjector.reset();
        cellSelector.setSelectMode(false);
        
        this.figure = figure;
        cellSelector = new FigureCellSelector(figure, selectModeOn);
        
        doFullProjection();
    }
    
    // Viewer manipulating methods
    public void setStereoscopicViewer(boolean colored) {
        if (colored) {
            setViewer(new ColoredStereoscopicViewer());
        } else {
            setViewer(new StereoscopicViewer());
        }
        doCameraProjection();
    }
    
    public void setMonoscopicViewer(boolean colored) {
        if (colored) {
            setViewer(new ColoredMonoscopicViewer());
        } else {
            setViewer(new MonoscopicViewer());
        }
        doCameraProjection();
    }
    
    protected void setViewer(Viewer viewer) {
        double currentDistance;
        double currentAzimuth;
        double currentAltitude;
        double currentFov;
        
        Viewer currentViewer = this.viewer;
        if (currentViewer != null) {
            currentDistance = currentViewer.getCurrentDistance();
            currentAzimuth = currentViewer.getCurrentAzimuth();
            currentAltitude = currentViewer.getCurrentAltitude();
            currentFov = currentViewer.getFov();
        } else {
            currentDistance = CAMERA_DISTANCE_DEFAULT;
            currentAzimuth = CAMERA_AZIMUTH_DEFAULT;
            currentAltitude = CAMERA_ALTITUDE_DEFAULT;
            currentFov = CAMERA_FOV_DEFAULT;
        }
        
        viewer.setPosition(currentDistance, currentAzimuth, currentAltitude);
        viewer.setFov(currentFov);
        
        this.viewer = viewer;
    }
    
    // 4D projectors manipulating methods
    public AbstractProjectingProjector get4DProjector(int projectorNumber) {
        return combining4DProjector.getProjectors().get(projectorNumber);
    }
    
    public void setMovableProjector(UNIT_VECTORS vector, boolean forward) {
        ((PerspectiveMovableProjector) get4DProjector(4)).setProjector(vector, forward);
        doFullProjection();
    }
    
    public void toggle4DProjector(int projectorNumber) {
        enable4DProjector(projectorNumber);
        doFullProjection();
    }

    protected void enable4DProjector(int projectorNumber) {
        List<AbstractProjectingProjector> projectors = combining4DProjector.getProjectors();
        for (int i = 0; i < projectors.size(); i++) {
            projectors.get(i).enable(i == projectorNumber);
        }
    }

    // Figure manipulation methods
    public void moveFigure(UNIT_VECTORS direction, double amount) {
        Vector vector = getVectorFrom(direction, amount);
        figure.move(vector);
        combining4DProjector.move(vector);
        doFullProjection();
    }

    public void rotateFigure(RotationPlane4DEnum rotationPlane, double amount) {
        figure.rotate(rotationPlane, amount);
        combining4DProjector.rotate(rotationPlane, amount, figure.getCentrum());
        doFullProjection();
    }

    public void rotateFigureOneStep(RotationPlane4DEnum rotationPlane, boolean forward) {
        double amount = forward ? ONE_ROTATE_STEP : -ONE_ROTATE_STEP;
        rotateFigure(rotationPlane, amount);
    }
    
    public void moveFigureOneStep(UNIT_VECTORS vector, boolean forward) {
        double amount = forward ? ONE_MOVE_STEP : -ONE_MOVE_STEP;
        moveFigure(vector, amount);
    }

    public void resetFigure() {
        figure.reset();
        resetSelectedCell();
        
        combining4DProjector.reset();
        
        doFullProjection();
    }
    
    // Selection of cells methods
    public boolean isSelectModeOn() {
        return cellSelector.isSelectModeOn();
    }
    
    public void toggleSelectMode() {
        cellSelector.setSelectMode(!cellSelector.isSelectModeOn());
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }

    public void selectNextCell() {
        cellSelector.selectNextCell();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }

    public void selectPrevCell() {
        cellSelector.selectPrevCell();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }
    
    private void resetSelectedCell() {
        cellSelector.resetSelectedCell();
    }
    
    // Camera manipulation methods
    public void changeCameraDistance(double delta) {
        double newDistance = viewer.getCurrentDistance() + delta;
        
        if (newDistance >= CAMERA_DISTANCE_LIMIT) {
            viewer.setDistance(newDistance);
            doCameraProjection();
        }
    }
    
    public void changeCameraEyesDistance(double delta) {
        if (viewer instanceof StereoscopicViewer) {
            StereoscopicViewer stereoscopicViewer = (StereoscopicViewer) viewer;
            stereoscopicViewer.changeEyesDistance(delta);
            doCameraProjection();
        }
    }

    public void changeCameraFov(double delta) {
        viewer.changeFov(delta);
        doCameraProjection();
    }
    
    public void rotateCamera(int xDelta, int yDelta) {
        double azimuthDelta = (double)xDelta / 100;
        double altitudeDelta = (double)yDelta / 100;
        viewer.rotateCamera(azimuthDelta, altitudeDelta);
        doCameraProjection();
    }
    
    // Projection manipulation methods
    public void change4dProjection(int delta) {
        combining4DProjector.change(delta);
        doFullProjection();
    }
    
    // Toggle methods
    public void toggle4dFigureProjection() {
        if (projectorMain.isEnabled()) {
            projectorMain.enable(false);
        } else {
            projectorMain.enable(true);
        }
        doFullProjection();
    }
    
    public void switchXYZOrts() {
        if (ortsAreEnabled && cubeOrtsAreEnabled) {
            cubeOrtsAreEnabled = false;
        } else if (ortsAreEnabled && !cubeOrtsAreEnabled) {
            ortsAreEnabled = false;
            cubeOrtsAreEnabled = true;
        } else if (!ortsAreEnabled && cubeOrtsAreEnabled) {
            ortsAreEnabled = true;
        } else {
            // both are switched off
        }
        toggleXYZProjectors();
        doFullProjection();
    }
    
    public void toggleXYZOrts() {
        xyzAreEnable = !xyzAreEnable;
        toggleXYZProjectors();
        doFullProjection();
    }
    
    private void toggleXYZProjectors() {
        ortsProjector.enable(xyzAreEnable && ortsAreEnabled);
        cubeOrtsProjector.enable(xyzAreEnable && cubeOrtsAreEnabled);
    }

    public void toggle3dIntersector() {
        if (spaceIntersector.isEnabled()) {
            spaceIntersector.enable(false);
        } else {
            spaceIntersector.enable(true);
        }
        doFullProjection();
    }
    
    public void toggle3dIntersector(boolean on) {
        spaceIntersector.enable(on);
        doFullProjection();
    }
    
    // Projection method
    public synchronized void doFullProjection() {
        projection3d = projectorMain.project(getFigure());
        doCameraProjection();
        needRepaint = true;
    }
    
    public synchronized void doCameraProjection() {
        viewer.doProjection(projection3d);
        needRepaint = true;
    }
    
    // Painting method
    public synchronized void paintOn(JPanel panel, Graphics g) {
        viewer.paintOn(panel, g);
        needRepaint = false;
    }
    
    public boolean needProjection() {
        return needRepaint;
    }
    
    // Auxiliary methods
    private static Vector getVectorFrom(UNIT_VECTORS direction, double amount) {
        if (direction == UNIT_VECTORS.X) {
            return new Vector(amount, 0, 0, 0);
        } else if (direction == UNIT_VECTORS.Y) {
            return new Vector(0, amount, 0, 0);
        } else if (direction == UNIT_VECTORS.Z) {
            return new Vector(0, 0, amount, 0);
        } else if (direction == UNIT_VECTORS.W) {
            return new Vector(0, 0, 0, amount);
        } else {
            throw new IllegalArgumentException("The direction (" + direction + ") is invalid.");
        }
    }

}