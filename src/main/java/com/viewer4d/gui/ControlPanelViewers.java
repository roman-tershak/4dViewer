package com.viewer4d.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.viewer4d.view.ViewContainer;

@SuppressWarnings("serial")
public class ControlPanelViewers extends JPanel implements ActionListener {

    private static final String ACTION_COMMAND_COLORED = "colored";
    
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
                "Monoscopic viewer",
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
        
        JCheckBox checkBox = new JCheckBox("Colored", Viewer4DFrame.VIEWER_COLORED_DEFAULT);
        checkBox.setBackground(Color.LIGHT_GRAY);
        checkBox.setFocusable(false);
        checkBox.setActionCommand(ACTION_COMMAND_COLORED);
        checkBox.addActionListener(this);
        add(checkBox);
        
        add(Box.createVerticalStrut(0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        
        if (ACTION_COMMAND_COLORED.equals(actionCommand)) {
            
            boolean colored = ((JCheckBox) e.getSource()).isSelected();
            viewContainer.setViewerColored(colored);
            
        } else {
            switch (Integer.parseInt(e.getActionCommand())) {
            case 0:
                viewContainer.setMonoscopicViewer();
                break;
            case 1:
                viewContainer.setStereoscopicViewer();
                break;
            }
        }
        paintingArea.repaint();
    }

}
