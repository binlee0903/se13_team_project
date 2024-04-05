package org.se13.game.timer;

public class BlockFallingTimer extends Timer {
    public BlockFallingTimer(long startedTime) {
        super(startedTime);
    }

    public void fasterBlockFallingTime() {
        blockFallingTimeManipulator += BLOCK_FALLING_TIME_SUBTRACTOR;
    }

    public boolean isBlockFallingTimeHasGone() {
        return (float)getElapsedTime() / 1000000000.0f > blockFallingTimeManipulator;
    }

    private final float BLOCK_FALLING_TIME_SUBTRACTOR = -0.2f;
    private float blockFallingTimeManipulator = 1.0f;
}
