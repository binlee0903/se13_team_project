package org.se13.game.event;

import org.se13.game.block.CellID;

public record AttackedTetrisBlocks(CellID[][] blocks) implements TetrisEvent {
}
