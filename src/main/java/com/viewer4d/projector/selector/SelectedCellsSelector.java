package com.viewer4d.projector.selector;

import static com.viewer4d.geometry.Selection.NOTSELECTED;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Selection;

public class SelectedCellsSelector extends AbstractEnablingSelector {

    public SelectedCellsSelector() {
        super(true);
    }
    
    @Override
    public void setNotCuttingSelected(boolean notCuttingSelected) {
        super.setNotCuttingSelected(true);
    }
    
    @Override
    protected Edge projectEdge(Edge edge) {
        Selection edgeSelection = edge.getSelection();
        if (edgeSelection != null && edgeSelection != NOTSELECTED) {
            return edge;
        } else {
            return null;
        }
    }

}
