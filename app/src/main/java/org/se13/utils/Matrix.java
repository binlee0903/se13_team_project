package org.se13.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Matrix {

    private static Random random = new SecureRandom();

    public static float[][] randn(int row, int col) {
        float[][] result = new float[row][col];

        for (int r = 0; r < row; r++) {
            float[] array = new float[col];
            for (int c = 0; c < col; c++) {
                if (random.nextBoolean()) {
                    array[c] = -random.nextFloat();
                } else {
                    array[c] = random.nextFloat();
                }
            }

            result[r] = array;
        }

        return result;
    }

    public static float[][] matmul(float[][] A, float[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        float[][] C = new float[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000f;
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

    public static float[][] relu(float[][] matrix) {
        float[][] result = new float[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            result[i] = new float[matrix[i].length];

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

    public static int argmax(float[] matrix) {
        int max = 0;

        for (int i = 1; i < matrix.length; i++) {
            if (matrix[max] < matrix[i]) {
                max = i;
            }
        }

        return max;
    }

    public static float[][] crossOver(float[][] w1, float[][] w2) {
        assert w1.length == w2.length;

        float[][] cross = new float[w1.length][];
        int length = w1.length;

        for (int i = 0; i < length; i++) {
            assert w1[i].length == w2[i].length;
            float[] over = new float[w1[i].length];

            for (int j = 0; j < w1[i].length; j++) {
                if (random.nextBoolean()) {
                    over[j] = w1[i][j];
                } else {
                    over[j] = w2[i][j];
                }
            }

            cross[i] = over;
        }

        return cross;
    }
}
