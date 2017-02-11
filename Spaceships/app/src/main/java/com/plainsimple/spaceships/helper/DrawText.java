package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

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

    public DrawText(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Canvas canvas, Context context) {
        canvas.drawText(text, x, y, null);
    }
}
