package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Stores instructions for drawing part of a Bitmap
 */

public class DrawSubImage implements DrawParams {

    // ID of bitmap to be drawn
    private BitmapResource bitmapID;
    // x-coordinate where drawing begins on canvas
    private float canvasX0;
    // y-coordinate where drawing begins on canvas
    private float canvasY0;
    // starting x-coordinate 
    private float x0;
    // starting y-coordinate
    private float y0;
    // ending x-coordinate
    private float x1;
    // ending y-coordinate
    private float y1;

    public DrawSubImage(BitmapResource bitmapID, float canvasX0, float canvasY0, float x0, float y0, float x1, float y1) { // todo: setParams method to set all params at once
        this.bitmapID = bitmapID;
        this.canvasX0 = canvasX0;
        this.canvasY0 = canvasY0;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        Rect source = new Rect((int) x0, (int) y0, (int) x1, (int) y1);
        Rect destination = new Rect((int) canvasX0, (int) canvasY0,
                (int) canvasX0 + source.width(), (int) canvasY0 + source.height());
        canvas.drawBitmap(BitmapCache.getImage(bitmapID, context), source, destination, null);
    }
}
