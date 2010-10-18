package com.viewer4d.gui;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.simple.Dimensional.UNIT_VECTORS;
import com.viewer4d.view.ViewContainer;

public class FrameEventAdapter implements 
KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowStateListener {

    private final ViewContainer viewContainer;
    private final Viewer4DFrame mainFrame;
    private final JPanel paintingArea;
    
    private Point prevDragPoint;
    
    public FrameEventAdapter(Viewer4DFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.viewContainer = mainFrame.getViewContainer();
        this.paintingArea = mainFrame.getPaintingArea();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String keyText = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
        boolean ctrlPressed = (e.getModifiersEx() & CTRL_DOWN_MASK) == CTRL_DOWN_MASK;
        boolean shiftPressed = (e.getModifiersEx() & SHIFT_DOWN_MASK) == SHIFT_DOWN_MASK;
        
        if (keyText.equals("f1")) {
            mainFrame.toggleHelp();
            
        } else if (keyText.equals("escape")) {
            mainFrame.handleEscape();
            
        } else if (ctrlPressed) {
            UNIT_VECTORS vector = null;
            RotationPlane4DEnum rotationPlane = null;
            
            if (keyText.equals("slash")) {
                viewContainer.toggleSiblingCells();
            } else if (keyText.equals("comma")) {
                viewContainer.selectPrevSiblingCell();
            } else if (keyText.equals("period")) {
                viewContainer.selectNextSiblingCell();
            } else if ((vector = getMotionVector(keyText)) != null) {
                mainFrame.stopFigureMovement();
                mainFrame.stopCameraMovement();
                viewContainer.moveMovable3DIntersectorOneStep(vector, !shiftPressed);
            } else if ((rotationPlane = getRotationPlane(keyText)) != null) {
                mainFrame.stopFigureMovement();
                mainFrame.stopCameraMovement();
                viewContainer.rotateMovable3DIntersectorOneStep(rotationPlane, !shiftPressed);
            }
            if (viewContainer.needProjection()) {
                paintingArea.repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        boolean shiftPressed = (e.getModifiersEx() & SHIFT_DOWN_MASK) == SHIFT_DOWN_MASK;
        String keyText = String.valueOf(e.getKeyChar()).toLowerCase();
        
        UNIT_VECTORS vector = null;
        RotationPlane4DEnum rotationPlane = null;
        
        if ((vector = getMotionVector(keyText)) != null) {
            mainFrame.stopFigureMovement();
            mainFrame.stopCameraMovement();
            viewContainer.moveFigureOneStep(vector, !shiftPressed);
        } else if ((rotationPlane = getRotationPlane(keyText)) != null) {
            mainFrame.stopFigureMovement();
            mainFrame.stopCameraMovement();
            viewContainer.rotateFigureOneStep(rotationPlane, !shiftPressed);
        } else {
            doControlAction(keyText, shiftPressed);
        }
        if (viewContainer.needProjection()) {
            paintingArea.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            RandomFigureMover figureMover = mainFrame.getFigureMover();
            if (figureMover != null) {
                figureMover.pauseMove();
                figureMover.pauseCameraMove();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            RandomFigureMover figureMover = mainFrame.getFigureMover();
            if (figureMover != null) {
                figureMover.resumeMove();
                figureMover.resumeCameraMove();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currPoint = e.getPoint();
        if (prevDragPoint != null) {
            double xDelta = -(currPoint.getX() - prevDragPoint.getX()) / 100;
            double yDelta = (currPoint.getY() - prevDragPoint.getY()) / 100;
            viewContainer.rotateCamera(xDelta, yDelta);
            if (viewContainer.needProjection()) {
                paintingArea.repaint();
            }
        }
        prevDragPoint = currPoint;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        prevDragPoint = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        int modifiersEx = e.getModifiersEx();
        
        if ((modifiersEx & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
            viewContainer.change4dProjection(wheelRotation);
            
        } else {
            if ((modifiersEx & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
                viewContainer.changeCameraEyesDistance((double)wheelRotation / 100);
            } else if ((modifiersEx & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
                // TODO Rotating viewer (preserving location point)? viewContainer.changeCameraFov(-(double)wheelRotation / 8);
            } else {
                viewContainer.changeCameraDistance(-(double)wheelRotation / 8);
            }
        }
        if (viewContainer.needProjection()) {
            paintingArea.repaint();
        }
    }
    
    @Override
    public void windowStateChanged(WindowEvent e) {
        if ((e.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            mainFrame.getControlPanel().setVisible(false);
        } else if ((e.getNewState() & JFrame.NORMAL) == JFrame.NORMAL) {
            mainFrame.getControlPanel().setVisible(true);
        }
    }

    private UNIT_VECTORS getMotionVector(String typedChar) {
        switch (typedChar.charAt(0)) {
        case 'u':
            return UNIT_VECTORS.X;
        case 'i':
            return UNIT_VECTORS.Y;
        case 'o':
            return UNIT_VECTORS.Z;
        case 'p':
            return UNIT_VECTORS.W;
        }
        return null;
    }

    private RotationPlane4DEnum getRotationPlane(String typedChar) {
        switch (typedChar.charAt(0)) {
        case 'q':
            return RotationPlane4DEnum.XW;
        case 'w':
            return RotationPlane4DEnum.YW;
        case 'e':
            return RotationPlane4DEnum.ZW;
        case 'r':
            return RotationPlane4DEnum.XY;
        case 't':
            return RotationPlane4DEnum.XZ;
        case 'y':
            return RotationPlane4DEnum.YZ;
        }
        return null;
    }

    private void doControlAction(String typedChar, boolean shiftPressed) {
        char firstTypedChar = typedChar.charAt(0);
        
        if (!shiftPressed) {
            switch (firstTypedChar) {
            case 'f':
                viewContainer.toggle4dFigureProjection();
                break;
            case 'x':
                viewContainer.toggle3dIntersector();
                break;
            case 'a':
                viewContainer.toggleMovable3DIntersector();
                break;
            case 'd':
                viewContainer.toggleXYZOrts();
                break;
            case 's':
                viewContainer.switchXYZOrts();
                break;
            case 'z':
                mainFrame.stopFigureMovement();
                mainFrame.stopCameraMovement();
                viewContainer.reset();
                break;
            case 'g':
                mainFrame.toggleFigureMover();
                break;
            case 'h':
                mainFrame.toggleFigureMoverIn3d();
                break;
            case 'j':
                mainFrame.toggleCameraMover();
                break;
            case 'v':
                viewContainer.toggleCuttingNWSelector();
                break;
            case 'c':
                viewContainer.toggleCellSelector();
                break;
            case 'b':
                viewContainer.toggleEntireFigureSelector();
                break;
            case 'm':
                viewContainer.toggleSelectMode();
                break;
            case 'l':
                viewContainer.lockUnlockSelectedCell();
                break;
            case 'k':
                viewContainer.clearLockedSelectedCells();
                break;
            case 'n':
                viewContainer.toggleNotCuttingSelected();
                break;
            case '1':
                viewContainer.setMovableProjector(UNIT_VECTORS.X, true);
                break;
            case '2':
                viewContainer.setMovableProjector(UNIT_VECTORS.X, false);
                break;
            case '3':
                viewContainer.setMovableProjector(UNIT_VECTORS.Y, true);
                break;
            case '4':
                viewContainer.setMovableProjector(UNIT_VECTORS.Y, false);
                break;
            case '5':
                viewContainer.setMovableProjector(UNIT_VECTORS.Z, true);
                break;
            case '6':
                viewContainer.setMovableProjector(UNIT_VECTORS.Z, false);
                break;
            case '7':
                viewContainer.setMovableProjector(UNIT_VECTORS.W, true);
                break;
            case '8':
                viewContainer.setMovableProjector(UNIT_VECTORS.W, false);
                break;
            }
        } else if (shiftPressed) {
            switch (firstTypedChar) {
            case '<':
                mainFrame.stopFigureMovement();
                viewContainer.selectPrevCell();
                break;
            case '>':
                mainFrame.stopFigureMovement();
                viewContainer.selectNextCell();
                break;
            case '!':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.X, true);
                break;
            case '@':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.X, false);
                break;
            case '#':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.Y, true);
                break;
            case '$':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.Y, false);
                break;
            case '%':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.Z, true);
                break;
            case '^':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.Z, false);
                break;
            case '&':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.W, true);
                break;
            case '*':
                viewContainer.setAltMovableProjector(UNIT_VECTORS.W, false);
                break;
            }
        }
    }

}