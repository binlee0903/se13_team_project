package org.se13.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MatrixTest {

    @Test
    void randnTest() {
        double[][] matrix = Matrix.randn(3, 2);

        Assertions.assertEquals(3, matrix.length);
        Assertions.assertEquals(2, matrix[0].length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Assertions.assertTrue(matrix[i][j] > -1);
                Assertions.assertTrue(matrix[i][j] < 1);
            }
        }
    }

    @Test
    void matmulTest() {
        double[][] a = new double[][]{
            new double[]{1, 2},
            new double[]{3, 4},
        };
        double[][] b = new double[][]{
            new double[]{5, 6, 7},
            new double[]{8, 9, 10},
        };
        double[][] c = new double[][]{
            new double[]{21, 24, 27},
            new double[]{47, 54, 61},
        };

        double[][] result = Matrix.matmul(a, b);

        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[i].length; j++) {
                Assertions.assertEquals(c[i][j], result[i][j]);
            }
        }
    }

    @Test
    void reluTest() {
        double[][] a = new double[][]{
            new double[]{-0.5, 0.5},
            new double[]{0.5, -0.5},
        };

        double[][] result = Matrix.relu(a);

        Assertions.assertEquals(result[0][0], 0);
        Assertions.assertEquals(result[0][1], 0.5);
        Assertions.assertEquals(result[1][0], 0.5);
        Assertions.assertEquals(result[1][1], 0);
    }

    @Test
    void argmaxTest() {
        double[] a = new double[]{1, 3, 7, 4};
        Assertions.assertEquals(2, Matrix.argmax(a));
    }
}