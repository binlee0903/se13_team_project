package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.Cell;
import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.se13.ai.PredictUtils.*;

public class Predict implements Prediction {

    private double heightWeight;
    private double lineWeight;
    private double holeWeight;
    private double bumpinessWeight;

    public Predict() {
        this(new Random());
    }

    public Predict(Random random) {
        this(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public Predict(double heightWeight, double lineWeight, double holeWeight, double bumpinessWeight) {
        this.heightWeight = heightWeight;
        this.lineWeight = lineWeight;
        this.holeWeight = holeWeight;
        this.bumpinessWeight = bumpinessWeight;
    }

    @Override
    public List<TetrisAction> predict(TetrisGrid board, CurrentBlock block) {
        int bestPosition = 0;
        int bestRotate = 0;
        double best = Integer.MIN_VALUE;

        for (int rotation = 0; rotation < 4; rotation++) {
            CurrentBlock clone = block.copy();
            for (int i = 0; i < rotation; i++) {
                clone.rotateCW();
            }

            while (shiftLeft(board, clone)) ;

            int moveRight = 0;
            do {
                double score = fitness(board, clone);
                if (score > best) {
                    best = score;
                    bestPosition = moveRight;
                    bestRotate = rotation;
                }
                moveRight++;
            } while (shiftRight(board, clone));

        }

        ArrayList<TetrisAction> bestMove = new ArrayList<>();

        CurrentBlock bestBlock = block.copy();
        while (shiftLeft(board, bestBlock)) {
            bestMove.add(TetrisAction.MOVE_BLOCK_LEFT);
        }

        for (int i = 0; i < bestPosition; i++) {
            if (bestMove.contains(TetrisAction.MOVE_BLOCK_LEFT)) {
                bestMove.removeFirst();
            } else {
                bestMove.add(TetrisAction.MOVE_BLOCK_RIGHT);
            }
        }

        for (int i = 0; i < bestRotate; i++) {
            bestMove.add(TetrisAction.ROTATE_BLOCK_CW);
        }

        for (int i = 0; i < shiftDown(board, bestBlock); i++) {
            bestMove.add(TetrisAction.MOVE_BLOCK_DOWN);
        }

        return bestMove;
    }

    private double fitness(TetrisGrid board, CurrentBlock block) {
        double score = 0;

        CurrentBlock attempt = block.copy();

        shiftDown(board, attempt);
        setCurrentBlock(board, block);

        score -= heightWeight * aggregate(board);
        score += lineWeight * lines(board);
        score -= holeWeight * holes(board);
        score -= bumpinessWeight * bumpiness(board);

        deleteCurrentBlock(board, block);

        return score;
    }

    private boolean shiftLeft(TetrisGrid board, CurrentBlock block) {
        block.move(0, -1);
        if (isBlockCollapse(board, block)) {
            block.move(0, 1);
            return false;
        }

        return true;
    }

    private boolean shiftRight(TetrisGrid board, CurrentBlock block) {
        block.move(0, 1);
        if (isBlockCollapse(board, block)) {
            block.move(0, -1);
            return false;
        }

        return true;
    }

    private int shiftDown(TetrisGrid board, CurrentBlock block) {
        int count = 0;
        for (int i = block.getPosition().getRowIndex(); i < 22; i++) {
            block.move(1, 0);
            if (isBlockCollapse(board, block)) {
                block.move(-1, 0);
                return count;
            }
            count++;
        }

        throw new RuntimeException("도달할 수 없는 코드입니다." );
    }

    private void setCurrentBlock(TetrisGrid board, CurrentBlock block) {
        BlockPosition position = block.getPosition();

        for (Cell cell : block.cells()) {
            BlockPosition p = cell.position();
            int rowIndex = p.getRowIndex() + position.getRowIndex();
            int colIndex = p.getColIndex() + position.getColIndex();
            board.setCell(rowIndex, colIndex, cell.cellID());
        }
    }

    private void deleteCurrentBlock(TetrisGrid board, CurrentBlock block) {
        BlockPosition position = block.getPosition();

        for (BlockPosition p : block.shape()) {
            int rowIndex = p.getRowIndex() + position.getRowIndex();
            int colIndex = p.getColIndex() + position.getColIndex();
            board.setCell(rowIndex, colIndex, CellID.EMPTY);
        }
    }

    private boolean isBlockCollapse(TetrisGrid board, CurrentBlock block) {
        for (BlockPosition p : block.shape()) {
            int blockRowIndex = p.getRowIndex() + block.getPosition().getRowIndex();
            int blockColIndex = p.getColIndex() + block.getPosition().getColIndex();

            if (!board.isEmptyCell(blockRowIndex, blockColIndex)) {
                return true;
            }

            if (!board.isInsideGrid(blockRowIndex, blockColIndex)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predict predict = (Predict) o;
        return Double.compare(predict.heightWeight, heightWeight) == 0 &&
                Double.compare(predict.lineWeight, lineWeight) == 0 &&
                Double.compare(predict.holeWeight, holeWeight) == 0 &&
                Double.compare(predict.bumpinessWeight, bumpinessWeight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(heightWeight, lineWeight, holeWeight, bumpinessWeight);
    }

    public double getHeightWeight() {
        return heightWeight;
    }

    public double getLineWeight() {
        return lineWeight;
    }

    public double getHoleWeight() {
        return holeWeight;
    }

    public double getBumpinessWeight() {
        return bumpinessWeight;
    }

    @Override
    public String toString() {
        return "Predict{" +
                "heightWeight=" + heightWeight +
                ", lineWeight=" + lineWeight +
                ", holeWeight=" + holeWeight +
                ", bumpinessWeight=" + bumpinessWeight +
                '}';
    }
}
