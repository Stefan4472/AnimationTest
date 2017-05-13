package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
    // rectangle specifying region of this bitmap to draw (defaults to full bitmap)
    protected Rect drawRegion;
    // degrees image will be rotated about center when drawn (default 0)
    protected float degreesRotation;
    // color filter used when drawing (default doesn't do anything)
    protected ColorMatrixColorFilter filter;
    // paint used for drawing
    private static Paint paint = new Paint();

    // constructor that sets bitmapID
    public DrawImage(BitmapID bitmapID) {
        this.bitmapID = bitmapID;
    }

    public DrawImage(BitmapID bitmapID, float canvasX0, float canvasY0) {
        this.bitmapID = bitmapID;
        this.canvasX0 = canvasX0;
        this.canvasY0 = canvasY0;
    }

    public void setCanvasX0(float canvasX0) {
        this.canvasX0 = canvasX0;
    }

    public void setCanvasY0(float canvasY0) {
        this.canvasY0 = canvasY0;
    }

    public void setDrawRegion(Rect drawRegion) {
        this.drawRegion = drawRegion;
    }

    public void setRotation(float degreesRotation) {
        this.degreesRotation = degreesRotation;
    }

    public void setFilter(ColorMatrix filter) {
        this.filter = new ColorMatrixColorFilter(filter);
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        // reset the paint object
        paint.reset();

        // get bitmapData for the image to be drawn
        BitmapData data = BitmapCache.getData(bitmapID, context);

        // set drawRegion to the full image if none was specified
        if (drawRegion == null) {
            drawRegion = new Rect(0, 0, data.getWidth(), data.getHeight());
        }

        // save and rotate canvas if a rotation was specified
        if (degreesRotation != 0) {
            // save canvas so we can revert it after performing the rotation
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            // rotate canvas about center of drawRegion
            canvas.rotate(degreesRotation, canvasX0 + drawRegion.width() / 2, canvasY0 + drawRegion.height() / 2);
        }

        // set color filter if one was specified
        if (filter != null) {
            paint.setColorFilter(filter);
        }

        // calculate destination coordinates
        Rect destination = new Rect((int) canvasX0, (int) canvasY0,
                (int) canvasX0 + drawRegion.width(), (int) canvasY0 + drawRegion.height());

        Log.d("DrawImage", "Drawing " + bitmapID + " from " + drawRegion.flattenToString() + " to " + destination.flattenToString());
        // draw command
        canvas.drawBitmap(BitmapCache.getBitmap(bitmapID, context), drawRegion, destination, paint);

        // restore canvas if it was previously rotated
        if (degreesRotation != 0) {
            canvas.restore();
        }
    }
}
