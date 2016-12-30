package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Stores instructions for drawing a Rectangle
 */

public class DrawRect implements DrawParams {

    private static Paint paint = new Paint();

    // top-left x-coordinate
    private float x;
    // top-left y-coordinate
    private float y;
    // bottom-right x-coordinate
    private float x1;
    // bottom-right y-coordinate
    private float y1;
    // color of rectangle
    private int color;
    // style to use in drawing
    private Paint.Style style;
    // width of stroke
    private float strokeWidth;

    public DrawRect(float x, float y, float x1, float y1, int color, Paint.Style style, float strokeWidth) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
        this.style = style;
        this.strokeWidth = strokeWidth;
    }

    public DrawRect(Rect rect, int color, Paint.Style style, float strokeWidth) {
        x = rect.left;
        y = rect.top;
        x1 = rect.right;
        y1 = rect.bottom;
        this.color = color;
        this.style = style;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(x, y, x1, y1, paint);
    }
}
