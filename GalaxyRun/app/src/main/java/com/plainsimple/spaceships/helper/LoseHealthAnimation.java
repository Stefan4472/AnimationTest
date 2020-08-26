package com.plainsimple.spaceships.helper;

import android.graphics.Color;

import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.List;

/**
 * Class used to draw the sprite's health floating up from it when damaged.
 * Tracks with the sprite and moves relative to the sprite to the right and up.
 */ // todo: optimizations, variable direction of movement (left/right) and better color?

public class LoseHealthAnimation {

    // x-offset from sprite's x-coordinate where text will be drawn
    private float offsetX;
    // y-offset from sprite's y-coordinate where text will be drawn
    private float offsetY;
    // amount of health lost, which will be displayed
    private String healthLost;
    // whether text/animation is still running/showing
    private boolean showing;
    // counts number of frames elapsed
    private int frameCounter;
    // color of text to be drawn
    private static final int TEXT_COLOR = Color.WHITE;
    // size of text to be drawn
    private static final int TEXT_SIZE = 25;
    // number of frames to display
    private static final int NUM_FRAMES = 20;
    // movement of the text in x (as fraction of sprite width)
    private static final float REL_MVMNT_X = 0.4f;
    // movement of the text in y (as fraction of sprite height)
    private static final float REL_MVMNT_Y = -0.4f;
    // movement of text in x and y per frame (px)
    private float frameDx, frameDy;

    // constructs and starts animation
    public LoseHealthAnimation(int spriteWidth, int spriteHeight, float offsetX,
                               float offsetY, int healthLost) {
        // calculate px text should move in x and y each frame
        frameDx = spriteWidth * REL_MVMNT_X / NUM_FRAMES;
        frameDy = spriteHeight * REL_MVMNT_Y / NUM_FRAMES;
        // set other vars and start animation
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.healthLost = Integer.toString(healthLost);
        showing = true;
    }

    // TODO: MOVE MORE LOGIC INTO UPDATE()
    public void update() {
        if (frameCounter == NUM_FRAMES) { // reset
            frameCounter = 0;
            showing = false;
        } else if (showing) {
            frameCounter++;
        }
    }

    // updates the animation if it is playing, including shifting based on the updated
    // sprite coordinates. Adds the animation's DrawParams to the given queue.
    public void getDrawParams(float spriteX, float spriteY,
            ProtectedQueue<DrawParams> drawQueue) {
        if (showing) {
            offsetX += frameDx;
            offsetY += frameDy;
            drawQueue.push(new DrawText(
                    healthLost,
                    spriteX + offsetX,
                    spriteY + offsetY,
                    TEXT_COLOR,
                    TEXT_SIZE
            ));
        }
    }

    // returns whether animation has finished
    public boolean isFinished() {
        return !showing;
    }
}
