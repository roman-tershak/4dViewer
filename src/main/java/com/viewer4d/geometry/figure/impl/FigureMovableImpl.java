package com.viewer4d.geometry.figure.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.viewer4d.config.FigureFactory;
import com.viewer4d.geometry.RotationPlane4DEnum;
import com.viewer4d.geometry.figure.Cell;
import com.viewer4d.geometry.figure.Edge;
import com.viewer4d.geometry.figure.Face;
import com.viewer4d.geometry.figure.FigureMovable;
import com.viewer4d.geometry.figure.Vertex;
import com.viewer4d.geometry.simple.MovablePoint;
import com.viewer4d.geometry.simple.Plane;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Space;
import com.viewer4d.geometry.simple.Vector;
import com.viewer4d.geometry.util.Util;

public class FigureMovableImpl extends FigureBaseImpl implements FigureMovable {

    private MovablePoint centrum;
    private final double precision;
    
    private Map<MovablePoint, double[]> initialCoords;

    public FigureMovableImpl(Collection<Face> faces, Collection<Edge> edges) {
        this(faces, edges, Pointable.PRECISION);
    }
    
    public FigureMovableImpl(Collection<Face> faces, Collection<Edge> edges, double precision) {
        super(faces, edges);
        this.precision = precision;

        setCentrum();
        setEdgeFaces();

        storeFigureCoords();
    }
    
    @Override
    public Pointable getCentrum() {
        return centrum;
    }
    
    public double getPrecision() {
        return precision;
    }
    
    @Override
    protected void setCells() {
        super.setCells();
        
        traverseFaces();
        removeOpenCells();
    }
    
    private void setCentrum() {
        centrum = new Vertex(0, 0, 0, 0);
    }

    private void setEdgeFaces() {
        for (Face face : getFaces()) {
            for (Edge edge : face.getEdges()) {
                edge.getFaces().add(face);
            }
        }
    }

    private void traverseFaces() {
        Set<Space> uniqueSpaces = new HashSet<Space>();
        Map<Cell, Space> cellToSpaces = new HashMap<Cell, Space>();
        Map<Space, Cell> spaceToCells = new HashMap<Space, Cell>();
        
        double figurePrecision = getPrecision();
        
        for (Face currFace : getFaces()) {
            Set<Cell> currCells = currFace.getCells();
            
            Plane currFacePlane = Util.create2dPlaneFrom(currFace, figurePrecision);
            
            nextSiblings: for (Face siblingFace : currFace.getSiblings()) {
                Set<Cell> siblingCells = siblingFace.getCells();
                
                // omit faces lying in the same plane as the original face
                if (Util.isLyingOn(siblingFace, currFacePlane)) {
                    continue nextSiblings;
                }
                
                for (Cell currCell : currCells) {
                    // omit faces already participated in one of the current cells
                    if (currCell.getFaces().contains(siblingFace)) {
                        continue nextSiblings;
                        
                    } else if (Util.isLyingOn(siblingFace, cellToSpaces.get(currCell))) {
                        
                        currCell.addFace(siblingFace);
                        continue nextSiblings;
                    }
                }
                
                for (Cell siblingCell : siblingCells) {
                    if (Util.isLyingOn(currFace, cellToSpaces.get(siblingCell))) {
                        siblingCell.addFace(currFace);
                        
                        continue nextSiblings;
                    }
                }
                
                Space newSpace = Util.create3dSpaceFrom(currFace, siblingFace, figurePrecision);
                if (!Util.isLyingOn(currFace, newSpace) || !Util.isLyingOn(siblingFace, newSpace)) {
                    throw new RuntimeException("Creation of 3D spaces logic does not work!");
                }
                
                Cell newCell;
                if (uniqueSpaces.contains(newSpace)) {
                    newCell = spaceToCells.get(newSpace);
                    
                } else {
                    newCell = new Cell();
                    uniqueSpaces.add(newSpace);
                    cellToSpaces.put(newCell, newSpace);
                    spaceToCells.put(newSpace, newCell);

                    getCells().add(newCell);
                }

                newCell.addFace(currFace);
                newCell.addFace(siblingFace);
            }
        }
    }
    
    private void removeOpenCells() {
        for (Iterator<Cell> cellIter = getCells().iterator(); cellIter.hasNext(); ) {
            
            Cell cell = cellIter.next();
            if (isCellOpen(cell)) {
                cellIter.remove();
            }
        }
    }
    
    private boolean isCellOpen(Cell cell) {
        Set<Face> cellFaces = cell.getFaces();
        
        for (Face face : cellFaces) {
            for (Edge edge : face.getEdges()) {
                
                Set<Face> faceSiblingsByEdge = new HashSet<Face>(face.getSiblings(edge));
                faceSiblingsByEdge.retainAll(cellFaces);
                if (faceSiblingsByEdge.size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public synchronized void move(Vector vector) {
        double[] vectorCoords = vector.getCoords();
        for (MovablePoint vertex : getVertices()) {
            vertex.move(vectorCoords);
        }
        centrum.move(vectorCoords);
    }

    @Override
    public synchronized void rotate(RotationPlane4DEnum rotationPlane, double radians) {
        double[][] rotationMatrix = rotationPlane.getRotationMatrix(radians);

        for (MovablePoint vertex : getVertices()) {
            vertex.rotate(rotationMatrix, centrum);
        }
    }
    
    @Override
    public void rotate(RotationPlane4DEnum rotationPlane, double radians, Pointable center) {
        throw new UnsupportedOperationException("This method is not supported.");
    }
    
    @Override
    public synchronized void reset() {
        restoreFigureCoords();
    }

    private void storeFigureCoords() {
        Collection<Vertex> vertices = getVertices();
        initialCoords = new HashMap<MovablePoint, double[]>(vertices.size());
        for (Vertex vertex : vertices) {
            initialCoords.put(vertex, vertex.getCoords().clone());
        }
        initialCoords.put(centrum, centrum.getCoords().clone());
    }

    private void restoreFigureCoords() {
        Collection<Vertex> vertices = getVertices();
        for (MovablePoint vertex : vertices) {
            vertex.setCoords(initialCoords.get(vertex).clone());
        }
        centrum.setCoords(initialCoords.get(centrum).clone());
    }

    public static void main(String[] args) throws Exception {
        FigureMovable figure;
        String[] figures = new String[] {
                "cube.xml",            
                "dodecahedron.xml",    
                "hecatonicosachoron.xml",
                "hexacosichoron.xml",
                "hexadecachoron.xml",  
                "hypercube.xml",       
                "hypercubepyramid.xml",
                "icosahedron.xml",
                "icositetrachoron.xml",
                "octahedron.xml",      
                "octahedronpyramid.xml",
                "pentachoron.xml",     
                "tetrahedron.xml",     
                "square.xml"
        };
        for (int i = 0; i < figures.length; i++) {
            String figureName = "/" + figures[i];
            try {
                System.out.println();
                System.out.println(figureName);
                figure = FigureFactory.getInstance().loadFromResources(figureName);
                System.out.println(figure.getCells().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
