package org.se13.game.timer;

public final class BlockCollideTimer extends Timer {
    public BlockCollideTimer(long startedTimeInSeconds) {
        super(startedTimeInSeconds);
        this.firstBlockCollideTime = 0;
        this.isBlockCollide = false;
    }

    public void setFirstBlockCollideTime(long firstBlockCollideTime) {
        if (this.isBlockCollide == false) {
            this.firstBlockCollideTime = firstBlockCollideTime;
            this.isBlockCollide = true;
        }
    }

    @Override
    public void reset(long l) {
        super.reset(l);
        this.firstBlockCollideTime = 0;
        this.isBlockCollide = false;
    }

    public boolean isTimerStarted() {
        if (this.isBlockCollide == true) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBlockPlaceTimeEnded() {
        if ((float) (super.currentTime - this.firstBlockCollideTime) / 1000000000 > 2.0f) {
            return true;
        } else {
            return false;
        }
    }

    private long firstBlockCollideTime;
    private boolean isBlockCollide;
}
