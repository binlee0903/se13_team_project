package org.se13.game.timer;

public class TimeLimitModeTimer extends Timer {
    public TimeLimitModeTimer(long startedTime) {
        super(startedTime);
    }

    public boolean isTimeOver() {
        return super.getElapsedTime() >= TIME_LIMITATION;
    }

    public int getRemainingTime() {
        return (int) ((TIME_LIMITATION - super.getElapsedTime()) / 1000000000L);
    }

    private long TIME_LIMITATION = 180000000000L; // 3 min
}
