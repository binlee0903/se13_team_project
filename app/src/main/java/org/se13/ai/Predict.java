package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.Cell;
import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.se13.ai.PredictUtils.*;

public class Predict implements Prediction {
    private static final Logger log = LoggerFactory.getLogger(Predict.class);

    private final float heightWeight;
    private final float lineWeight;
    private final float holeWeight;
    private final float bumpinessWeight;

    public Predict() {
        this(new Random());
    }

    public Predict(Random random) {
        this(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public Predict(float heightWeight, float lineWeight, float holeWeight, float bumpinessWeight) {
        this.heightWeight = heightWeight;
        this.lineWeight = lineWeight;
        this.holeWeight = holeWeight;
        this.bumpinessWeight = bumpinessWeight;
    }

    @Override
    public List<TetrisAction> predict(TetrisGrid board, CurrentBlock block) {
        int bestPosition = 0;
        int bestRotate = 0;
        float best = Integer.MIN_VALUE;

        for (int rotation = 0; rotation < 4; rotation++) {
            CurrentBlock clone = block.copy();
            for (int i = 0; i < rotation; i++) {
                clone.rotateCW();
            }

            while (shiftLeft(board, clone)) ;

            int moveRight = 0;
            do {
                float score = fitness(board, clone);
                if (score > best) {
                    best = score;
                    bestPosition = moveRight;
                    bestRotate = rotation;
                }
                moveRight++;
            } while (shiftRight(board, clone));
        }

        return createActions(board, block, bestPosition, bestRotate);
    }

    private float fitness(TetrisGrid board, CurrentBlock block) {
        float score = 0;

        TetrisGrid cloneBoard = board.copy();
        CurrentBlock cloneBlock = block.copy();

        while (shiftDown(cloneBoard, cloneBlock)) ;
        setCurrentBlock(cloneBoard, cloneBlock);

        score -= heightWeight * aggregate(cloneBoard);
        score += lineWeight * lines(cloneBoard);
        score -= holeWeight * holes(cloneBoard);
        score -= bumpinessWeight * bumpiness(cloneBoard);

        deleteCurrentBlock(cloneBoard, cloneBlock);

        return score;
    }

    private List<TetrisAction> createActions(TetrisGrid board, CurrentBlock block, int bestPosition, int bestRotate) {
        List<TetrisAction> actions = new ArrayList<>();
        TetrisGrid boardClone = board.copy();
        CurrentBlock blockClone = block.copy();

        for (int i = 0; i < bestRotate; i++) {
            actions.add(TetrisAction.ROTATE_BLOCK_CW);
            blockClone.rotateCW();
        }

        while (shiftLeft(boardClone, blockClone)) {
            actions.add(TetrisAction.MOVE_BLOCK_LEFT);
        }

        for (int i = 0; i < bestPosition; i++) {
            if (actions.contains(TetrisAction.MOVE_BLOCK_LEFT)) {
                actions.remove(TetrisAction.MOVE_BLOCK_LEFT);
            } else {
                actions.add(TetrisAction.MOVE_BLOCK_RIGHT);
            }
            shiftRight(board, blockClone);
        }

        actions.add(TetrisAction.IMMEDIATE_BLOCK_PLACE);

//        while (shiftDown(boardClone, blockClone)) {
//            actions.add(TetrisAction.MOVE_BLOCK_DOWN);
//        }
//        actions.add(TetrisAction.MOVE_BLOCK_DOWN);

        return actions;
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

    private boolean shiftDown(TetrisGrid board, CurrentBlock block) {
        block.move(1, 0);
        if (isBlockCollapse(board, block)) {
            block.move(-1, 0);
            return false;
        }
        return true;
    }

    private void setCurrentBlock(TetrisGrid board, CurrentBlock block) {
        BlockPosition position = block.getPosition();

        try {
            for (Cell cell : block.cells()) {
                BlockPosition p = cell.position();
                int rowIndex = p.getRowIndex() + position.getRowIndex();
                int colIndex = p.getColIndex() + position.getColIndex();
                board.setCell(rowIndex, colIndex, cell.cellID());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("IsBlockCollapse :" + isBlockCollapse(board, block));
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

    public float getHeightWeight() {
        return heightWeight;
    }

    public float getLineWeight() {
        return lineWeight;
    }

    public float getHoleWeight() {
        return holeWeight;
    }

    public float getBumpinessWeight() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predict predict = (Predict) o;
        return Float.compare(heightWeight, predict.heightWeight) == 0 && Float.compare(lineWeight, predict.lineWeight) == 0 && Float.compare(holeWeight, predict.holeWeight) == 0 && Float.compare(bumpinessWeight, predict.bumpinessWeight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(heightWeight, lineWeight, holeWeight, bumpinessWeight);
    }
}
