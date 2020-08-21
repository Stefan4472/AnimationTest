package com.plainsimple.spaceships.helper;

/**
 * RocketManager for Rocket_3 type rockets.
 * 6 rockets are fired alternating, in quick
 * succession (INTER_DELAY) before waiting
 * FULL_DELAY
 */

public class Rocket3Manager extends RocketManager {

    // delay between firing alternating rocket
    private static final int INTER_DELAY = 15;
    // delay once all six have been fired
    private static final int FULL_DELAY = 100;
    // framecount last time a barrage of 6 began
    private int lastBarrage = -FULL_DELAY - 5 * INTER_DELAY;

    @Override
    public FireInstructions attemptFire(int frameCount) {
        int frames_since = frameCount - lastBarrage;

        if (frames_since <= 5 * INTER_DELAY) { // barrage in progress
            int rockets_since = (frames_since) / INTER_DELAY;
            if (frames_since % INTER_DELAY == 0 && rockets_since % 2 == 0) {
                // fire left if frames since is a multiple of INTER_DELAY and
                // and even number of rockets has been fired
                return new FireInstructions(true, false);
            } else if (frames_since % INTER_DELAY == 0) { // time to fire right
                return new FireInstructions(false, true);
            } else { // not yet time to fire either
                return new FireInstructions(false, false);
            }
        } else if (frameCount - lastBarrage >= 5 * INTER_DELAY + FULL_DELAY) {
            // start a new barrage and fire left
            lastBarrage = frameCount;
            return new FireInstructions(true, false);
        } else { // don't shoot!
            return new FireInstructions(false, false);
        }
    }
}
