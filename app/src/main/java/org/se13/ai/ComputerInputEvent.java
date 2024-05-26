package org.se13.ai;

import org.se13.game.block.CurrentBlock;
import org.se13.game.event.TetrisEvent;
import org.se13.game.grid.TetrisGrid;

public record ComputerInputEvent(TetrisGrid board, CurrentBlock block) implements TetrisEvent {
}
