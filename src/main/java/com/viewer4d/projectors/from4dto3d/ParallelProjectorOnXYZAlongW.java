package com.viewer4d.projectors.from4dto3d;

import com.viewer4d.geometry.Vertex;
import com.viewer4d.projectors.AbstractProjectingProjector;

public class ParallelProjectorOnXYZAlongW extends AbstractProjectingProjector {

    public ParallelProjectorOnXYZAlongW() {
    }

    @Override
    protected Vertex projectVertex(Vertex vertex) {
        return new Vertex(vertex.getCoords());
    }
}
