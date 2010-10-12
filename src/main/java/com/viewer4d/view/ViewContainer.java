package com.viewer4d.view;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.figure.Figure;
import com.viewer4d.geometry.figure.FigureMovable;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;
import com.viewer4d.gui.ControlPanelSelectors;
import com.viewer4d.gui.Viewer4DFrame;
import static com.viewer4d.gui.Viewer4DFrame.*;
import com.viewer4d.projector.AbstractEnablingProjector;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Projector;
import com.viewer4d.projector.auxiliary.CubeXYZOrtsProjector;
import com.viewer4d.projector.auxiliary.XYZWOrtsProjector;
import com.viewer4d.projector.combining.AuxProjectorsMerger;
import com.viewer4d.projector.combining.CombiningProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveMovableProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveProjectorOnXYZ;
import com.viewer4d.projector.intersector.Movable3DSpaceIntersector;
import com.viewer4d.projector.intersector.Simple3DSpaceIntersectorWPoint;
import com.viewer4d.projector.selector.AbstractEnablingSelector;
import com.viewer4d.projector.selector.EntireFigureSelector;
import com.viewer4d.projector.selector.SelectedCellsSelector;
import com.viewer4d.projector.selector.SelectorCuttingHalfOfOrt;

public class ViewContainer {

    public static final UNIT_VECTORS CUTTING_FIGURE_ORT_DEFAULT = UNIT_VECTORS.W;
    public static final boolean CUTTING_FIGURE_NEG_DEFAULT = true;
    
    public static final int W_4D_POSITION_DEFAULT = -10;
    
    public static final double CAMERA_DISTANCE_DEFAULT = 18;
    public static final double CAMERA_EYES_DIST_DEFAULT = 1.0;
    public static final double CAMERA_FOV_DEFAULT = Math.tan(Math.PI/12);
    public static final double CAMERA_AZIMUTH_DEFAULT = -Math.PI/5;
    public static final double CAMERA_ALTITUDE_DEFAULT = Math.PI/8;
    
    public static final double CAMERA_DISTANCE_LIMIT = 4;

    public static final double ONE_ROTATE_STEP = Math.PI/400;
    public static final double ONE_MOVE_STEP = 0.02;

    
    private FigureMovable figure;
    
    private Figure merged4dFigure;
    private Figure projection3d;
    private Figure altProjection3d;
    
    
    private CombiningProjector<AbstractEnablingSelector> selectorFirst;
    private EntireFigureSelector entireFigureSelector;
    private SelectedCellsSelector selectedCellsSelector;
    
    private AuxProjectorsMerger<Projector> intersectorsMerger;
    private Simple3DSpaceIntersectorWPoint spaceIntersector;
    private Movable3DSpaceIntersector movable3dSpaceIntersector;
    
    private AuxProjectorsMerger<Projector> halfOrtProjectorsMerger;
    private SelectorCuttingHalfOfOrt halfOrtCuttingSelector;
    
    private CombiningProjector<PerspectiveProjectorOnXYZ> main4DProjector;
    private PerspectiveProjectorOnXYZ projector4DOnXYZ;
    private PerspectiveMovableProjector projector4DOnOrts;
    private PerspectiveMovableProjector moving4DProjector;
    
    private PerspectiveMovableProjector altProjector4DOnOrts;
    
    private AuxProjectorsMerger<Projector> mainAuxProjectorsMerger;
    private AbstractEnablingProjector ortsProjector;
    private AbstractEnablingProjector cubeOrtsProjector;
    
    
    private Viewer viewer;

    
    private FigureCellSelector cellSelector;
    
    
    private boolean needRepaint;
    
    private boolean ortsAreEnabled;
    private boolean cubeOrtsAreEnabled;
    private boolean xyzAreEnable;


