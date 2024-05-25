package org.se13.game.event;

import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;

import java.util.Arrays;

public record UpdateTetrisState(CellID[][] tetrisGrid, CurrentBlock nextBlock, int score, int remainingTime) implements TetrisEvent {

    @Override
    public String toString() {
        return Arrays.deepToString(tetrisGrid);
    }
}
