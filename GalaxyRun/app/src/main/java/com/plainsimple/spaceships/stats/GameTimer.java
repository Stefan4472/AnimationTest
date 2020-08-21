package com.plainsimple.spaceships.stats;

/**
 * Keeps track of the time something takes in milliseconds using System.time
 */

public class GameTimer {

    // last time the timer was started (ms)
    private long startTime;
    // number of milliseconds this timer has in total tracked
    private long msTracked;
    // whether timer is currently recording
    private boolean recording;

    public GameTimer() {

    }

    // starts the timer. No effect unless previously paused
    public void start() {
        if (!recording) {
            startTime = System.currentTimeMillis();
            recording = true;
        }
    }

    // stops recording and adds to msTracked from last startTime
    public void pause() {
        if (recording) {
            msTracked += System.currentTimeMillis() - startTime;
            recording = false;
        }
    }

    // reset time recorded to zero
    public void reset() {
        msTracked = 0;
    }

    // returns ms tracked
    public long getMsTracked() {
        if (recording) {
            return msTracked + System.currentTimeMillis() - startTime;
        } else {
            return msTracked;
        }
    }
}
