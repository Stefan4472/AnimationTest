package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Stores instructions and contains method for drawing a rotated
 * bitmap. Extends DrawImage and adds rotating functionality. // todo: a better engine that can add all sorts of effects on the fly
 */

public class DrawRotatedImage extends DrawImage {

    // angle to rotate image when drawing
    private int rotatedAngle;
    // x-coordinate of pivot point for rotation
    private float pivotX;
    // y-coordinate of pivot point for rotation
    private float pivotY;

    public DrawRotatedImage(BitmapID bitmapID, float canvasX0, float canvasY0, int rotatedAngle,
                            float pivotX, float pivotY) {
        super(bitmapID, canvasX0, canvasY0);
        this.rotatedAngle = rotatedAngle;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        // save canvas so we can revert it after performing the rotation
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        // rotate canvas
        canvas.rotate(rotatedAngle, pivotX, pivotY);
        // draw bitmap
        canvas.drawBitmap(BitmapCache.getBitmap(bitmapID, context), canvasX0, canvasY0, null);
        // restore canvas to original matrix
        canvas.restore();
    }
}
