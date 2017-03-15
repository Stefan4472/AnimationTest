package com.plainsimple.spaceships.helper;

import android.support.v7.widget.ActivityChooserView;

/**
 * RocketManager for Rocket_2 type rockets.
 * The two rockets are fired in succession
 * (left first, then INTER_DELAY frames before
 * right), and then there is a longer pause
 * (FULL_DELAY) before they can be fired again.
 */

public class Rocket2Manager extends RocketManager {

    // delay after left before right can be fired
    private final static int INTER_DELAY = 30;
    // delay after right before left can be fired
    private final static int FULL_DELAY = 100;
    // frame count last time left was fired
    private int lastLeft = -FULL_DELAY;
    // frame count last time right was fired
    private int lastRight = 0;

    @Override
    public FireInstructions attemptFire(int frameCount) {
        int delay_left = frameCount - lastLeft;
        int delay_right = frameCount - lastRight;
        if (delay_left > delay_right && delay_left >= FULL_DELAY) {
            // fire left, but not right
            lastLeft = frameCount;
            return new FireInstructions(true, false);
        } else if (delay_right >= INTER_DELAY) {
            // fire right, but not left
            lastRight = frameCount;
            return new FireInstructions(false, true);
        } else { // still waiting
            return new FireInstructions(false, false);
        }
    }
}
