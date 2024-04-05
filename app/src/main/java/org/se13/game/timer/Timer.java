package org.se13.game.timer;

public class Timer {
    public Timer(long startedTime) {
        this.startedTime = startedTime;
    }

    public long getElapsedTime() {
        return currentTime - startedTime;
    }

    public void setCurrentTime(long l) {
        this.currentTime = l;
    }

    public void pauseTimer() {
        this.beforePausedTimeInterval = this.currentTime - this.startedTime;
    }

    public void resumeTimer(long l) {
        this.startedTime = l - this.beforePausedTimeInterval;
        this.currentTime = l;
    }

    public void reset(long l) {
        this.startedTime = l;
    }

    protected long startedTime;
    protected long currentTime;
    protected long beforePausedTimeInterval;
}
