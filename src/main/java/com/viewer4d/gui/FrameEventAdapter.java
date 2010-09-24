package com.viewer4d.gui;

import static java.awt.event.InputEvent.SHIFT_MASK;

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
        String keyText = KeyEvent.getKeyText(e.getKeyCode());
        if (keyText.equals("F1")) {
            mainFrame.toggleHelp();
            
        } else if (keyText.equals("Escape")) {
            mainFrame.handleEscape();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        boolean shiftPressed = (e.getModifiers() & SHIFT_MASK) == SHIFT_MASK;
        String typedChar = String.valueOf(e.getKeyChar()).toLowerCase();
        
        UNIT_VECTORS vector = null;
        RotationPlane4DEnum rotationPlane = null;
        
        if ((vector = getMotionVector(typedChar)) != null) {
            mainFrame.stopFigureMovement();
            viewContainer.moveFigureOneStep(vector, !shiftPressed);
        } else if ((rotationPlane = getRotationPlane(typedChar)) != null) {
            mainFrame.stopFigureMovement();
            viewContainer.rotateFigureOneStep(rotationPlane, !shiftPressed);
        } else {
            doControlAction(typedChar, shiftPressed);
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
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            RandomFigureMover figureMover = mainFrame.getFigureMover();
            if (figureMover != null) {
                figureMover.resumeMove();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currPoint = e.getPoint();
        if (prevDragPoint != null) {
            int xDelta = -(int)(currPoint.getX() - prevDragPoint.getX());
            int yDelta = (int)(currPoint.getY() - prevDragPoint.getY());
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
            case 'd':
                viewContainer.toggleXYZOrts();
                break;
            case 's':
                viewContainer.switchXYZOrts();
                break;
            case 'z':
                mainFrame.stopFigureMovement();
                viewContainer.resetFigure();
                break;
            case 'g':
                mainFrame.toggleFigureMover();
                break;
            case 'c':
                viewContainer.toggleSelectMode();
                break;
            }
        } else {
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
                viewContainer.setMovableProjector(UNIT_VECTORS.X, true);
                break;
            case '@':
                viewContainer.setMovableProjector(UNIT_VECTORS.X, false);
                break;
            case '#':
                viewContainer.setMovableProjector(UNIT_VECTORS.Y, true);
                break;
            case '$':
                viewContainer.setMovableProjector(UNIT_VECTORS.Y, false);
                break;
            case '%':
                viewContainer.setMovableProjector(UNIT_VECTORS.Z, true);
                break;
            case '^':
                viewContainer.setMovableProjector(UNIT_VECTORS.Z, false);
                break;
            case '&':
                viewContainer.setMovableProjector(UNIT_VECTORS.W, true);
                break;
            case '*':
                viewContainer.setMovableProjector(UNIT_VECTORS.W, false);
                break;
            }
        }
    }

}