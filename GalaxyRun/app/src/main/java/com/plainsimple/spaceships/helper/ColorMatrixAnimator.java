package com.plainsimple.spaceships.helper;

import android.graphics.ColorMatrix;

/**
 * Created by Stefan on 8/24/2020.
 */

// class used by the Spaceship to animate its ColorMatrix. Currently the only supported animation
// is a flash, where every pixel's Red/Green/Blue values are briefly jacked up to 255 before coming
// back down to what they originally were.
// The ColorMatrix uses a 20-element float[], visualized as a 4x5 matrix. The 5th column of each row
// tells by how much to increase each Red/Green/Blue/Alpha value. So, to transition to full white,
// we want to change elems 4, 9, and 14 to 255, before bringing them back down to zero.
public class ColorMatrixAnimator {

    // used to count frames in an animation sequence
    private int frameCount;
    // whether the animation is currently running
    private boolean flashing;
    // number of frames it takes for sprite to reach completely white
    private int flashIn;
    // number of frames sprite will stay completely white
    private int flashStay;
    // number of frames it takes for sprite to go back to normal after being
    // completely white.
    private int flashOut;
    // total frame count for an animation
    private int totalFrames;
    // current calculated vals for ColorMatrix
    private float[] currentVals;
    // the actual ColorMatrix
    private ColorMatrix colorMatrix = new ColorMatrix();

    public ColorMatrixAnimator(int flashIn, int flashStay, int flashOut) {
        // set params
        this.flashIn = flashIn;
        this.flashStay = flashStay;
        this.flashOut = flashOut;
        totalFrames = flashIn + flashStay + flashOut;
        // set currentVals to default
        currentVals = colorMatrix.getArray();
    }

    // begins a flash animation sequence
    public void flash() {
        // if not currently flashing, set flashing to true and reset frameCount
        if (!flashing) {
            flashing = true;
            frameCount = 0;
        }
    }

    // updates the matrix by one frame
    public void update() {
        // check if frameCount has hit total frames, in which case stop flashing
        if (frameCount == totalFrames) {
            flashing = false;
        }
        // if currently flashing, update the matrix
        if (flashing) {
            frameCount++;
            int add_const = 0;
            // determine value to add to color chanel. We want it to go up to 255 over flashIn,
            // stay at 255 during flashStay, and go back to zero over flashOut
            if (frameCount <= flashIn) {
                add_const = 255 / flashIn;
            } else if (frameCount > flashIn + flashStay) {
                add_const = -255 / flashOut;
            }
            // update the 5th column of each color value
            for (int i = 0; i < 3; i++) {
                currentVals[4 + 5 * i] += add_const;
            }
            // update the matrix
            colorMatrix.set(currentVals);
        }
    }

    // returns ColorMatrix with calculated values
    public ColorMatrix getMatrix() {
        return colorMatrix;
    }
}
