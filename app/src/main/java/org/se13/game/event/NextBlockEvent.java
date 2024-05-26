package org.se13.game.event;

import org.se13.game.block.CellID;

public record NextBlockEvent(CellID[][] tetrisGrid, CellID[][] withoutCurrentBlock) implements TetrisEvent {
}
