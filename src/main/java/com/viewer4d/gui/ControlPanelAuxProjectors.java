package com.viewer4d.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.viewer4d.geometry.FigureMovable;

@SuppressWarnings("serial")
public class ControlPanelAuxProjectors extends JPanel implements ActionListener, KeyListener {

    private final Viewer4DFrame mainFrame;
    private final JCheckBox[] auxCheckBoxs = new JCheckBox[4];

    public ControlPanelAuxProjectors(Viewer4DFrame mainFrame) {
        super(new GridLayout(0, 1));
        this.mainFrame = mainFrame;
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "Options:"));
        setBackground(Color.LIGHT_GRAY);
        
        JComboBox figureSelector = new JComboBox(
                new Vector<String>(Viewer4DFrame.FIGURE_NAMES));
        figureSelector.setFocusable(false);
        figureSelector.addActionListener(this);
        add(figureSelector);
        
        String[] jbTitles = new String[] {
                "Show 3D space intersection",
                "Show coordinate orts",
                "Show figure projection",
                "Cutting figure projection"
        };
        boolean[] selectedDefaults = new boolean[] {
                Viewer4DFrame.SHOW_3D_SPACE_INTERSECTION_DEFAULT,
                Viewer4DFrame.SHOW_COORDINATE_ORTS_DEFAULT,
                Viewer4DFrame.SHOW_FIGURE_PROJECTION_DEFAULT,
                Viewer4DFrame.CUTTING_FIGURE_PROJECTION_DEFAULT,
        };
        for (int i = 0; i < jbTitles.length; i++) {
            JCheckBox checkBox = new JCheckBox(jbTitles[i], selectedDefaults[i]);
            checkBox.setBackground(Color.LIGHT_GRAY);
            checkBox.setFocusable(false);
            checkBox.setActionCommand(String.valueOf(i));
            checkBox.addActionListener(this);
            add(checkBox);
            auxCheckBoxs[i] = checkBox;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JComboBox) {
            try {
                mainFrame.stopFigureMovement();
                
                FigureMovable newFigure = mainFrame.retrieveFigure(
                        ((JComboBox) source).getSelectedItem().toString());
                
                mainFrame.getViewContainer().setFigure(newFigure);
                mainFrame.getPaintingArea().repaint();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else if (source instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) source;
            switch (Integer.parseInt(e.getActionCommand())) {
            case 0:
                mainFrame.getViewContainer().toggle3dIntersector();
                break;
            case 1:
                mainFrame.getViewContainer().toggleXYZOrts();
                break;
            case 2:
                mainFrame.getViewContainer().toggle4dFigureProjection();
                break;
            case 3:
                mainFrame.getViewContainer().setCuttingFigureSelector(checkBox.isSelected());
                break;
            }
            mainFrame.getPaintingArea().repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    @Override
    public void keyTyped(KeyEvent e) {
        String typedChar = String.valueOf(e.getKeyChar()).toLowerCase();
        switch (typedChar.charAt(0)) {
        case 'x':
            auxCheckBoxs[0].setSelected(!auxCheckBoxs[0].isSelected());
            break;
        case 'd':
            auxCheckBoxs[1].setSelected(!auxCheckBoxs[1].isSelected());
            break;
        case 'f':
            auxCheckBoxs[2].setSelected(!auxCheckBoxs[2].isSelected());
            break;
        }
    }
}
