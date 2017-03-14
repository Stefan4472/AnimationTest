package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Stores instructions for drawing a Bitmap
 */

public class DrawImage implements DrawParams {

    // ID of bitmap to be drawn
    protected BitmapID bitmapID;
    // x-coordinate where drawing begins on canvas
    protected float canvasX0;
    // y-coordinate where drawing begins on canvas
    protected float canvasY0;

    public DrawImage(BitmapID bitmapID, float canvasX0, float canvasY0) {
        this.bitmapID = bitmapID;
        this.canvasX0 = canvasX0;
        this.canvasY0 = canvasY0;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        canvas.drawBitmap(BitmapCache.getImage(bitmapID, context), canvasX0, canvasY0, null);
    }
}
