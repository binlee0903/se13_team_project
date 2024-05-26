package org.se13.game.event;

import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;

public record NextBlockEvent(TetrisGrid board, CurrentBlock block) implements TetrisEvent {
}
