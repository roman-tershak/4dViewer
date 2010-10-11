package com.viewer4d.view;

import com.viewer4d.geometry.figure.Figure;

public interface TwoChannelViewer extends Viewer {

    public void doProjection(Figure main, Figure alternative);
}
