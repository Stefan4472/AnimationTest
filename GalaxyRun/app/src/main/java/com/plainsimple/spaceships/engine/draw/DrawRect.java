package com.plainsimple.spaceships.engine.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.Rectangle;

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

    // sets bounds of rectangle to those specified by given Rectangle
    public void setBounds(Rectangle bounds) {
        x = (float) bounds.getX();
        y = (float) bounds.getY();
        x1 = (float) (bounds.getX() + bounds.getWidth());
        y1 = (float) (bounds.getY() + bounds.getHeight());
    }

    public void setBounds(float x, float y, float x1, float y1) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas, BitmapCache bitmapCache) {
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(x, y, x1, y1, paint);
    }
}
