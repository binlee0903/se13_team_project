package org.se13.ai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.se13.game.block.CellID;
import org.se13.game.grid.TetrisGrid;

class PredictUtilsTest {

    private TetrisGrid board;

    @BeforeEach
    void setup() {
        board = new TetrisGrid(22, 10);
    }

    @Test
    void utilTest() {
        fill(0, 3, 0);
        fill(0, 5, 1);
        fill(0, 5, 2);

        setCell(0,3);
        setCell(2,3);
        setCell(4,3);

        fill(0, 6, 4);
        fill(0, 6, 5);
        fill(0, 5, 6);
        fill(0, 4, 7);
        fill(0, 4, 8);
        fill(0, 5, 9);

        Assertions.assertEquals(48, PredictUtils.aggregate(board));
        Assertions.assertEquals(2, PredictUtils.lines(board));
        Assertions.assertEquals(2, PredictUtils.holes(board));
        Assertions.assertEquals(6, PredictUtils.bumpiness(board));
    }

    private void setCell(int rowReverse, int col) {
        board.setCell(21 - rowReverse, col, CellID.TBLOCK_ID);
    }

    private void fill(int startRow, int endRow, int col) {
        for (int row = startRow; row < endRow; row++) {
            setCell(row, col);
        }
    }
}