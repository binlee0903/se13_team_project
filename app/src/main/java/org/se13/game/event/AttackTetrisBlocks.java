package org.se13.game.event;

import org.se13.game.block.BlockPosition;

public record AttackTetrisBlocks(BlockPosition[][] blocks) implements TetrisEvent {
}
