package com.viewer4d.view;

import static com.viewer4d.geometry.Selection.NOTSELECTED;
import static com.viewer4d.geometry.Selection.SELECTED1;
import static com.viewer4d.geometry.Selection.SELECTED2;
import static com.viewer4d.geometry.Selection.SELECTED3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.viewer4d.geometry.Cell;
import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Face;
import com.viewer4d.geometry.Figure;
import com.viewer4d.geometry.Vertex;

public class FigureCellSelector {

    private volatile boolean selectModeOn;
    
    private List<Cell> cellsList;
    private int cellsListSize;
    
    private Cell selectedCell;
    private int selectedCellNum;
    
    private ListIterator<Cell> cellSiblingIterator;
    private Cell selectedSiblingCell;
    
    private final Set<Cell> selectedCellSet = new HashSet<Cell>();
    private final Set<Cell> siblingsCellSet = new HashSet<Cell>();
    
    private boolean siblingCellsSelected;
    
    private boolean needRepaint;

    
    public FigureCellSelector(Figure figure, boolean selectModeOn) {
        this.selectModeOn = selectModeOn;
        
        init(figure);
        selectFirstCell();
    }

    private void init(Figure figure) {
        List<Cell> figureCells = new ArrayList<Cell>(figure.getCells());
        
        Collections.sort(figureCells, new Comparator<Cell>() {

            private Map<Cell, Double> avgWByCells = new HashMap<Cell, Double>();
            
            @Override
            public int compare(Cell c1, Cell c2) {
                double w1 = getAvgWFor(c1);
                double w2 = getAvgWFor(c2);
                
                if (w1 < w2) {
                    return 1;
                } else if (w1 > w2) {
                    return -1;
                } else {
                    return 0;
                }
            }

            private double getAvgWFor(Cell cell) {
                Double avgW = avgWByCells.get(cell);
                if (avgW == null) {
                    
                    Set<Vertex> cellVertices = new HashSet<Vertex>();
                    for (Face face : cell.getFaces()) {
                        for (Edge edge : face.getEdges()) {
                            cellVertices.add(edge.getA());
                            cellVertices.add(edge.getB());
                        }
                    }
                    
                    double res = 0;
                    int count = 0;
                    for (Vertex vertex : cellVertices) {
                        res += vertex.getCoords()[3];
                        count++;
                    }
                    
                    avgW = new Double(res / count);
                    avgWByCells.put(cell, avgW);
                }
                return avgW.doubleValue();
            }
            
        });
        
        cellsList = figureCells;
        cellsListSize = figureCells.size();
        selectedCellSet.clear();
    }

    public void setFigure(Figure figure) {
        selectCell(false);
        selectSiblingCell(false);
        selectSelectedCells(false);
        
        init(figure);
        selectFirstCell();
    }
    
    private void selectFirstCell() {
        if (cellsListSize > 0) {
            selectedCellNum = 0;
            selectedCell = cellsList.get(selectedCellNum);
            cellSiblingIterator = getSiblingCellIterator();
            siblingsCellSet.clear();
            siblingCellsSelected = false;
        } else {
            selectedCellNum = -1;
            selectedCell = null;
            cellSiblingIterator = null;
        }
        if (selectModeOn) {
            selectCell(true);
        }
    }

    private ListIterator<Cell> getSiblingCellIterator() {
        return new LinkedList<Cell>(selectedCell.getSiblings()).listIterator();
    }

    public boolean isSelectModeOn() {
        return selectModeOn;
    }
    
    public void setSelectMode(boolean selectModeOn) {
        if (selectModeOn) {
            setSelectModeOn();
        } else {
            setSelectModeOff();
        }
    }
    
    private void setSelectModeOn() {
        if (!selectModeOn) {
            selectModeOn = true;
            
            selectSelectedCells(true);
            selectSiblingCells(true);
            selectCell(true);
            selectSiblingCell(true);
        }
    }
    
    private void setSelectModeOff() {
        if (selectModeOn) {
            selectModeOn = false;
            
            selectSelectedCells(false);
            selectSiblingCells(false);
            selectCell(false);
            selectSiblingCell(false);
        }
    }
    
    public void selectNextCell() {
        if (selectModeOn) {
            selectCell(false);
            selectSiblingCell(false);
            selectSiblingCells(false);
            
            selectedCell = getNextCell();
            cellSiblingIterator = getSiblingCellIterator();
            selectedSiblingCell = null;
            siblingsCellSet.clear();
            siblingCellsSelected = false;
            
            selectSelectedCells(true);
            selectCell(true);
        }
    }

