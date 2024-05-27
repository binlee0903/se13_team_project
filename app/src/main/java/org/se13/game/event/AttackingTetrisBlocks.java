package org.se13.game.event;

import org.se13.game.block.CellID;

import java.io.Serializable;

public record AttackingTetrisBlocks(CellID[][] cellIDs) implements TetrisEvent, Serializable {
}
