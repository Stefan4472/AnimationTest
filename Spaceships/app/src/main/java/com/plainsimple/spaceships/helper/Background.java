package com.plainsimple.spaceships.helper;

import android.graphics.*;
import android.util.Log;

import com.plainsimple.spaceships.galaxydraw.DrawSpace;

/**
 * Draws the background of the game.
 * // todo: explanation
 */
public class Background {

    // number of pixels scrolled
    private int pixelsScrolled;
    // screen width (px)
    private int screenW;
    // screen height (px)
    private int screenH;
    // used to render space background
    private DrawSpace drawSpace;
    // rendered background that scrolls
    private Bitmap background;


    // increases scroll counter by x
    public void scroll(double x) {
        this.pixelsScrolled += x;
    }

    // resets Background // todo: refinement?
    public void reset() {
        pixelsScrolled = 0;
    }

    public Background(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        drawSpace = new DrawSpace();
        drawSpace.setAntiAlias(true);
        drawSpace.setVariance(0.2);
        drawSpace.setDensity(3);
        drawSpace.setStarSize(2);
        drawSpace.setUseGradient(false);
        drawSpace.setBackgroundColor(Color.BLACK);
        background = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        drawSpace.drawSpace(background);
    }

    // draws background onto canvas
    public void draw(Canvas canvas) {
        int offset = pixelsScrolled % screenW;
        Rect src = new Rect(offset, 0, screenW, screenH);
        Rect dst = new Rect(0, 0, src.width(), screenH);
        canvas.drawBitmap(background, src, dst, null);
        src = new Rect(0, 0, offset, screenH);
        dst = new Rect(screenW - offset, 0, screenW, screenH);
        canvas.drawBitmap(background, src, dst, null);
    }

    // returns "distance" travelled: 1 screen width = 1 kilometer (for now) todo: change?
    public float getDistanceTravelled() {
        return (float) pixelsScrolled / screenW;
    }
}