    public ViewContainer(FigureMovable figure) {
        this.figure = figure;
        
        // Selectors
        entireFigureSelector = new EntireFigureSelector(!CUTTING_NON_SELECTED_DEFAULT);
        selectedCellsSelector = new SelectedCellsSelector(CUTTING_NON_SELECTED_DEFAULT);
        
        selectorFirst = new CombiningProjector<AbstractEnablingSelector>(
                entireFigureSelector, selectedCellsSelector);
        
        // 3D space intersectors
        spaceIntersector = new Simple3DSpaceIntersectorWPoint(true);
        movable3dSpaceIntersector = new Movable3DSpaceIntersector(false);
        
        // The intersectors merger
        intersectorsMerger = new AuxProjectorsMerger<Projector>(
                spaceIntersector,
                movable3dSpaceIntersector
        );
        
        halfOrtCuttingSelector = new SelectorCuttingHalfOfOrt(CUTTING_FIGURE_PROJECTION_DEFAULT,
                CUTTING_FIGURE_ORT_DEFAULT, CUTTING_FIGURE_NEG_DEFAULT);
        
        // The half ort cutting selector merger
        halfOrtProjectorsMerger = new AuxProjectorsMerger<Projector>(
                halfOrtCuttingSelector
        );
        halfOrtProjectorsMerger.enable(!CUTTING_FIGURE_PROJECTION_DEFAULT);
        
        // 4D Projectors
        projector4DOnXYZ = new PerspectiveProjectorOnXYZ(W_4D_POSITION_DEFAULT, true);
        projector4DOnOrts = new PerspectiveMovableProjector(W_4D_POSITION_DEFAULT, true);
        moving4DProjector = new PerspectiveMovableProjector(W_4D_POSITION_DEFAULT, true);
        
        main4DProjector = new CombiningProjector<PerspectiveProjectorOnXYZ>(
                projector4DOnXYZ,
                projector4DOnOrts,
                moving4DProjector
        );
        
        altProjector4DOnOrts = new PerspectiveMovableProjector(W_4D_POSITION_DEFAULT, true);
        altProjector4DOnOrts.enable(true);
        altProjector4DOnOrts.setProjector(UNIT_VECTORS.X, true);
        
        // Orts
        ortsProjector = new XYZWOrtsProjector(true);
        cubeOrtsProjector = new CubeXYZOrtsProjector(false);
        
        // The auxiliary projectors merger
        mainAuxProjectorsMerger = new AuxProjectorsMerger<Projector>(
                ortsProjector,
                cubeOrtsProjector
        );
        
        // Variables
        ortsAreEnabled = ortsProjector.isEnabled();
        cubeOrtsAreEnabled = cubeOrtsProjector.isEnabled();
        xyzAreEnable = ortsAreEnabled || cubeOrtsAreEnabled;
        
        // Figure cell selector
        cellSelector = new FigureCellSelector(figure, false);
        
        setViewer(new MonoscopicViewer(Viewer4DFrame.VIEWER_COLORED_DEFAULT));
        enable4DProjector(0);
    }

    public FigureMovable getFigure() {
        return figure;
    }
    
    public void setFigure(FigureMovable figure) {
        figure.reset();
        
        main4DProjector.reset();
        movable3dSpaceIntersector.reset();
        
        cellSelector.setFigure(figure);
        this.figure = figure;
        
        doFullProjection();
    }
    
    // Viewer manipulating methods
    public void setStereoscopicViewer() {
        setViewer(new StereoscopicViewer());
        doCameraProjection();
    }
    
    public void setMonoscopicViewer() {
        setViewer(new MonoscopicViewer());
        doCameraProjection();
    }
    
    public void setTwoChannelViewer() {
        setViewer(new TwoChannelMonoViewer());
        doAlternative4DProjection(merged4dFigure);
        doCameraProjection();
    }
    
    public void setViewerColored(boolean colored) {
        viewer.setColored(colored);
    }
    
    protected void setViewer(Viewer viewer) {
        double currentDistance;
        double currentAzimuth;
        double currentAltitude;
        double currentFov;
        boolean colored;
        
        Viewer currentViewer = this.viewer;
        if (currentViewer != null) {
            currentDistance = currentViewer.getCurrentDistance();
            currentAzimuth = currentViewer.getCurrentAzimuth();
            currentAltitude = currentViewer.getCurrentAltitude();
            currentFov = currentViewer.getFov();
            colored = currentViewer.isColored();
        } else {
            currentDistance = CAMERA_DISTANCE_DEFAULT;
            currentAzimuth = CAMERA_AZIMUTH_DEFAULT;
            currentAltitude = CAMERA_ALTITUDE_DEFAULT;
            currentFov = CAMERA_FOV_DEFAULT;
            colored = Viewer4DFrame.VIEWER_COLORED_DEFAULT;
        }
        
        viewer.setPosition(currentDistance, currentAzimuth, currentAltitude);
        viewer.setFov(currentFov);
        viewer.setColored(colored);
        
        this.viewer = viewer;
    }
    
    // 4D projectors manipulating methods
    public AbstractProjectingProjector get4DProjector(int projectorNumber) {
        return main4DProjector.getProjectors().get(projectorNumber);
    }
    
    public void setMovableProjector(UNIT_VECTORS vector, boolean forward) {
        // TODO Add perspectiveMovingProjector here?
        if (projector4DOnOrts.isEnabled()) {
            projector4DOnOrts.setProjector(vector, forward);
            doFullProjection();
        }
    }
    
    public void setAltMovableProjector(UNIT_VECTORS vector, boolean forward) {
        if (isAlternativeProjectionActive()) {
            altProjector4DOnOrts.setProjector(vector, forward);
            doFullProjection();
        }
    }

