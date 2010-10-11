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
import com.viewer4d.projector.AbstractEnablingProjector;
import com.viewer4d.projector.AbstractProjectingProjector;
import com.viewer4d.projector.Projector;
import com.viewer4d.projector.auxiliary.CubeXYZOrtsProjector;
import com.viewer4d.projector.auxiliary.XYZWOrtsProjector;
import com.viewer4d.projector.combining.AuxProjectorsMerger;
import com.viewer4d.projector.combining.CombiningProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveMovableProjector;
import com.viewer4d.projector.from4dto3d.PerspectiveProjectorOnXYZ;
import com.viewer4d.projector.intersector.Simple3DSpaceIntersectorAtZeroW;
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
    
    
    private CombiningProjector<AbstractEnablingSelector> selectorMain;
    
    private EntireFigureSelector entireFigureSelector;
    private SelectorCuttingHalfOfOrt halfOrtCuttingSelector;
    private SelectedCellsSelector selectedCellsSelector;
    
    
    private CombiningProjector<PerspectiveProjectorOnXYZ> main4DProjector;
    
    private PerspectiveProjectorOnXYZ projector4DOnXYZ;
    private PerspectiveMovableProjector projector4DOnOrts;
    private PerspectiveMovableProjector moving4DProjector;
    
    private PerspectiveMovableProjector altProjector4DOnOrts;
    
    private AuxProjectorsMerger<Projector> mainAuxProjectorsMerger;
    
    private Simple3DSpaceIntersectorAtZeroW spaceIntersector;
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
        entireFigureSelector = new EntireFigureSelector();
        halfOrtCuttingSelector = new SelectorCuttingHalfOfOrt(
                CUTTING_FIGURE_ORT_DEFAULT, CUTTING_FIGURE_NEG_DEFAULT);
        selectedCellsSelector = new SelectedCellsSelector();
        
        entireFigureSelector.enable(!Viewer4DFrame.CUTTING_FIGURE_PROJECTION_DEFAULT);
        halfOrtCuttingSelector.enable(Viewer4DFrame.CUTTING_FIGURE_PROJECTION_DEFAULT);
        selectedCellsSelector.enable(Viewer4DFrame.CUTTING_NON_SELECTED_DEFAULT);
        
        selectorMain = new CombiningProjector<AbstractEnablingSelector>(
                entireFigureSelector, halfOrtCuttingSelector, selectedCellsSelector);
        
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
        
        // 3D space intersector
        spaceIntersector = new Simple3DSpaceIntersectorAtZeroW();
        // Orts
        ortsProjector = new XYZWOrtsProjector(true);
        cubeOrtsProjector = new CubeXYZOrtsProjector(false);
        
        // The auxiliary projectors merger
        mainAuxProjectorsMerger = new AuxProjectorsMerger<Projector>(
                spaceIntersector,
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
        Vector vector = getVectorFrom(direction, amount);
        figure.move(vector);
        moving4DProjector.move(vector);
        doFullProjection();
    }

    public void rotateFigure(RotationPlane4DEnum rotationPlane, double amount) {
        figure.rotate(rotationPlane, amount);
        moving4DProjector.rotate(rotationPlane, amount);
        doFullProjection();
    }

    public void rotateFigureDouble(RotationPlane4DEnum rotationPlane1, double amount1, 
            RotationPlane4DEnum rotationPlane2, double amount2) {
        figure.rotate(rotationPlane1, amount1);
        moving4DProjector.rotate(rotationPlane1, amount1);
        figure.rotate(rotationPlane2, amount2);
        moving4DProjector.rotate(rotationPlane2, amount2);
        
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

    public void reset() {
        figure.reset();
        resetSelectedCell();
        
        main4DProjector.reset();
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
    
    // Toggle methods
    public void toggle4dFigureProjection() {
        mainAuxProjectorsMerger.enable(!mainAuxProjectorsMerger.isEnabled());
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
        List<AbstractEnablingSelector> selectors = selectorMain.getProjectors();
        for (int i = 0; i < selectors.size(); i++) {
            selectors.get(i).enable(i == selectorNumber);
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
        Figure selected = selectorMain.project(getFigure());
        merged4dFigure = mainAuxProjectorsMerger.project(selected);
        
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