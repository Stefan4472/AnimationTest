package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.engine.GameContext;

/**
 * Stores instructions for drawing text
 */

public class DrawText implements DrawParams {

    // text to draw
    private String text;
    // bottom-left x-coordinate
    private float x;
    // bottom-left y-coordinate
    private float y;
    // color of text to draw
    private int textColor;
    // size of text to draw
    private int textSize;
    // paint used by any class member to draw text
    private static Paint paint = new Paint();

    public DrawText(String text, float x, float y, int textColor, int textSize) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.textColor = textColor;
        this.textSize = textSize;
    }

    @Override
    public void draw(Canvas canvas, BitmapCache bitmapCache) { // todo: should paint be static? should it be passed as a parameter?
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        canvas.drawText(text, x, y, paint);
    }
}