    public void selectPrevCell() {
        if (selectModeOn) {
            selectCell(false);
            selectSiblingCell(false);
            selectSiblingCells(false);
            
            selectedCell = getPrevCell();
            cellSiblingIterator = getSiblingCellIterator();
            selectedSiblingCell = null;
            siblingsCellSet.clear();
            siblingCellsSelected = false;
            
            selectSelectedCells(true);
            selectCell(true);
        }
    }
    
    public void selectNextSiblingCell() {
        if (selectModeOn) {
            selectSiblingCell(false);
            selectSiblingCells(false);
            
            siblingsCellSet.clear();
            siblingCellsSelected = false;
            selectedSiblingCell = getNextSiblingCell();
            
            selectSelectedCells(true);
            selectCell(true);
            selectSiblingCell(true);
        }
    }

    public void selectPrevSiblingCell() {
        if (selectModeOn) {
            selectSiblingCell(false);
            selectSiblingCells(false);
            
            siblingsCellSet.clear();
            siblingCellsSelected = false;
            selectedSiblingCell = getPrevSiblingCell();
            
            selectSelectedCells(true);
            selectCell(true);
            selectSiblingCell(true);
        }
    }
    
    public void toggleSiblingCells() {
        if (!siblingCellsSelected) {
            selectSiblingCells();
        } else {
            unselectSiblingCells();
        }
    }
    
    public void lockUnlockSelectedCell() {
        if (selectModeOn && selectedCell != null) {
            if (selectedCellSet.contains(selectedCell)) {
                selectedCellSet.remove(selectedCell);
            } else {
                selectedCellSet.add(selectedCell);
            }
        }
    }
    
    public void clearLockedSelectedCells() {
        if (selectModeOn) {
            selectSelectedCells(false);
            selectedCellSet.clear();
            
            selectSiblingCells(false);
            siblingsCellSet.clear();
            siblingCellsSelected = false;
        
            selectCell(true);
            selectSiblingCell(true);
        }
    }
    
    public boolean isRepaintNeeded() {
        return needRepaint;
    }

    public void resetSelectedCell() {
        selectCell(false);
        selectSiblingCell(false);
        selectSelectedCells(false);
        selectSiblingCells(false);
        
        selectedCellSet.clear();
        siblingsCellSet.clear();
        siblingCellsSelected = false;
        
        selectFirstCell();
    }
    
    private void selectCell(boolean select) {
        needRepaint = false;
        if (selectedCell != null) {
            selectedCell.setSelection(select ? SELECTED1 : NOTSELECTED);
            needRepaint = true;
        }
    }
    
    private void selectSiblingCell(boolean select) {
        if (selectedSiblingCell != null) {
            selectedSiblingCell.setSelection(select ? SELECTED2 : NOTSELECTED);
            needRepaint = true;
        }
    }

    private void selectSiblingCells() {
        if (selectModeOn && selectedCell != null) {
            siblingsCellSet.addAll(selectedCell.getSiblings());
            siblingCellsSelected = true;
            
            selectSelectedCells(true);
            selectSiblingCells(true);
            selectCell(true);
        }
    }

    private void unselectSiblingCells() {
        if (selectModeOn) {
            selectSiblingCells(false);
            siblingsCellSet.clear();
            siblingCellsSelected = false;
            
            selectSelectedCells(true);
            selectCell(true);
            selectSiblingCell(true);
        }
    }

    private void selectSiblingCells(boolean select) {
        for (Cell cell : siblingsCellSet) {
            cell.setSelection(select ? SELECTED2 : NOTSELECTED);
            needRepaint = true;
        }
    }

    private void selectSelectedCells(boolean select) {
        for (Cell cell : selectedCellSet) {
            cell.setSelection(select ? SELECTED3 : NOTSELECTED);
            needRepaint = true;
        }
    }
    
    private Cell getNextCell() {
        if (selectedCellNum != -1) {
            selectedCellNum++;
            if (selectedCellNum >= cellsListSize) {
                selectedCellNum = 0;
            }
            return cellsList.get(selectedCellNum);
        } else {
            return null;
        }
    }

    private Cell getPrevCell() {
        if (selectedCellNum != -1) {
            selectedCellNum--;
            if (selectedCellNum < 0) {
                selectedCellNum = cellsListSize - 1;
            }
            return cellsList.get(selectedCellNum);
        } else {
            return null;
        }
    }
    
    private Cell getNextSiblingCell() {
        if (cellSiblingIterator != null && cellSiblingIterator.hasNext()) {
            return cellSiblingIterator.next();
        }
        return null;
    }

    private Cell getPrevSiblingCell() {
        if (cellSiblingIterator != null && cellSiblingIterator.hasPrevious()) {
            return cellSiblingIterator.previous();
        }
        return null;
    }

}
