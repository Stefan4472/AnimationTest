package com.plainsimple.spaceships.helper;

import android.graphics.Color;

import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Class used to draw the sprite's health floating up from it when damaged.
 * Tracks with the sprite and moves relative to the sprite to the right and up.
 */ // todo: optimizations, variable direction of movement (left/right) and better color?

public class LoseHealthAnimation {

    // x-offset from sprite's x-coordinate where text will be drawn
    private double offsetX;
    // y-offset from sprite's y-coordinate where text will be drawn
    private double offsetY;
    // Dimensions of the game (px)
    private int gameWidthPx, gameHeightPx;
    // amount of health lost, which will be displayed
    private int healthLost;
    // whether text/animation is still running/showing
    private boolean showing;
    // counts number of milliseconds elapsed
    private int timeShownMs;
    // color of text to be drawn
    private static final int TEXT_COLOR = Color.WHITE;
    // size of text to be drawn
    private static final int TEXT_SIZE = 25;
    // Duration of the animation
    private static final int DURATION_MS = 1000;
    // movement of the text in x (as fraction of game width)
    private static final double REL_MOVEMENT_X = 0.1;
    // movement of the text in y (as fraction of game height)
    private static final double REL_MOVEMENT_Y = 0.1;


    /*
    Create and start the animation.
     */
    public LoseHealthAnimation(
            int gameWidthPx,
            int gameHeightPx,
            double offsetX,
            double offsetY,
            int healthLost
    ) {
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.healthLost = healthLost;
        showing = true;
    }

    // TODO: MOVE MORE LOGIC INTO UPDATE()
    public void update(long milliseconds) {
        timeShownMs += milliseconds;
        offsetX += (REL_MOVEMENT_X * gameWidthPx) * (milliseconds / (double) DURATION_MS);
        offsetY += (REL_MOVEMENT_Y * gameHeightPx) * (milliseconds / (double) DURATION_MS);

        if (timeShownMs >= DURATION_MS) {
            showing = false;
        }
    }

    // updates the animation if it is playing, including shifting based on the updated
    // sprite coordinates. Adds the animation's DrawParams to the given queue.
    public void getDrawParams(
            double spriteX,
            double spriteY,
            ProtectedQueue<DrawParams> drawQueue
    ) {
        if (showing) {
            drawQueue.push(new DrawText(
                    Integer.toString(healthLost),
                    (float) (spriteX + offsetX),
                    (float) (spriteY - offsetY),
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
