package org.se13.game;

import org.se13.game.block.BlockPosition;
import org.se13.game.block.CurrentBlock;
import org.se13.game.config.GameConfig;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.rule.BlockQueue;

public class GameStatus {
    private CurrentBlock currentBlock;
    private final TetrisGrid tetrisGrid;
    private final BlockQueue blockQueue;

    GameStatus(GameConfig config) {
        this.tetrisGrid = new TetrisGrid(config.rowSize(), config.colSize());
        this.blockQueue = new BlockQueue(config.seed());
        this.currentBlock = nextBlock();
    }

    private CurrentBlock nextBlock() {
        return new CurrentBlock(blockQueue.nextBlock());
    }

    private boolean blockFits() {
        for (BlockPosition p : currentBlock.shape()) {
            if (!tetrisGrid.isEmptyCell(p.getRowPosition(), p.getColIndex())) {
                return false;
            }
        }

        return true;
    }

    private void rotateBlockCW() {
        currentBlock.rotateCW();

        if (!blockFits()) {
            currentBlock.rotateCCW();
        }
    }

    private void rotateBlockCCW() {
        currentBlock.rotateCCW();

        if (blockFits()) {
            currentBlock.rotateCCW();
        }
    }

    private void moveBlockLeft() {
        currentBlock.move(0, -1);

        if (!blockFits()) {
            currentBlock.move(0, 1);
        }
    }

    private void moveBlockRight() {
        currentBlock.move(0, 1);

        if (!blockFits()) {
            currentBlock.move(0, -1);
        }
    }

    private boolean isGameOver() {
        return !(tetrisGrid.isRowEmpty(0) && tetrisGrid.isRowEmpty(1));
    }

    private void placeBlock() {
        for (BlockPosition p : currentBlock.shape()) {
            tetrisGrid.setGrid(p.getRowPosition(), p.getColIndex(), currentBlock.getId());
        }

        tetrisGrid.clearFullRows();

        if (isGameOver()) {
            // TODO: ranking
        }
    }
}
