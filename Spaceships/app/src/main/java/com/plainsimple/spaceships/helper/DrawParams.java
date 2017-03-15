package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Interface for all sub-classes that draw onto the screen.
 */
public interface DrawParams {

    // draws to the canvas using the specific instructions stored in the class
    void draw(Canvas canvas, Context context); // todo: add method that takes paint object, and method that draws it rotated

}
