package com.viewer4d.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.viewer4d.view.ViewContainer;

@SuppressWarnings("serial")
public class ControlPanelViewers extends JPanel implements ActionListener {

    private ViewContainer viewContainer;
    private JPanel paintingArea;

    public ControlPanelViewers(ViewContainer viewContainer, JPanel paintingArea) {
        super(new GridLayout(0, 1));
        this.viewContainer = viewContainer;
        this.paintingArea = paintingArea;
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "Viewers:"));
        setBackground(Color.LIGHT_GRAY);
        
        ButtonGroup buttonGroupViewers = new ButtonGroup();
        
        String[] jbTitles = new String[] {
                "Colored monoscopic viewer",
                "Monoscopic viewer",
                "Colored stereoscopic viewer",
                "Stereoscopic viewer",
        };
        for (int i = 0; i < jbTitles.length; i++) {
            JRadioButton jb4dP = new JRadioButton(jbTitles[i], (i == 0 ? true : false));
            jb4dP.setBackground(Color.LIGHT_GRAY);
            jb4dP.setFocusable(false);
            jb4dP.setActionCommand(String.valueOf(i));
            jb4dP.addActionListener(this);
            
            buttonGroupViewers.add(jb4dP);
            add(jb4dP);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (Integer.parseInt(e.getActionCommand())) {
        case 0:
            viewContainer.setMonoscopicViewer(true);
            break;
        case 1:
            viewContainer.setMonoscopicViewer(false);
            break;
        case 2:
            viewContainer.setStereoscopicViewer(true);
            break;
        case 3:
            viewContainer.setStereoscopicViewer(false);
            break;
        }
        paintingArea.repaint();
    }

}
