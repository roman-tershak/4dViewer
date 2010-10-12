package com.viewer4d.geometry;


public interface Transformer {

    public double[] transform(double[] coords);

    public double[] backwardTransform(double[] coords);
}
