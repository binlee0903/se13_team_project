package org.se13.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MatrixTest {

    @Test
    void randnTest() {
        float[][] matrix = Matrix.randn(3, 2);

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
        float[][] a = new float[][]{
            new float[]{1, 2},
            new float[]{3, 4},
        };
        float[][] b = new float[][]{
            new float[]{5, 6, 7},
            new float[]{8, 9, 10},
        };
        float[][] c = new float[][]{
            new float[]{21, 24, 27},
            new float[]{47, 54, 61},
        };

        float[][] result = Matrix.matmul(a, b);

        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[i].length; j++) {
                Assertions.assertEquals(c[i][j], result[i][j]);
            }
        }
    }

    @Test
    void reluTest() {
        float[][] a = new float[][]{
            new float[]{-0.5f, 0.5f},
            new float[]{0.5f, -0.5f},
        };

        float[][] result = Matrix.relu(a);

        Assertions.assertEquals(result[0][0], 0f);
        Assertions.assertEquals(result[0][1], 0.5f);
        Assertions.assertEquals(result[1][0], 0.5f);
        Assertions.assertEquals(result[1][1], 0f);
    }

    @Test
    void crossOverTest() {
        float[][] w1 = Matrix.randn(3, 2);
        float[][] w2 = Matrix.randn(3, 2);
        float[][] result = Matrix.crossOver(w1, w2);

        Assertions.assertFalse(Arrays.deepEquals(w1, result));
        Assertions.assertFalse(Arrays.deepEquals(w2, result));
    }

    @Test
    void argmaxTest() {
        float[] a = new float[]{1, 3, 7, 4};
        Assertions.assertEquals(2, Matrix.argmax(a));
    }
}