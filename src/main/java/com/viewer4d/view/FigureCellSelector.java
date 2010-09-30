package com.viewer4d.view;

import static com.viewer4d.geometry.Selection.NOTSELECTED;
import static com.viewer4d.geometry.Selection.SELECTED1;
import static com.viewer4d.geometry.Selection.SELECTED2;

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

    private final Figure figure;
    private volatile boolean selectModeOn;
    
    private final List<Cell> cellsList = new ArrayList<Cell>();
    private int cellsListSize;
    
    private Cell selectedCell;
    private int selectedCellNum;
    
    private ListIterator<Cell> cellSiblingIterator;
    private Cell selectedSiblingCell;
    
    private boolean needRepaint;

    
    public FigureCellSelector(Figure figure, boolean selectModeOn) {
        this.figure = figure;
        this.selectModeOn = selectModeOn;
        
        init(figure);
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
                    return -1;
                } else if (w1 > w2) {
                    return 1;
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
        
        cellsList.addAll(figureCells);
        cellsListSize = figureCells.size();
        
        if (cellsListSize > 0) {
            selectedCellNum = 0;
            selectedCell = cellsList.get(selectedCellNum);
            cellSiblingIterator = getSiblingCellIterator();
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
            selectCell(true);
            selectSiblingCell(true);
        }
    }
    
    private void setSelectModeOff() {
        if (selectModeOn) {
            selectModeOn = false;
            selectCell(false);
            selectSiblingCell(false);
        }
    }
    
    public void selectNextCell() {
        if (selectModeOn) {
            selectCell(false);
            selectSiblingCell(false);
            selectedCell = getNextCell();
            cellSiblingIterator = getSiblingCellIterator();
            selectCell(true);
        }
    }

    public void selectPrevCell() {
        if (selectModeOn) {
            selectCell(false);
            selectSiblingCell(false);
            selectedCell = getPrevCell();
            cellSiblingIterator = getSiblingCellIterator();
            selectCell(true);
        }
    }
    
    public void selectNextSiblingCell() {
        if (selectModeOn) {
            selectSiblingCell(false);
            selectedSiblingCell = getNextSiblingCell();
            selectCell(true);
            selectSiblingCell(true);
        }
    }

    public void selectPrevSiblingCell() {
        if (selectModeOn) {
            selectSiblingCell(false);
            selectedSiblingCell = getPrevSiblingCell();
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
        init(figure);
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
        if (cellSiblingIterator != null) {
            if (cellSiblingIterator.hasNext()) {
                return cellSiblingIterator.next();
            }
        }
        return null;
    }

    private Cell getPrevSiblingCell() {
        if (cellSiblingIterator != null) {
            if (cellSiblingIterator.hasPrevious()) {
                return cellSiblingIterator.previous();
            }
        }
        return null;
    }
    
}
