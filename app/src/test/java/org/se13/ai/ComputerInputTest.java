package org.se13.ai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.se13.game.block.Block;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;
import org.se13.utils.Matrix;

class ComputerInputTest {

    @Test
    void gnomeTest() {
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 10; j++) {
                for (Block block : Block.values()) {
                    float[][] w1 = Matrix.randn(10, 10);
                    float[][] w2 = Matrix.randn(10, 20);
                    float[][] w3 = Matrix.randn(20, 10);
                    float[][] w4 = Matrix.randn(10, 4);

                    ComputerInput input = new ComputerInput();
                    input.tetrisGrid = new TetrisGrid(22, 20).getGrid();
                    input.attacked = new TetrisGrid(i, j).getGrid();
                    input.nextBlock = new CurrentBlock(block);

                    int choose = input.inputs(w1, w2, w3, w4);
                    Assertions.assertTrue(choose >= 0);
                    Assertions.assertTrue(choose <= 3);
                }
            }
        }
    }
}