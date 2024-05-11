package org.se13.view.tetris;

import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;

public record TetrisState(CellID[][] tetrisGrid, CurrentBlock nextBlock, int score, int remainingTime) {
}
