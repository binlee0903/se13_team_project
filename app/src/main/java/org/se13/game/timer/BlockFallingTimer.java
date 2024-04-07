package org.se13.game.timer;

public class BlockFallingTimer extends Timer {
    public BlockFallingTimer(long startedTime) {
        super(startedTime);
    }

    public void fasterBlockFallingTime() {
        blockFallingTimeManipulator += BLOCK_FALLING_TIME_SUBTRACTOR;
    }

    public boolean isBlockFallingTimeHasGone() {
        return getElapsedTime() >= blockFallingTimeManipulator;
    }

    private final long BLOCK_FALLING_TIME_SUBTRACTOR = -100000000;
    private long blockFallingTimeManipulator = 1000000000;
}
