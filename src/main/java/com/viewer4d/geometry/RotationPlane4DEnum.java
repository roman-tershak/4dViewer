package com.viewer4d.geometry;

public enum RotationPlane4DEnum {

    XY( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[0][0] = cos;
            matrix[0][1] = -sin;
            matrix[1][0] = sin;
            matrix[1][1] = cos;
        }
    } ),

    XZ( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[0][0] = cos;
            matrix[0][2] = -sin;
            matrix[2][0] = sin;
            matrix[2][2] = cos;
        }
    } ),

    YZ( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[1][1] = cos;
            matrix[1][2] = -sin;
            matrix[2][1] = sin;
            matrix[2][2] = cos;
        }
    } ),

    XW( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[0][0] = cos;
            matrix[0][3] = -sin;
            matrix[3][0] = sin;
            matrix[3][3] = cos;
        }
    } ),

    YW( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[1][1] = cos;
            matrix[1][3] = -sin;
            matrix[3][1] = sin;
            matrix[3][3] = cos;
        }
    } ),

    ZW( new RotationMatrixCalculator() {
        @Override
        protected void fill() {
            matrix[2][2] = cos;
            matrix[2][3] = -sin;
            matrix[3][2] = sin;
            matrix[3][3] = cos;
        }
    } );

    // Internal types
    private static abstract class RotationMatrixCalculator {

        protected final double[][] matrix = new double[][] {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        protected double cos;
        protected double sin;

        protected final double[][] calculate(double radians) {
            cos = Math.cos(radians);
            sin = Math.sin(radians);
            fill();
            return matrix;
        }

        protected abstract void fill();
    }

    // Instance members
    private RotationMatrixCalculator rotationMatrixCalculator;

    private RotationPlane4DEnum(RotationMatrixCalculator calculator) {
        this.rotationMatrixCalculator = calculator;
    }

    public double[][] getRotationMatrix(double radians) {
        return rotationMatrixCalculator.calculate(radians);
    }
}
