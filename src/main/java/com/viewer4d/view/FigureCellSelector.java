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
        } else {
            selectedCell = null;
        }
        if (selectModeOn) {
            selectCell();
        }
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
            selectCell();
        }
    }
    
    private void setSelectModeOff() {
        if (selectModeOn) {
            selectModeOn = false;
            deselectCell();
        }
    }
    
    public void selectNextCell() {
        if (selectModeOn) {
            deselectCell();
            selectedCell = getNextCell();
            selectCell();
        }
    }

    public void selectPrevCell() {
        if (selectModeOn) {
            deselectCell();
            Cell prevCell = getPrevCell();
            if (selectedCell == prevCell) {
                prevCell = getPrevCell();
            }
            selectedCell = prevCell;
            selectCell();
        }
    }
    
    public boolean isRepaintNeeded() {
        return needRepaint;
    }

    public void resetSelectedCell() {
        deselectCell();
        init(figure);
    }
    
    private void deselectCell() {
        needRepaint = false;
        if (selectedCell != null) {
            selectedCell.setSelected(false);
            needRepaint = true;
        }
    }
    
    private void selectCell() {
        needRepaint = false;
        if (selectedCell != null) {
            selectedCell.setSelected(true);
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
