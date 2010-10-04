package com.viewer4d.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.viewer4d.view.ViewContainer;

@SuppressWarnings("serial")
public class ControlPanelSelectors extends JPanel implements ActionListener, KeyListener {

    public static final int ENTIRE_FIGURE_SELECTOR_NUMBER = 0;
    public static final int CUTTING_NEGATIVE_W_SELECTOR_NUMBER = 1;
    public static final int CUTTING_NONSELECTED_CELL_SELECTOR_NUMBER = 2;

    
    String[] TITLES = new String[] {
            "Entire figure projection",
            "Cutting negative W projection",
            "Cutting non selected cells"
    };

    String[] TIPS = new String[] {
            "This selector selects the entire figure.",
            "This selector selects only those parts of the figure that have non-negative W coordinate.",
            "This selector selects only those cells of figure that are selected.",
    };
    
    private final JRadioButton[] selectorRadioButtons = new JRadioButton[3];
    private ViewContainer viewContainer;
    private JPanel paintingArea;

    public ControlPanelSelectors(ViewContainer viewContainer, JPanel paintingArea) {
        super(new GridLayout(0, 1));
        
        this.viewContainer = viewContainer;
        this.paintingArea = paintingArea;
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "Selectors:"));
        setBackground(Color.LIGHT_GRAY);
        
        ButtonGroup buttonGroupSelectors = new ButtonGroup();
        
        for (int i = 0; i < TITLES.length; i++) {
            JRadioButton jbSelector = new JRadioButton(TITLES[i], (i == 0 ? true : false));
            jbSelector.setToolTipText(TIPS[i]);
            jbSelector.setBackground(Color.LIGHT_GRAY);
            jbSelector.setFocusable(false);
            jbSelector.setActionCommand(String.valueOf(i));
            jbSelector.addActionListener(this);
            
            buttonGroupSelectors.add(jbSelector);
            add(jbSelector);
            
            selectorRadioButtons[i] = jbSelector;
        }
        
        add(Box.createVerticalStrut(0));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        int selectorNumber = Integer.parseInt(actionCommand);
        viewContainer.toggleSelector(selectorNumber);
        paintingArea.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        String typedChar = String.valueOf(e.getKeyChar()).toLowerCase();
        switch (typedChar.charAt(0)) {
        case 'v':
            selectorRadioButtons[CUTTING_NEGATIVE_W_SELECTOR_NUMBER].setSelected(true);
            break;
        case 'c':
            selectorRadioButtons[CUTTING_NONSELECTED_CELL_SELECTOR_NUMBER].setSelected(true);
            break;
        case 'b':
            selectorRadioButtons[ENTIRE_FIGURE_SELECTOR_NUMBER].setSelected(true);
            break;
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
