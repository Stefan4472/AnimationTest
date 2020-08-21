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
    private int lastLeft = -FULL_DELAY - INTER_DELAY;

    @Override
    public FireInstructions attemptFire(int frameCount) {
        int frames_since = frameCount - lastLeft;
        if (frames_since == INTER_DELAY) { // fire right
            return new FireInstructions(false, true);
        } else if (frames_since >= INTER_DELAY + FULL_DELAY) { // allowed to fire left again
            lastLeft = frameCount;
            return new FireInstructions(true, false);
        } else { // none can be fired
            return new FireInstructions(false, false);
        }
    }
}
