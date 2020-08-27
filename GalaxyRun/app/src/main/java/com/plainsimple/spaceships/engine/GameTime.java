package com.plainsimple.spaceships.engine;

/**
 * Basically a struct to store a couple values related to game timing.
 */

public class GameTime {
    private long currTimeMs;
    private long msSincePrevUpdate;
    private long runTimeMs;

    public GameTime(long currTimeMs, long msSincePrevUpdate, long runTimeMs) {
        this.currTimeMs = currTimeMs;
        this.msSincePrevUpdate = msSincePrevUpdate;
        this.runTimeMs = runTimeMs;
    }

    public long getCurrTimeMs() {
        return currTimeMs;
    }

    public long getMsSincePrevUpdate() {
        return msSincePrevUpdate;
    }

    public long getRunTimeMs() {
        return runTimeMs;
    }
}
