package com.viewer4d.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class HelpPanel extends JPanel {

    private static final String HELP_TEXT = "Figure movement:\n" +
    "   'u' - move by X forward, Shift+'u' - move by X backward\n" +
    "   'i' - move by Y forward, Shift+'i' - move by Y backward\n" +
    "   'o' - move by Z forward, Shift+'o' - move by Z backward\n" +
    "   'p' - move by W forward, Shift+'p' - move by W backward\n" +
    "\n" +
    "Figure rotation:\n" +
    "   'r' - rotate by XY, Shift+'r' - rotate by XY in opposite direction\n" +
    "   't' - rotate by XZ, Shift+'t' - rotate by XZ in opposite direction\n" +
    "   'y' - rotate by YZ, Shift+'y' - rotate by YZ in opposite direction\n" +
    "   'q' - rotate by XW, Shift+'q' - rotate by XW in opposite direction\n" +
    "   'w' - rotate by YW, Shift+'w' - rotate by YW in opposite direction\n" +
    "   'e' - rotate by ZW, Shift+'e' - rotate by ZW in opposite direction\n" +
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
    "   'z' - reset the figure\n" +
    "   'x' - show or hide 3D space intersection with the figure\n" +
    "   'd' - show or hide the coordinate orts\n" +
    "   's' - switch the type of the coordinate orts\n" +
    "   'f' - show or hide the figure\n" +
    "   'c' - switch on cutting non-selected cell projection mode\n" +
    "   'v' - switch on cutting by W ort figure projection\n" +
    "   'b' - switch on the entire figure projection (effectivelly switches off any cutting mode selected before)\n" +
    "   'a' - toggle an additional movable 3D space intersector\n" +
    "   Ctrl+(the figure movement control combinations) - moving the additional 3D space intersector\n" +
    "   'm' - switch on or off the figure cell selection mode\n" +
    "   Shift+('<' or '>') - select next figure cell (when the figure cell selection mode is on)\n" +
    "   Ctrl+('<' or '>') - select next sibling cell for the selected cell (when the figure cell selection mode is on)\n" +
    "   Ctrl+'/' - select or unselect all sibling cells for the selected cell (when the figure cell selection mode is on)\n" +
    "   'l' - lock/unlock the selected cell (when the figure cell selection mode is on)\n" +
    "   'k' - clear all the locked selected cells (when the figure cell selection mode is on)\n" +
    "   'n' - cut or not the selected cells (the selection mode and cutting by W ort projection are on)\n" +
    "   '1', '2'... '8' - change the projector position (movable projector only)\n" +
    "   Shift+('1', '2'... '8') - change the alternative projector position (two channel viewer only)\n" +
    "   'g' - toggle rotation of the figute in a random way\n" +
    "   'h' - toggle random figure rotation in XYZ space only\n" +
    "   'j' - toggle camera precession for better filling of 3D space\n" +
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
