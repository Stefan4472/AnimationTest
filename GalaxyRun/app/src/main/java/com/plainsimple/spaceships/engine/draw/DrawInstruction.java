package com.plainsimple.spaceships.engine.draw;

import android.graphics.Canvas;

import com.plainsimple.spaceships.helper.BitmapCache;

/**
 * Interface for all sub-classes that draw onto the screen.
 */
public interface DrawInstruction {

    // Draw to the canvas.
    void draw(Canvas canvas);

}
