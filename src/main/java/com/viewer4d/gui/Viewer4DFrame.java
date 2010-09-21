package com.viewer4d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.awt.VerticalBagLayout;

import com.viewer4d.config.FigureFactory;
import com.viewer4d.geometry.FigureMovable;
import com.viewer4d.view.ViewContainer;

@SuppressWarnings("serial")
public class Viewer4DFrame extends JFrame {

    class PaintingArea extends JPanel {
        @Override
        protected void paintChildren(Graphics g) {
            viewContainer.paintOn(this, g);
        }
    }
    
    static final List<String> FIGURE_NAMES = new ArrayList<String>(Arrays.asList(
            "hypercube.xml",
            "hypercubepyramid.xml",
            "octahedronpyramid.xml",
            "pentachoron.xml",
            "hexadecachoron.xml",
            "icositetrachoron.xml",
            "hecatonicosachoron.xml",
            "hexacosichoron.xml",
            "orts4d.xml",
            "cube.xml",
            "dodecahedron.xml",
            "icosahedron.xml",
            "octahedron.xml",
            "tetrahedron.xml",
            "square.xml",
            "segment.xml",
            "righthand.xml"
    ));

    protected static final Dimension FRAME_SIZE = new Dimension(800, 640);
    
    private PaintingArea paintingArea;
    private JPanel controlPanel;
    
    private ControlPanel4DProjectors controlPanel4DProjectors;
    private ControlPanelViewers controlPanelViewers;
    private ControlPanelAuxProjectors controlPanelAuxProjectors;
    
    private HelpPanel helpPanel;
    
    private RandomFigureMover figureMover;
    
    private ViewContainer viewContainer;

    private Map<String, FigureMovable> figuresInPackage = new HashMap<String, FigureMovable>();


    public Viewer4DFrame() throws Exception {
        super();
        
        viewContainer = new ViewContainer(
                retrieveFigure(FIGURE_NAMES.get(0)));
        
        viewContainer.doFullProjection();
        initializeUI();
    }

    public JPanel getPaintingArea() {
        return paintingArea;
    }
    
    public ViewContainer getViewContainer() {
        return viewContainer;
    }
    
    public JPanel getControlPanel() {
        return controlPanel;
    }
    
    private void initializeUI() {
        setTitle("4D Viewer");
        setSize(FRAME_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initContentPane();
        initEventSubsystem();
        
        setVisible(true);
    }

    private void initContentPane() {
        createPainingArea();
        createControlPanel();
    }

    private void createPainingArea() {
        paintingArea = new PaintingArea();
        paintingArea.setBorder(BorderFactory.createLoweredBevelBorder());
        paintingArea.setBackground(Color.BLACK);
        getContentPane().add(paintingArea, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new VerticalBagLayout());
        controlPanel.setBackground(Color.LIGHT_GRAY);
        
        JPanel controlPanels = new JPanel(new GridLayout(1, 3));
        controlPanels.setBackground(Color.LIGHT_GRAY);
        
        controlPanel4DProjectors = new ControlPanel4DProjectors(viewContainer, paintingArea);
        controlPanelViewers = new ControlPanelViewers(viewContainer, paintingArea);
        controlPanelAuxProjectors = new ControlPanelAuxProjectors(this);
        controlPanels.add(controlPanel4DProjectors);
        controlPanels.add(controlPanelViewers);
        controlPanels.add(controlPanelAuxProjectors);
        
        controlPanel.add(controlPanels);
        
        JPanel helpPane = new JPanel(new GridLayout(1, 1));
        helpPane.setBackground(Color.LIGHT_GRAY);
        helpPane.setBorder(BorderFactory.createEtchedBorder());
        helpPane.add(new JLabel("Press 'F1' for help. Move mouse over the controls to see help tips."));
        
        controlPanel.add(helpPane);
        
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    private void initEventSubsystem() {
        FrameEventAdapter eventAdapter = new FrameEventAdapter(this);
        
        Container contentPane = getContentPane();
        contentPane.setFocusable(true);
        
        contentPane.addKeyListener(eventAdapter);
        contentPane.addKeyListener(controlPanelAuxProjectors);
        contentPane.addMouseListener(eventAdapter);
        contentPane.addMouseMotionListener(eventAdapter);
        contentPane.addMouseWheelListener(eventAdapter);
        
        this.addWindowStateListener(eventAdapter);
    }
    
    protected FigureMovable retrieveFigure(String figureName) throws Exception {
        FigureMovable figure = figuresInPackage.get(figureName);
        if (figure == null) {
            figure = FigureFactory.getInstance().loadFromResources(
                    "/" + figureName);
            
            figure.getCells();
            
            figuresInPackage.put(figureName, figure);
        }
        return figure;
    }
    
    protected void toggleHelp() {
        if (helpPanel == null) {
            helpPanel = new HelpPanel((JPanel) getGlassPane());
        }
        helpPanel.toggleHelp(paintingArea.getSize());
    }
    
    protected void handleEscape() {
        if (helpPanel != null && helpPanel.isHelpShown()) {
            helpPanel.hideHelp();
        } else {
            if (figureMover != null) {
                figureMover.stopThread();
            }
            dispose();
        }
    }
    
    protected RandomFigureMover getFigureMover() {
        return figureMover;
    }
    
    protected void toggleFigureMover() {
        if (figureMover == null) {
            figureMover = new RandomFigureMover(this);
            figureMover.start();
        }
        if (figureMover.isMoving()) {
            figureMover.stopMove();
        } else {
            figureMover.startMove();
        }
    }
    
    protected void startFigureMovement() {
        if (figureMover != null) {
            figureMover.startMove();
        }
    }
    
    protected void stopFigureMovement() {
        if (figureMover != null) {
            figureMover.stopMove();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new Viewer4DFrame();
    }

}
