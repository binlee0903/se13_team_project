package org.se13.game.timer;

import org.se13.game.rule.GameLevel;

public class BlockFallingTimer extends Timer {
    public BlockFallingTimer(long startedTime) {
        super(startedTime);
    }

    public void fasterBlockFallingTime(GameLevel difficulty) {

        switch (difficulty) {
            case EASY:
                blockFallingTimeManipulator += (long) (BLOCK_FALLING_TIME_SUBTRACTOR * 0.8);
                break;
            case NORMAL:
                blockFallingTimeManipulator += BLOCK_FALLING_TIME_SUBTRACTOR;
                break;
            case HARD:
                blockFallingTimeManipulator += (long) (BLOCK_FALLING_TIME_SUBTRACTOR * 1.2);
                break;
            default:
        }
    }

    public boolean isBlockFallingTimeHasGone() {
        return getElapsedTime() >= blockFallingTimeManipulator;
    }

    private final long BLOCK_FALLING_TIME_SUBTRACTOR = -700000000;
    private long blockFallingTimeManipulator = 3000000000L;
}
