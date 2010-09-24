package com.viewer4d.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class HelpPanel extends JPanel {

    private static final String HELP_TEXT = "Figure movement:\n" +
                    "   'U' - move by X forward, Shift+'U' - move by X backward\n" +
                    "   'I' - move by Y forward, Shift+'I' - move by Y backward\n" +
                    "   'O' - move by Z forward, Shift+'O' - move by Z backward\n" +
                    "   'P' - move by W forward, Shift+'P' - move by W backward\n" +
                    "\n" +
                    "Figure rotation:\n" +
                    "   'R' - rotate by XY, Shift+'R' - rotate by XY in opposite direction\n" +
                    "   'T' - rotate by XZ, Shift+'T' - rotate by XZ in opposite direction\n" +
                    "   'Y' - rotate by YZ, Shift+'Y' - rotate by YZ in opposite direction\n" +
                    "   'Q' - rotate by XW, Shift+'Q' - rotate by XW in opposite direction\n" +
                    "   'W' - rotate by YW, Shift+'W' - rotate by YW in opposite direction\n" +
                    "   'E' - rotate by ZW, Shift+'E' - rotate by ZW in opposite direction\n" +
                    "\n" +
                    "Projection control:\n" +
                    "   Wheel rotation + the right button down - change 4D projector distance\n" +
                    "\n" +
                    "Camera control:\n" +
                    "   Wheel rotation - change the camera distance\n" +
                    "   Wheel rotation + shift button down - change the camera eyes distance (stereo only)\n" +
                    "   Dragging mouse - move the camera\n" +
                    "\n" +
                    "Other control keys:\n" +
                    "   'Z' - reset the figure\n" +
                    "   'X' - show or hide 3D space intersection with the figure\n" +
                    "   'D' - show or hide the coordinate orts\n" +
                    "   'S' - switch the type of the coordinate orts\n" +
                    "   'F' - show or hide the figure\n" +
                    "   'C' - switch on or off the figure cell selection mode\n" +
                    "   Shift+('<' or '>') - select next figure cell (when the figure cell selection mode is on)\n" +
                    "   Shift+('1', '2'... '8') - change projector position (movable projector only)\n" +
                    "   'G' - start or stop rotation of the figute in a random way\n" +
                    "   'F1' - show or hide this help, Escape - hide the help or quit";
    
    private final JPanel glassPane;
    private boolean hidden = true;

    public HelpPanel(JPanel glassPane) {
        this.glassPane = glassPane;
    }

    public void toggleHelp(Dimension prefSize) {
        if (hidden) {
            showHelp(prefSize);
        } else {
            hideHelp();
        }
    }

    public void showHelp(Dimension prefSize) {
        JTextArea jTextArea = new JTextArea(HELP_TEXT);
        jTextArea.setPreferredSize(new Dimension(
                (int) prefSize.getWidth(), (int) prefSize.getHeight() - 5));
        jTextArea.setEditable(false);
        jTextArea.setFocusable(false);
        jTextArea.setForeground(new Color(0, 128, 0));
        jTextArea.setBackground(Color.BLACK);
        jTextArea.setOpaque(true);
        
        glassPane.add(jTextArea);
        
        glassPane.setFocusable(false);
        glassPane.setEnabled(false);
        glassPane.setVisible(true);
        
        hidden = false;
    }

    public void hideHelp() {
        glassPane.setVisible(false);
        glassPane.removeAll();
        hidden = true;
    }
    
    public boolean isHelpShown() {
        return !hidden;
    }
    
}
