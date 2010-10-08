package com.viewer4d.gui;

import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.view.ViewContainer;

public class RandomFigureMover extends Thread {

    protected static final double ONE_ROTATE_STEP = Math.PI/1600;
    protected static final double FULL_CIRCLE_RADIANS = Math.PI * 2;
    protected static final double CAMERA_PRECESSION_AMPLITUDE = 0.0064;
    protected static final double CAMERA_PRECESSION_STEP = Math.PI / 64;
    
    protected static final int CHANGE_PERIOD_1 = 1000;
    protected static final int CHANGE_PERIOD_2 = 250;
    protected static final int CHANGE_PERIOD_CAMERA_ROTATION = 1;
    protected static final int WAIT_INTERVAL = 30;

    private final Viewer4DFrame viewer4dFrame;
    private ViewContainer viewContainer;
    
    private volatile boolean move = false;
    private volatile boolean moveCamera = false;
    private volatile boolean stop = false;
    private boolean paused;
    private boolean pausedCamera;
    
    private volatile boolean in3dOnly = false;
    
    private final Object signaller = new Object();

    private boolean currForward;
    private RotationPlane4DEnum currRotationPlane1;
    private RotationPlane4DEnum currRotationPlane2;
    
    private double cameraPrecessionRadians = 0;
    
    private int countMoves = 0;
    private int countCameraMoves = 0;


    
    public RandomFigureMover(Viewer4DFrame viewer4dFrame) {
        super("Random Figure Mover");
        this.viewer4dFrame = viewer4dFrame;
        viewContainer = viewer4dFrame.getViewContainer();
        
        currRotationPlane1 = getRandomRotationPlane(null);
        currRotationPlane2 = getRandomRotationPlane(null);
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                if (move) {
                    moveRandomly();
                }
                
                if (moveCamera) {
                    moveCamera();
                }
                synchronized (signaller) {
                    signaller.wait(WAIT_INTERVAL);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private void moveRandomly() {
        if (countMoves % CHANGE_PERIOD_1 == 0) {
            currForward = Math.random() < 0.5;
        }
        if (countMoves % CHANGE_PERIOD_2 == 0) {
            if (Math.random() < 0.5) {
                currRotationPlane1 = getRandomRotationPlane(currRotationPlane2);
            } else {
                currRotationPlane2 = getRandomRotationPlane(currRotationPlane1);
            }
        }
        countMoves++;
        
        double amount = currForward ? ONE_ROTATE_STEP : -ONE_ROTATE_STEP;
        viewContainer.rotateFigureDouble(currRotationPlane1, amount, currRotationPlane2, amount);
        
        viewer4dFrame.getPaintingArea().repaint();
    }

    private RotationPlane4DEnum getRandomRotationPlane(RotationPlane4DEnum other) {
        RotationPlane4DEnum rotationPlane = other;
        int iter = 0;
        while (rotationPlane == other && iter < 20) {
            rotationPlane = RotationPlane4DEnum.values()[(int) Math.floor(Math.random() * 
                    (in3dOnly ? 3 : 6))];
            iter++;
        }
        return rotationPlane;
    }
    
    public boolean isIn3dOnly() {
        return in3dOnly;
    }
    public void setIn3dOnly(boolean in3dOnly) {
        this.in3dOnly = in3dOnly;
        if (in3dOnly) {
            currRotationPlane1 = getRandomRotationPlane(currRotationPlane2);
            currRotationPlane2 = getRandomRotationPlane(currRotationPlane1);
        }
    }
    
    private void moveCamera() {
        if (countCameraMoves++ % CHANGE_PERIOD_CAMERA_ROTATION != 0) {
            return;
        }
        
        double azimuthDelta = CAMERA_PRECESSION_AMPLITUDE * Math.cos(cameraPrecessionRadians);
        double altitudeDelta;
        if (!move) {
            altitudeDelta = CAMERA_PRECESSION_AMPLITUDE * Math.sin(cameraPrecessionRadians);
        } else {
            altitudeDelta = 0;
        }
        viewContainer.rotateCamera(azimuthDelta, altitudeDelta);
        
        cameraPrecessionRadians -= CAMERA_PRECESSION_STEP;
        if (cameraPrecessionRadians < FULL_CIRCLE_RADIANS) {
            cameraPrecessionRadians -= FULL_CIRCLE_RADIANS;
        }
        
        viewer4dFrame.getPaintingArea().repaint();
    }
    
    public void startMove() {
        move = true;
        paused = false;
        notifyMoverThread();
    }
    
    public void stopMove() {
        move = false;
        paused = false;
        notifyMoverThread();
    }

    public boolean isMoving() {
        return move;
    }

    public void pauseMove() {
        if (move) {
            paused = true;
            move = false;
            notifyMoverThread();
        }
    }
    
    public void resumeMove() {
        if (paused) {
            paused = false;
            if (!move) {
                move = true;
                notifyMoverThread();
            }
        }
    }

    public boolean isCameraMoving() {
        return moveCamera;
    }
    
    public void startCameraMove() {
        moveCamera = true;
        notifyMoverThread();
    }
    
    public void stopCameraMove() {
        moveCamera = false;
        notifyMoverThread();
    }
    
    public void pauseCameraMove() {
        if (moveCamera) {
            pausedCamera = true;
            moveCamera = false;
            notifyMoverThread();
        }
    }

    public void resumeCameraMove() {
        if (pausedCamera) {
            pausedCamera = false;
            if (!moveCamera) {
                moveCamera = true;
                notifyMoverThread();
            }
        }
    }

    public void stopThread() {
        stop = true;
        notifyMoverThread();
    }
    
    private void notifyMoverThread() {
        synchronized (signaller) {
            signaller.notify();
        }
    }
}
