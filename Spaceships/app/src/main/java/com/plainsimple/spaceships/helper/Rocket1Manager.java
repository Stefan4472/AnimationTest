package com.plainsimple.spaceships.helper;

/**
 * RocketManager for Rocket_1 type rockets.
 * Both rockets are fired simultaneously after
 * DELAY frames.
 */

public class Rocket1Manager extends RocketManager {

    private static final int DELAY = 30;

    // last time left rocket was fired
    private int lastFire = -DELAY;

    @Override
    public FireInstructions attemptFire(int frameCount) {
        if (frameCount - lastFire >= DELAY) {
            lastFire = frameCount;
            return new FireInstructions(true, true);
        } else {
            return new FireInstructions(false, false);
        }
    }
}
