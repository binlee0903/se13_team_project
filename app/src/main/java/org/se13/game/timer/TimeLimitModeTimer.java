package org.se13.game.timer;

public class TimeLimitModeTimer extends Timer {
    public TimeLimitModeTimer(long startedTime) {
        super(startedTime);
        isDisabled = false;
    }

    public void disableTimer() {
        isDisabled = true;
    }

    public boolean isTimeOver() {
        if (isDisabled == true) {
            return false;
        }

        return super.getElapsedTime() >= TIME_LIMITATION;
    }

    public int getRemainingTime() {
        if (isDisabled == true) {
            return DEFAULT_TIME;
        }

        return (int) ((TIME_LIMITATION - super.getElapsedTime()) / 1000000000L);
    }

    private boolean isDisabled;
    private int DEFAULT_TIME = 999;
    private long TIME_LIMITATION = 180000000000L; // 3 min
}
