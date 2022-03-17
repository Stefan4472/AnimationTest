package com.plainsimple.spaceships.stats;

import com.plainsimple.spaceships.engine.GameTime;

/**
 * Keeps track of the time something takes in milliseconds using System.time
 */

public class GameTimer {

    // TODO: CLEANUP

    // TODO: DIFFERENTIATE "GAME TIME" (TIME SINCE GAME WAS STARTED) VS. "RUN TIME"
    // (TIME SINCE THE RUN BEGAN). PROVIDE "MARKRUNSTARTED()/MARKRUNFINISHED()" FUNCTIONS.
    // THIS ALLOWS US TO PROVIDE TIMING INFORMATION EVEN WHILE THE "RUN" ISN'T IN_PROGRESS

    // last time the timer was started (ms)
    private long startTime;
    private long lastUpdateMs;
    private long carriedUpdateTimeMs;
    private boolean isPaused = true;
    // number of milliseconds this timer has in total tracked
    private long msTracked;

    public GameTimer() {
        carriedUpdateTimeMs = 0;
        msTracked = 0;
    }

    // Starts the timer. Doesn't do anything if the timer isn't paused.
    public void resume() {
        if (isPaused) {
            startTime = System.currentTimeMillis();
            lastUpdateMs = startTime;
            isPaused = false;
        }
    }

    // Adds to msTracked from last startTime
    public void pause() {
        if (!isPaused) {
            carriedUpdateTimeMs = System.currentTimeMillis() - startTime;
            msTracked += carriedUpdateTimeMs;
            isPaused = true;
        }
    }

    public void reset() {
        // TODO
    }

    public GameTime recordUpdate() {
        long currTime = System.currentTimeMillis();
        if (isPaused) {
            return new GameTime(currTime, 0, msTracked);
        } else {
            // TODO: NOT SURE IF THIS WORKS CORRECTLY (THE TRICKY THING IS TO WORK OVER PAUSE()/RESUME())
            long msThisUpdate = currTime - lastUpdateMs + carriedUpdateTimeMs;
            long runTime = msTracked + currTime - startTime;

            lastUpdateMs = currTime;
            carriedUpdateTimeMs = 0;
            return new GameTime(currTime, msThisUpdate, runTime);
        }
    }

    public long getRunTimeMs() {
        if (isPaused) {
            return msTracked;
        } else {
            return msTracked + System.currentTimeMillis() - startTime;
        }
    }
}
