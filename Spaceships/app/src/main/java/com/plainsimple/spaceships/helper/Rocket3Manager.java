package com.plainsimple.spaceships.helper;

/**
 * RocketManager for Rocket_3 type rockets.
 * 6 rockets are fired alternating, in quick
 * succession (INTER_DELAY) before waiting
 * FULL_DELAY
 */

public class Rocket3Manager extends RocketManager {

    // delay between firing alternating rocket
    private static final int INTER_DELAY = 20;
    // delay once all six have been fired
    private static final int FULL_DELAY = 100;
    // framecount last time a barrage of 6 began
    private int lastBarrage = -FULL_DELAY - 6 * INTER_DELAY;

    @Override
    public FireInstructions attemptFire(int frameCount) {
        // barrage is in progress: calculate whether a rocket should be fired,
        // and if so, which one
        if (frameCount - lastBarrage < 6 * INTER_DELAY) {
            int rockets_since = (frameCount - lastBarrage) / INTER_DELAY;
            if (rockets_since % 2 == 0 && rockets_since % INTER_DELAY == 0) {
                // time to fire left
                return new FireInstructions(true, false);
            } else if (rockets_since % INTER_DELAY == 0) {
                // time to fire right
                return new FireInstructions(false, true);
            } else {
                // not yet time to fire either
                return new FireInstructions(false, false);
            }
        } else if (frameCount - lastBarrage >= 6 * INTER_DELAY + FULL_DELAY) {
            // start a new barrage and fire left
            lastBarrage = frameCount;
            return new FireInstructions(true, false);
        } else {
            return new FireInstructions(false, false);
        }
    }
}
