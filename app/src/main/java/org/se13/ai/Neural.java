package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.CellID;

import java.util.Arrays;
import java.util.Random;

public class Neural {
    private static final TetrisAction[] AVAILABLE = new TetrisAction[]{
            TetrisAction.MOVE_BLOCK_LEFT, TetrisAction.MOVE_BLOCK_RIGHT, TetrisAction.ROTATE_BLOCK_CW, TetrisAction.MOVE_BLOCK_DOWN, TetrisAction.IMMEDIATE_BLOCK_PLACE};
    private static final int ROWS = 22;
    private static final int COLS = 10;
    private static final int STATE_SIZE = ROWS * COLS;
    private static final int ACTION_SIZE = 5;
    private static final int HIDDEN_LAYER_SIZE1 = 12;
    private static final int HIDDEN_LAYER_SIZE2 = 20;
    private static final int HIDDEN_LAYER_SIZE3 = 8;
    private static final double MUTATION_RATE = 0.05;
    private static Random random = new Random();

    private final double[][] weightsHiddenInput;
    private final double[][] weightsHiddenLayer1;
    private final double[][] weightsHiddenLayer2;
    private final double[][] weightsHiddenOutput;

    public Neural(double[][] weightsInputHidden, double[][] weightsHiddenLayer1, double[][] weightsHiddenLayer2, double[][] weightsHiddenOutput) {
        this.weightsHiddenInput = weightsInputHidden;
        this.weightsHiddenOutput = weightsHiddenOutput;
        this.weightsHiddenLayer1 = weightsHiddenLayer1;
        this.weightsHiddenLayer2 = weightsHiddenLayer2;
    }

    public Neural() {
        double[][] weightsInput = new double[STATE_SIZE][HIDDEN_LAYER_SIZE1];
        double[][] weightHidden1 = new double[HIDDEN_LAYER_SIZE1][HIDDEN_LAYER_SIZE2];
        double[][] weightHidden2 = new double[HIDDEN_LAYER_SIZE2][HIDDEN_LAYER_SIZE3];
        double[][] weightsOutput = new double[HIDDEN_LAYER_SIZE3][ACTION_SIZE];

        initializeWeights(weightsInput);
        initializeWeights(weightHidden1);
        initializeWeights(weightHidden2);
        initializeWeights(weightsOutput);

        this.weightsHiddenInput = weightsInput;
        this.weightsHiddenLayer1 = weightHidden1;
        this.weightsHiddenLayer2 = weightHidden2;
        this.weightsHiddenOutput = weightsOutput;
    }

