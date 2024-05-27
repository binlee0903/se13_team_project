package org.se13.game.event;

import org.se13.game.block.CellID;

import java.io.Serializable;

public record AttackedTetrisBlocks(CellID[][] blocks) implements TetrisEvent, Serializable {
}
