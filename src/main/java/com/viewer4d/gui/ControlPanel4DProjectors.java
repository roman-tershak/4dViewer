package com.viewer4d.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.viewer4d.view.ViewContainer;

@SuppressWarnings("serial")
public class ControlPanel4DProjectors extends JPanel implements ActionListener {

    private static final String ACTION_COMMAND_PERSPECTIVE = "perspective";

    String[] TITLES = new String[] {
            "Perspective projector on XYZ along W",
            "Perspective movable projector",
            "Perspective moving projector",
    };

    String[] TIPS = new String[] {
            "This projector is fixed and located below XYZ along W ort. It makes perspective projection.\n The projector distance on W ort can be changed by mouse wheel with the right button pressed.",
            "This projector is initially located below XYZ along W ort. It makes perspective projection.\n The projector can be re-located on either ort positive or negative side.\n The projector distance from the figure can be changed by mouse wheel with the right button pressed.",
            "This projector is fixed on the figure and makes perspective projection.\n The projector distance from the figure can be changed by mouse wheel with the right button pressed.",
//            "This projector is fixed and located below XYZ along W ort. It makes perspective projection,\n but only those points that are in or above XYZ space (not negative w). The projector distance on W ort can be changed by mouse wheel with the right button pressed.",
    };
    
    private ViewContainer viewContainer;
    private JPanel paintingArea;

    public ControlPanel4DProjectors(ViewContainer viewContainer, JPanel paintingArea) {
        super(new GridLayout(0, 1));
        
        this.viewContainer = viewContainer;
        this.paintingArea = paintingArea;
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "4D to 3D Projectors:"));
        setBackground(Color.LIGHT_GRAY);
        
        ButtonGroup buttonGroup4DProjectors = new ButtonGroup();
        
        for (int i = 0; i < TITLES.length; i++) {
            JRadioButton jb4dP = new JRadioButton(TITLES[i], (i == 0 ? true : false));
            jb4dP.setToolTipText(TIPS[i]);
            jb4dP.setBackground(Color.LIGHT_GRAY);
            jb4dP.setFocusable(false);
            jb4dP.setActionCommand(String.valueOf(i));
            jb4dP.addActionListener(this);
            
            buttonGroup4DProjectors.add(jb4dP);
            add(jb4dP);
        }
        
        JCheckBox checkBox = new JCheckBox("Perspective", true);
        checkBox.setBackground(Color.LIGHT_GRAY);
        checkBox.setFocusable(false);
        checkBox.setActionCommand(ACTION_COMMAND_PERSPECTIVE);
        checkBox.addActionListener(this);
        add(checkBox);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (ACTION_COMMAND_PERSPECTIVE.equals(actionCommand)) {
            boolean pespective = ((JCheckBox) e.getSource()).isSelected();
            viewContainer.set4DProjectorsPerspective(pespective);
        } else {
            int number4dP = Integer.parseInt(actionCommand);
            viewContainer.toggle4DProjector(number4dP);
        }
        paintingArea.repaint();
    }

}
