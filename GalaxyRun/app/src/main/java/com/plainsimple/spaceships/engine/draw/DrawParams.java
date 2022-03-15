package com.plainsimple.spaceships.engine.draw;

import android.graphics.Canvas;

import com.plainsimple.spaceships.helper.BitmapCache;

/**
 * Interface for all sub-classes that draw onto the screen.
 */
public interface DrawParams {

    // draws to the canvas using the specific instructions stored in the class
    // todo: add method that takes paint object, and method that draws it rotated
    void draw(Canvas canvas, BitmapCache bitmapCache);

}
