package com.viewer4d.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.viewer4d.geometry.Cell;
import com.viewer4d.geometry.Figure;

public class FigureCellSelector {

    private static final int LIST_SIZE_LIMIT_MAX = 1000;
    
    private final Figure figure;
    private volatile boolean selectModeOn;
    
    private final LinkedList<Cell> cellsList = new LinkedList<Cell>();
    private int listSizeLimit;
    private ListIterator<Cell> cellIterator;
    private Cell selectedCell;
    private ListIterator<Cell> cellSiblingIterator;
    private Cell selectedSiblingCell;
    
    private boolean needRepaint;

    private int figureCellsListSize;

    
    public FigureCellSelector(Figure figure, boolean selectModeOn) {
        this.figure = figure;
        this.selectModeOn = selectModeOn;
        
        init(figure);
    }

    private void init(Figure figure) {
        Collection<Cell> figureCells = figure.getCells();
        if (!figureCells.isEmpty()) {
            cellsList.add(figureCells.iterator().next());
        }
        figureCellsListSize = figureCells.size();
        listSizeLimit = figureCellsListSize > LIST_SIZE_LIMIT_MAX ? LIST_SIZE_LIMIT_MAX : figureCellsListSize;
        cellIterator = cellsList.listIterator();
        if (cellIterator.hasNext()) {
            selectedCell = cellIterator.next();
            cellSiblingIterator = getSiblingCellIterator();
        } else {
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
            Cell prevCell = getPrevCell();
            if (selectedCell == prevCell) {
                prevCell = getPrevCell();
            }
            selectedCell = prevCell;
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
            selectedCell.setSelected(select);
            needRepaint = true;
        }
    }
    
    private void selectSiblingCell(boolean select) {
        if (selectedSiblingCell != null) {
            selectedSiblingCell.setSelected(select);
            needRepaint = true;
        }
    }

    private Cell getNextCell() {
        Cell cell = selectedCell;
        for (int i = 0; cell != null && cell == selectedCell && i < 2; i++) {
            if (cellIterator.hasNext()) {
                cell = cellIterator.next();
            } else {
                if (cellsList.size() == figureCellsListSize) {
                    cellIterator = cellsList.listIterator();
                    cell = cellIterator.next();
                } else {
                    cell = getNextSiblingCell(selectedCell, cellsList);
                    if (cell != null) {
                        cellsList.addLast(cell);
                        if (cellsList.size() > listSizeLimit) {
                            cellsList.removeFirst();
                        }
                        cellIterator = cellsList.listIterator(cellsList.size());
                    }
                }
            }
        }
        return cell;
    }

    private Cell getPrevCell() {
        Cell cell = selectedCell;
        for (int i = 0; cell != null && cell == selectedCell && i < 2; i++) {
            if (cellIterator.hasPrevious()) {
                cell = cellIterator.previous();
            } else {
                if (cellsList.size() == figureCellsListSize) {
                    cellIterator = cellsList.listIterator(cellsList.size());
                    cell = cellIterator.previous();
                } else {
                    cell = getNextSiblingCell(selectedCell, cellsList);
                    if (cell != null) {
                        cellsList.addFirst(cell);
                        if (cellsList.size() > listSizeLimit) {
                            cellsList.removeLast();
                        }
                        cellIterator = cellsList.listIterator();
                    }
                }
            }
        }
        return cell;
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
    
    private static Cell getNextSiblingCell(Cell cell, Collection<Cell> alreadyInListCells) {
        Cell result = null;
        Iterator<Cell> it = cell.getSiblings().iterator();
        while (result == null || alreadyInListCells.contains(result)) {
            if (it.hasNext()) {
                result = it.next();
            } else {
                break;
            }
        }
        return result;
    }
}
