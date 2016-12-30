package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Stores instructions for drawing a Bitmap
 */

public class DrawImage implements DrawParams {

    // ID of bitmap to be drawn
    private BitmapResource bitmapID;
    // x-coordinate where drawing begins on canvas
    private float canvasX0;
    // y-coordinate where drawing begins on canvas
    private float canvasY0;

    @Override
    public void draw(Canvas canvas, Context context) {
        canvas.drawBitmap(BitmapCache.getImage(bitmapID, context), canvasX0, canvasY0, null);
    }
}
