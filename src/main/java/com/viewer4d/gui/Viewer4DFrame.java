package com.viewer4d.gui;

import com.viewer4d.config.FigureFactory;
import com.viewer4d.geometry.figure.FigureMovable;
import com.viewer4d.view.ViewContainer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("serial")
public class Viewer4DFrame extends JFrame {

    public static final int FRAME_WIDTH = 1024;
    public static final int FRAME_HEIGHT = 720;
    
    public static final boolean SHOW_3D_SPACE_INTERSECTION_DEFAULT = true;
    public static final boolean SHOW_COORDINATE_ORTS_DEFAULT = true;
    public static final boolean SHOW_FIGURE_PROJECTION_DEFAULT = true;

    public static final boolean CUTTING_FIGURE_PROJECTION_DEFAULT = false;
    public static final boolean CUTTING_NON_SELECTED_DEFAULT = false;
    
    public static final int ACTIVE_VIEWER_NUMBER_DEFAULT = 0;
    

    class PaintingArea extends JPanel {
        @Override
        protected void paintChildren(Graphics g) {
            viewContainer.paintOn(this, g);
        }
    }
    
    static final List<String> FIGURE_NAMES = new ArrayList<String>(Arrays.asList(
            "hypercube",
            "hypercubepyramid",
            "octahedronpyramid",
            "pentachoron",
            "hexadecachoron",
            "icositetrachoron",
            "hecatonicosachoron",
            "hexacosichoron",
            "orts4d",
            "cube",
            "dodecahedron",
            "icosahedron",
            "octahedron",
            "tetrahedron",
            "square",
            "triangle",
            "segment",
            "righthand"
    ));

    protected static final Dimension FRAME_SIZE = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
    
    private PaintingArea paintingArea;
    private JPanel controlPanel;
    
    private ControlPanelSelectors controlPanelSelectors;
    private ControlPanel4DProjectors controlPanel4DProjectors;
    private ControlPanelViewers controlPanelViewers;
    private ControlPanelAuxProjectors controlPanelAuxProjectors;
    
    private HelpPanel helpPanel;
    
    private RandomFigureMover figureMover;
    
    private ViewContainer viewContainer;

    private Map<String, FigureMovable> figuresInPackage = new HashMap<String, FigureMovable>();
    public static final boolean VIEWER_COLORED_DEFAULT = true;


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
        controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);
        
        JPanel controlPanels = new JPanel(new GridLayout(1, 3));
        controlPanels.setBackground(Color.LIGHT_GRAY);
        
        controlPanelSelectors = new ControlPanelSelectors(viewContainer, paintingArea);
        controlPanel4DProjectors = new ControlPanel4DProjectors(viewContainer, paintingArea);
        controlPanelViewers = new ControlPanelViewers(viewContainer, paintingArea);
        controlPanelAuxProjectors = new ControlPanelAuxProjectors(this);
        
        controlPanels.add(controlPanelSelectors);
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
        contentPane.addKeyListener(controlPanelSelectors);
        contentPane.addMouseListener(eventAdapter);
        contentPane.addMouseMotionListener(eventAdapter);
        contentPane.addMouseWheelListener(eventAdapter);
        
        this.addWindowStateListener(eventAdapter);
    }
    
    protected FigureMovable retrieveFigure(String figureName) throws Exception {
        FigureMovable figure = figuresInPackage.get(figureName);
        if (figure == null) {
            figure = FigureFactory.getInstance().loadFromResources(
                    "/" + figureName + ".xml");
            
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
    
    // Figure mover control methods
    protected RandomFigureMover getFigureMover() {
        return figureMover;
    }
    
    protected void toggleFigureMover() {
        initFigureMover();
        if (figureMover.isMoving()) {
            figureMover.stopMove();
        } else {
            figureMover.startMove();
        }
    }

    protected void toggleFigureMoverIn3d() {
        initFigureMover();
        figureMover.setIn3dOnly(!figureMover.isIn3dOnly());
        if (figureMover.isIn3dOnly()) {
            viewContainer.getFigure().reset();
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
    
    protected void toggleCameraMover() {
        initFigureMover();
        if (figureMover.isCameraMoving()) {
            figureMover.stopCameraMove();
        } else {
            figureMover.startCameraMove();
        }
    }

    protected void stopCameraMovement() {
        if (figureMover != null) {
            figureMover.stopCameraMove();
        }
    }
    
    private void initFigureMover() {
        if (figureMover == null) {
            figureMover = new RandomFigureMover(this);
            figureMover.start();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new Viewer4DFrame();
    }

}
