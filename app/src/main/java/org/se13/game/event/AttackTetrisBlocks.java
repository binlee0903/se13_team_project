package org.se13.game.event;

import org.se13.game.block.CellID;

public record AttackTetrisBlocks(CellID[][] cellIDs) implements TetrisEvent {
}
