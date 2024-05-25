package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.CellID;

import java.util.Arrays;
import java.util.Random;

public class Neural {
    private static final TetrisAction[] AVAILABLE = new TetrisAction[]{TetrisAction.MOVE_BLOCK_LEFT, TetrisAction.MOVE_BLOCK_RIGHT, TetrisAction.IMMEDIATE_BLOCK_PLACE, TetrisAction.ROTATE_BLOCK_CW};
    private static final int ROWS = 22;
    private static final int COLS = 10;
    private static final int STATE_SIZE = ROWS * COLS;
    private static final int ACTION_SIZE = 4;
    private static final int HIDDEN_LAYER_SIZE = 3;
    private static final double MUTATION_RATE = 0.5;
    private static Random random = new Random();

    private final double[][] weightsHiddenInput;
    private final double[][] weightsHiddenLayer;
    private final double[][] weightsHiddenOutput;

    public Neural(double[][] weightsInputHidden, double[][] weightsHiddenLayer, double[][] weightsHiddenOutput) {
        this.weightsHiddenInput = weightsInputHidden;
        this.weightsHiddenOutput = weightsHiddenOutput;
        this.weightsHiddenLayer = weightsHiddenLayer;
    }

    public Neural() {
        double[][] weightsInput = new double[STATE_SIZE][HIDDEN_LAYER_SIZE];
        double[][] weightHidden = new double[HIDDEN_LAYER_SIZE][HIDDEN_LAYER_SIZE];
        double[][] weightsOutput = new double[HIDDEN_LAYER_SIZE][ACTION_SIZE];
        initializeWeights(weightsInput);
        initializeWeights(weightHidden);
        initializeWeights(weightsOutput);

        this.weightsHiddenInput = weightsInput;
        this.weightsHiddenLayer = weightHidden;
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
                state[i][j] = board[i][j].id;
            }
        }

        for (int i = 0; i < state.length; i++) {
            state[i] = normalize(state[i], min, max);
        }

        return predict(state);
    }

    public TetrisAction predict(double[][] state) {
        // 입력 데이터 전처리 및 신경망 연산
        double[] flatState = flatten(state);
        double[] input = new double[HIDDEN_LAYER_SIZE];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            for (int j = 0; j < STATE_SIZE; j++) {
                input[i] += 2 * flatState[j] * weightsHiddenInput[j][i];
            }
        }

        double[] hidden = new double[HIDDEN_LAYER_SIZE];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            for (int j = 0; j < HIDDEN_LAYER_SIZE; j++) {
                hidden[i] += 2 * input[j] * weightsHiddenLayer[j][i];
            }
        }

        double[] output = new double[ACTION_SIZE];
        for (int i = 0; i < ACTION_SIZE; i++) {
            for (int j = 0; j < HIDDEN_LAYER_SIZE; j++) {
                output[i] += hidden[j] * weightsHiddenOutput[j][i];
            }
        }

        output = softmax(output);

        return selectAction(output);
    }

    public Neural crossover(Neural mother) {
        return new Neural(
                crossover(weightsHiddenInput, mother.weightsHiddenInput),
                crossover(weightsHiddenLayer, mother.weightsHiddenLayer),
                crossover(weightsHiddenOutput, mother.weightsHiddenOutput)
        );
    }

    public Neural mutate() {
        return new Neural(
                mutate(this.weightsHiddenInput),
                mutate(this.weightsHiddenLayer),
                mutate(this.weightsHiddenOutput)
        );
    }

    private double[][] crossover(double[][] d1, double[][] d2) {
        double[][] result = new double[d1.length][];

        for (int i = 0; i < d1.length; i++) {
            double[] child = new double[d1[i].length];
            int crossoverPoint = random.nextInt(d1[i].length);

            for (int j = 0; j < d1[i].length; j++) {
                if (i < crossoverPoint) {
                    child[j] = d1[i][j];
                } else {
                    child[j] = d2[i][j];
                }
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
                    o[j] = (random.nextDouble() - 0.5) * 2;
                }
            }

            result[i] = o;
        }

        return result;
    }

    private void initializeWeights(double[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (random.nextDouble() - 0.5) * 2 / Math.sqrt(weights.length);
            }
        }
    }

    private double[] relu(double[] x) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.max(0, x[i]);
        }
        return result;
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

    private TetrisAction selectAction(double[] probabilities) {
        double rand = Math.random();
        double cumulativeProbability = 0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (rand <= cumulativeProbability) {
                return AVAILABLE[i];
            }
        }
        return AVAILABLE[probabilities.length - 1];
    }

    @Override
    public String toString() {
        return "Neural{" +
                "weightsHiddenInput=" + Arrays.deepToString(weightsHiddenInput) +
                ", weightsHiddenOutput=" + Arrays.deepToString(weightsHiddenOutput) +
                '}';
    }
}