package com.viewer4d.view;

import com.viewer4d.geometry.figure.Figure;

public class TwoChannelMonoViewer extends StereoscopicViewer implements TwoChannelViewer {

    public TwoChannelMonoViewer(boolean colored) {
        super(colored);
    }

    public TwoChannelMonoViewer() {
        this(true);
    }

    @Override
    protected void initProjectors(double distance, double eyeDist, double fov) {
        super.initProjectors(distance, 0, fov);
    }
    
    @Override
    public void doProjection(Figure figure) {
        throw new UnsupportedOperationException("This method is not supported in this class.");
    }
    
    @Override
    public void doProjection(Figure main, Figure alternative) {
        doProjectionRight(main);
        doProjectionLeft(alternative);
    }

}