    public void toggle4DProjector(int projectorNumber) {
        enable4DProjector(projectorNumber);
        doFullProjection();
    }

    public void set4DProjectorsPerspective(boolean perspective) {
        List<PerspectiveProjectorOnXYZ> projectors = main4DProjector.getProjectors();
        for (PerspectiveProjectorOnXYZ projector : projectors) {
            projector.setPerspective(perspective);
        }
        if (isAlternativeProjectionActive()) {
            altProjector4DOnOrts.setPerspective(perspective);
        }
        doFullProjection();
    }
    
    public void set4DProjectorsColorRelativeTo(boolean relativeToWOrt) {
        List<PerspectiveProjectorOnXYZ> projectors = main4DProjector.getProjectors();
        for (PerspectiveProjectorOnXYZ projector : projectors) {
            projector.setColorRelToWOrt(relativeToWOrt);
        }
        if (isAlternativeProjectionActive()) {
            altProjector4DOnOrts.setColorRelToWOrt(relativeToWOrt);
        }
        doFullProjection();
    }
    
    protected void enable4DProjector(int projectorNumber) {
        List<PerspectiveProjectorOnXYZ> projectors = main4DProjector.getProjectors();
        for (int i = 0; i < projectors.size(); i++) {
            projectors.get(i).enable(i == projectorNumber);
        }
    }

    // Figure manipulation methods
    public void moveFigure(UNIT_VECTORS direction, double amount) {
        Vector vector = Vector.createFrom(direction, amount);
        
        figure.move(vector);
        moving4DProjector.move(vector);
        movable3dSpaceIntersector.move(vector);
        
        doFullProjection();
    }

    public void rotateFigure(RotationPlane4DEnum rotationPlane, double radians) {
        figure.rotate(rotationPlane, radians);
        moving4DProjector.rotate(rotationPlane, radians);
        movable3dSpaceIntersector.rotate(rotationPlane, radians, figure.getCentrum());
        
        doFullProjection();
    }

    public void rotateFigureDouble(RotationPlane4DEnum rotationPlane1, double radians1, 
            RotationPlane4DEnum rotationPlane2, double radians2) {
        figure.rotate(rotationPlane1, radians1);
        moving4DProjector.rotate(rotationPlane1, radians1);
        movable3dSpaceIntersector.rotate(rotationPlane1, radians1, figure.getCentrum());
        figure.rotate(rotationPlane2, radians2);
        moving4DProjector.rotate(rotationPlane2, radians2);
        movable3dSpaceIntersector.rotate(rotationPlane2, radians2, figure.getCentrum());
        
        doFullProjection();
    }

    public void rotateFigureOneStep(RotationPlane4DEnum rotationPlane, boolean forward) {
        double radians = forward ? ONE_ROTATE_STEP : -ONE_ROTATE_STEP;
        rotateFigure(rotationPlane, radians);
    }
    
    public void moveFigureOneStep(UNIT_VECTORS vector, boolean forward) {
        double radians = forward ? ONE_MOVE_STEP : -ONE_MOVE_STEP;
        moveFigure(vector, radians);
    }

