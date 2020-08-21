package com.plainsimple.spaceships.helper;

/**
 * RocketManager for Rocket_0 type rockets.
 * Both rockets are fired simultaneously after
 * DELAY frames.
 */

public class Rocket0Manager extends RocketManager {

    private static final int DELAY = 40;

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
