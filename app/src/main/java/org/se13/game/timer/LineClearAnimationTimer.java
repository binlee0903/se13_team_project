package org.se13.game.timer;

public class LineClearAnimationTimer extends Timer {
    public LineClearAnimationTimer(long startedTime) {
        super(startedTime);
    }

    public boolean startLineClearAnimation(BlockCollideTimer blockCollideTimer, BlockFallingTimer blockFallingTimer, FeverModeTimer feverModeTimer) {
        if (isTimerStarted == false) {
            isTimerStarted = true;
            blockCollideTimer.pauseTimer();
            blockFallingTimer.pauseTimer();
            feverModeTimer.setPause();
            startedTime = System.nanoTime();
            currentTime = System.nanoTime();

            new Thread(() -> {
                while (getElapsedTime() < duration) {
                    currentTime = System.nanoTime();
                }
                isTimerOver = true;
            }).start();
        }

        return isTimerStarted;
    }

    public void resetFlags() {
        isTimerOver = false;
        isTimerStarted = false;
    }

    public boolean isTimerOver() {
        return isTimerOver;
    }

    private long duration = 500000000L;
    private boolean isTimerStarted = false;
    private boolean isTimerOver = false;
}
