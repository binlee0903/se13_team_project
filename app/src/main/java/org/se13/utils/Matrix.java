package org.se13.utils;

import java.util.Random;

public class Matrix {

    public static double[][] randn(int row, int col) {
        Random random = new Random();

        double[][] result = new double[row][col];

        for (int r = 0; r < row; r++) {
            double[] array = new double[col];
            for (int c = 0; c < col; c++) {
                if (random.nextBoolean()) {
                    array[c] = -random.nextDouble();
                } else {
                    array[c] = random.nextDouble();
                }
            }

            result[r] = array;
        }

        return result;
    }

    public static double[][] matmul(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                for (int k = 0; k < aColumns; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    public static double[][] relu(double[][] matrix) {
        double[][] result = new double[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            result[i] = new double[matrix[i].length];

            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] > 0) {
                    result[i][j] = matrix[i][j];
                } else {
                    result[i][j] = 0;
                }
            }
        }

        return result;
    }

    public static int argmax(double[] matrix) {
        int max = 0;

        for (int i = 1; i < matrix.length; i++) {
            if (matrix[max] < matrix[i]) {
                max = i;
            }
        }

        return max;
    }
}