    public TetrisAction predict(ComputerInput input) {
        CellID[] cellIds = CellID.values();
        int min = min(cellIds);
        int max = max(cellIds);

        double[][] state = new double[ROWS][COLS];
        CellID[][] board = input.tetrisGrid;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                state[i][j] = board[i][j].id > 0 ? 1 : 0;
            }
        }

        return predict(state);
    }

    public TetrisAction predict(double[][] state) {
        // 입력 데이터 전처리 및 신경망 연산
        double[] flatState = flatten(state);

        double[] input = new double[HIDDEN_LAYER_SIZE1];
        matmul(input, flatState, weightsHiddenInput);
        relu(input);

        double[] hidden1 = new double[HIDDEN_LAYER_SIZE2];
        matmul(hidden1, input, weightsHiddenLayer1);
        relu(hidden1);

        double[] hidden2 = new double[HIDDEN_LAYER_SIZE3];
        matmul(hidden2, hidden1, weightsHiddenLayer2);
        relu(hidden2);

        double[] output = new double[ACTION_SIZE];
        matmul(output, hidden2, weightsHiddenOutput);
        relu(output);

        output = softmax(output);

        return AVAILABLE[argMax(output)];
    }

    private void matmul(double[] des, double[] src, double[][] weights) {
        for (int i = 0; i < des.length; i++) {
            for (int j = 0; j < src.length; j++) {
                des[i] += src[j] * weights[j][i];
            }
        }
    }

    public Neural crossover(Neural mother) {
        return new Neural(
                crossover(weightsHiddenInput, mother.weightsHiddenInput),
                crossover(weightsHiddenLayer1, mother.weightsHiddenLayer1),
                crossover(weightsHiddenLayer2, mother.weightsHiddenLayer2),
                crossover(weightsHiddenOutput, mother.weightsHiddenOutput)
        );
    }

    public Neural mutate() {
        return new Neural(
                mutate(this.weightsHiddenInput),
                mutate(this.weightsHiddenLayer1),
                mutate(this.weightsHiddenLayer2),
                mutate(this.weightsHiddenOutput)
        );
    }

    private double[][] crossover(double[][] d1, double[][] d2) {
        double[][] result = new double[d1.length][];

        for (int i = 0; i < d1.length; i++) {
            double[] child = new double[d1[i].length];

            for (int j = 0; j < d1[i].length; j++) {
                child[j] = (d1[i][j] + d2[i][j]) * random.nextDouble();
            }

            result[i] = child;
        }

        return result;
    }

    private double[][] mutate(double[][] original) {
        double[][] result = new double[original.length][];

        for (int i = 0; i < original.length; i++) {
            double[] o = new double[original[i].length];

            for (int j = 0; j < o.length; j++) {
                if (random.nextDouble() < MUTATION_RATE) {
                    o[j] = original[i][j];
                } else {
                    double gaussian = (random.nextGaussian() - 0.5) * 2;
                    o[j] = original[i][j] * gaussian;
                }
            }

            result[i] = o;
        }

        return result;
    }

    private void initializeWeights(double[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (random.nextDouble() - 0.5) * 2;
            }
        }
    }

    private void relu(double[] x) {
        for (int i = 0; i < x.length; i++) {
            x[i] = Math.max(0, x[i]);
        }
    }

    private double[] flatten(double[][] state) {
        double[] flatState = new double[STATE_SIZE];
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                flatState[index++] = state[i][j];
            }
        }
        return flatState;
    }

    private int argMax(double[] values) {
        int bestIndex = 0;
        double bestValue = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > bestValue) {
                bestValue = values[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private int min(CellID[] array) {
        int min = array[0].id;
        for (int i = 1; i < array.length; i++) {
            min = Math.min(min, array[i].id);
        }

        return min;
    }

    private int max(CellID[] array) {
        int max = array[0].id;
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i].id);
        }

        return max;
    }

    private double[] normalize(double[] array, int min, int max) {
        double[] normalized = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            normalized[i] = (array[i] - min) / (max - min);
            normalized[i] = (normalized[i] - 0.5) * 2;
        }

        return normalized;
    }

    private double[] softmax(double[] x) {
        double[] result = new double[x.length];
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i]);
            sum += result[i];
        }
        for (int i = 0; i < x.length; i++) {
            result[i] /= sum;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Neural{" +
                "weightsHiddenInput=" + Arrays.deepToString(weightsHiddenInput) +
                ", weightsHiddenOutput=" + Arrays.deepToString(weightsHiddenOutput) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neural neural = (Neural) o;
        return Arrays.deepEquals(weightsHiddenInput, neural.weightsHiddenInput) &&
                Arrays.deepEquals(weightsHiddenLayer1, neural.weightsHiddenLayer1) &&
                Arrays.deepEquals(weightsHiddenLayer2, neural.weightsHiddenLayer2) &&
                Arrays.deepEquals(weightsHiddenOutput, neural.weightsHiddenOutput);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(weightsHiddenInput);
        result = 31 * result + Arrays.deepHashCode(weightsHiddenLayer1);
        result = 31 * result + Arrays.deepHashCode(weightsHiddenLayer2);
        result = 31 * result + Arrays.deepHashCode(weightsHiddenOutput);
        return result;
    }
}