    public void reset() {
        figure.reset();
        resetSelectedCell();
        
        main4DProjector.reset();
        movable3dSpaceIntersector.reset();
        
        halfOrtCuttingSelector.setUnitVector(CUTTING_FIGURE_ORT_DEFAULT);
        halfOrtCuttingSelector.setNegative(CUTTING_FIGURE_NEG_DEFAULT);
        
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
    
    public void selectNextSiblingCell() {
        cellSelector.selectNextSiblingCell();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }

    public void selectPrevSiblingCell() {
        cellSelector.selectPrevSiblingCell();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }
    
    public void toggleSiblingCells() {
        cellSelector.toggleSiblingCells();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }

    public void lockUnlockSelectedCell() {
        cellSelector.lockUnlockSelectedCell();
        if (cellSelector.isRepaintNeeded()) {
            doFullProjection();
        }
    }
    
    public void clearLockedSelectedCells() {
        cellSelector.clearLockedSelectedCells();
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
    
    public void rotateCamera(double azimuthDelta, double altitudeDelta) {
        viewer.rotateCamera(azimuthDelta, altitudeDelta);
        doCameraProjection();
    }
    
    // Projection manipulation methods
    public void change4dProjection(int delta) {
        main4DProjector.change(delta);
        doFullProjection();
    }
    
    // Auxiliary projection methods
    public void toggle4dFigureProjection() {
        intersectorsMerger.enable(!intersectorsMerger.isEnabled());
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
        spaceIntersector.enable(!spaceIntersector.isEnabled());
        doFullProjection();
    }
    
    public void toggle3dIntersector(boolean on) {
        spaceIntersector.enable(on);
        doFullProjection();
    }
    
    public void toggleMovable3DIntersector() {
        movable3dSpaceIntersector.enable(!movable3dSpaceIntersector.isEnabled());
        doFullProjection();
    }
    
    protected void moveMovable3DIntersector(UNIT_VECTORS direction, double amount) {
        if (movable3dSpaceIntersector.isEnabled()) {
            Vector vector = Vector.createFrom(direction, amount);
            movable3dSpaceIntersector.move(vector);
            doFullProjection();
        }
    }

    protected void rotateMovable3DIntersector(RotationPlane4DEnum rotationPlane, double radians) {
        if (movable3dSpaceIntersector.isEnabled()) {
            movable3dSpaceIntersector.rotate(rotationPlane, radians, figure.getCentrum());
            doFullProjection();
        }
    }

    public void rotateMovable3DIntersectorOneStep(RotationPlane4DEnum rotationPlane, boolean forward) {
        double radians = forward ? ONE_ROTATE_STEP : -ONE_ROTATE_STEP;
        rotateMovable3DIntersector(rotationPlane, radians);
    }
    
    public void moveMovable3DIntersectorOneStep(UNIT_VECTORS vector, boolean forward) {
        double radians = forward ? ONE_MOVE_STEP : -ONE_MOVE_STEP;
        moveMovable3DIntersector(vector, radians);
    }

    // Selectors handling methods
    public void toggleSelector(int selectorNumber) {
        enableSelector(selectorNumber);
        doFullProjection();
    }

    public void toggleCuttingNWSelector() {
        enableSelector(ControlPanelSelectors.CUTTING_NEGATIVE_W_SELECTOR_NUMBER);
        doFullProjection();
    }
    
    public void toggleCellSelector() {
        enableSelector(ControlPanelSelectors.CUTTING_NONSELECTED_CELL_SELECTOR_NUMBER);
        doFullProjection();
    }
    
    public void toggleEntireFigureSelector() {
        enableSelector(ControlPanelSelectors.ENTIRE_FIGURE_SELECTOR_NUMBER);
        doFullProjection();
    }

    protected void enableSelector(int selectorNumber) {
        entireFigureSelector.enable(false);
        selectedCellsSelector.enable(false);
        halfOrtCuttingSelector.enable(false);
        halfOrtProjectorsMerger.enable(true);
        
        switch (selectorNumber) {
        case ControlPanelSelectors.ENTIRE_FIGURE_SELECTOR_NUMBER:
            entireFigureSelector.enable(true);
            break;
        case ControlPanelSelectors.CUTTING_NEGATIVE_W_SELECTOR_NUMBER:
            entireFigureSelector.enable(true);
            halfOrtCuttingSelector.enable(true);
            halfOrtProjectorsMerger.enable(false);
            break;
        case ControlPanelSelectors.CUTTING_NONSELECTED_CELL_SELECTOR_NUMBER:
            selectedCellsSelector.enable(true);
            break;
        }
    }

    public void setCuttingNWSelector(UNIT_VECTORS unitVector, boolean negative) {
        if (halfOrtCuttingSelector.isEnabled()) {
            halfOrtCuttingSelector.setUnitVector(unitVector);
            halfOrtCuttingSelector.setNegative(negative);
            doFullProjection();
        }
    }
    
    public void toggleNotCuttingSelected() {
        if (halfOrtCuttingSelector.isEnabled()) {
            halfOrtCuttingSelector.setNotCuttingSelected(
                    !halfOrtCuttingSelector.isNotCuttingSelected());
            doFullProjection();
        }
    }
    
    // Projection method
    public synchronized void doFullProjection() {
        merged4dFigure = mainAuxProjectorsMerger.project(
                halfOrtProjectorsMerger.project(
                        intersectorsMerger.project(
                                selectorFirst.project(
                                        getFigure()))));
        
        doMain4DProjection(merged4dFigure);
        
        if (isAlternativeProjectionActive()) {
            doAlternative4DProjection(merged4dFigure);
        }
        
        doCameraProjection();
        needRepaint = true;
    }
    
    protected void doMain4DProjection(Figure figure) {
        projection3d = main4DProjector.project(figure);
    }

    protected void doAlternative4DProjection(Figure figure) {
        altProjection3d = altProjector4DOnOrts.project(figure);
    }
    
    protected boolean isAlternativeProjectionActive() {
        return viewer instanceof TwoChannelViewer;
    }

    public synchronized void doCameraProjection() {
        if (isAlternativeProjectionActive()) {
            ((TwoChannelViewer) viewer).doProjection(projection3d, altProjection3d);
        } else {
            viewer.doProjection(projection3d);
        }
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
    
}