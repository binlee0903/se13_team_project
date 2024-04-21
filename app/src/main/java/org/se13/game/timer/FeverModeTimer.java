package org.se13.game.timer;

public class FeverModeTimer {
    public static final long DEFAULT_DURATION = 20000000000L;

    private long endTime = 0;
    private long pauseTime = 0;
    private boolean isActive = false;

    private Runnable executeFever;
    private Runnable releaseFever;

    public FeverModeTimer(Runnable execute, Runnable release) {
        executeFever = execute;
        releaseFever = release;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFeverMode() {
        return isActive && pauseTime == 0 && currentTime() < endTime;
    }

    public void setPause() {
        pauseTime = currentTime();
    }

    public void setResume() {
        endTime += currentTime() - pauseTime;
        pauseTime = 0;
    }

    public void execute() {
        execute(DEFAULT_DURATION);
    }

    public void execute(long duration) {
        endTime = currentTime() + duration;
        isActive = true;
        executeFever.run();
    }

    public void release() {
        isActive = false;
        releaseFever.run();
    }

    protected long currentTime() {
        return System.nanoTime();
    }
}
