package org.se13.game.event;

import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;

public record UpdateTetrisState(CellID[][] tetrisGrid, CurrentBlock nextBlock, int score, int remainingTime) implements TetrisEvent {
}
