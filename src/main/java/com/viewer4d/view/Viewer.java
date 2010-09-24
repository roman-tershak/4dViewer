package com.viewer4d.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.viewer4d.geometry.Figure;

public interface Viewer {

    public static final double ALTITUDE_LIMIT = Math.PI * 0.49;
    
    public static final Color PAINT_BW_COLOR = new Color(72, 72, 72);
    public static final Color SELECTED_BW_COLOR = new Color(128, 128, 128);

    public static final int MAIN_COLOR_COEF = 108;
    public static final int OPP1_COLOR_COEF = 92;
    public static final int OPP2_COLOR_COEF = 80;
    public static final Color SELECTED_COLORED_COLOR = new Color(32, 128, 32);

    public static final Color ZERO_W_COLOR = new Color(72, 72, 72);
    
    public boolean isColored();
    
    public void setColored(boolean colored);
    
    public double getCurrentDistance();

    public double getCurrentAzimuth();

    public double getCurrentAltitude();

    public void setDistance(double distance);
    
    public void setPosition(double distance, double azimuth, double altitude);
    
    public double getFov();
    
    public void setFov(double fov);
    
    public void changeFov(double delta);
    
    public void rotateCamera(double deltaAzimuth, double deltaAltitude);
    
    public void doProjection(Figure figure);
    
    public void paintOn(JPanel panel, Graphics g);

}
