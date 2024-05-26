package org.se13.ai;

import org.se13.game.grid.TetrisGrid;

public class PredictUtils {

    public static int aggregate(TetrisGrid board) {
        int count = 0;
        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 22; row++) {
                if (!board.isEmptyCell(row, col)) {
                    count += 22 - row;
                    break;
                }
            }
        }

        return count;
    }

    public static int lines(TetrisGrid board) {
        int count = 0;
        for (int row = 0; row < 22; row++) {
            if (board.isRowFull(row)) {
                count++;
            }
        }

        return count;
    }

    public static int holes(TetrisGrid board) {
        int count = 0;

        for (int col = 0; col < 10; col++) {
            boolean isBlockPlaced = false;
            for (int row = 0; row < 22; row++) {
                if (!isBlockPlaced && !board.isEmptyCell(row, col)) {
                    isBlockPlaced = true;
                }

                if (isBlockPlaced && board.isEmptyCell(row, col)) {
                    count++;
                }
            }
        }

        return count;
    }

    public static int bumpiness(TetrisGrid board) {
        int count = 0;
        int forward = -1;

        for (int col = 0; col < 10; col++) {
            int top = 0;
            for (int row = 0; row < 22; row++) {
                if (!board.isEmptyCell(row, col)) {
                    top = 22 - row;
                    break;
                }
            }
            if (forward != -1) {
                count += Math.abs(forward - top);
            }
            forward = top;
        }

        return count;
    }

    public static Predict normalize(Predict predict) {
        float normalized = (float) Math.sqrt(
            predict.getHeightWeight() * predict.getHeightWeight() +
                predict.getLineWeight() * predict.getLineWeight() +
                predict.getHoleWeight() * predict.getHoleWeight() +
                predict.getBumpinessWeight() * predict.getBumpinessWeight()
        );

        return new Predict(
            predict.getHeightWeight() / normalized,
            predict.getLineWeight() / normalized,
            predict.getHoleWeight() / normalized,
            predict.getBumpinessWeight() / normalized
        );
    }
}
