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

    // top-left x-coordinate todo: simply use Rect?
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

    // initializes properties without setting coordinates
    public DrawRect(int color, Paint.Style style, float strokeWidth) {
        this.color = color;
        this.style = style;
        this.strokeWidth = strokeWidth;
    }

    // sets bounds of rectangle to those specified by given FloatRect
    public void setBounds(FloatRect bounds) {
        x = bounds.getX();
        y = bounds.getY();
        x1 = bounds.getX() + bounds.getWidth();
        y1 = bounds.getY() + bounds.getHeight();
    }

    public void setColor(int color) {
        this.color = color;
    }
    @Override
    public void draw(Canvas canvas, Context context) {
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(x, y, x1, y1, paint);
    }
}
