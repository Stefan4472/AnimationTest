package com.plainsimple.spaceships.engine.ui;

import android.graphics.*;
import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage2;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.galaxydraw.DrawSpace;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Draws the background of the game.
 * // todo: explanation
 */
public class Background {

    private GameContext gameContext;
    // number of pixels scrolled
    private int pixelsScrolled;
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

    public Background(GameContext gameContext) {
        this.gameContext = gameContext;
        drawSpace = new DrawSpace();
        drawSpace.setAntiAlias(true);
        drawSpace.setVariance(0.2);
        drawSpace.setDensity(3);
        drawSpace.setStarSize(2);
        drawSpace.setUseGradient(false);
        drawSpace.setBackgroundColor(Color.BLACK);
        background = Bitmap.createBitmap(
                gameContext.gameWidthPx, gameContext.gameHeightPx, Bitmap.Config.ARGB_8888);
        drawSpace.drawSpace(background);
    }

    public void update(UpdateContext updateContext) {
//        scroll(updateContext.getScrollSpeed() * updateContext.;);
        scroll(1);
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        // Requires two draw calls to split over the screen
        int offset = pixelsScrolled % gameContext.gameWidthPx;
        Rect src = new Rect(offset, 0, gameContext.gameWidthPx, gameContext.gameHeightPx);
        Rect dst = new Rect(0, 0, src.width(), gameContext.gameHeightPx);
        drawParams.push(new DrawImage2(background, src, dst));

        Rect src2 = new Rect(0, 0, offset, gameContext.gameHeightPx);
        Rect dst2 = new Rect(gameContext.gameWidthPx - offset, 0, gameContext.gameWidthPx, gameContext.gameHeightPx);
        drawParams.push(new DrawImage2(background, src2, dst2));
    }

    // returns "distance" travelled: 1 screen width = 1 kilometer (for now) todo: change?
    public float getDistanceTravelled() {
        return (float) pixelsScrolled / gameContext.gameWidthPx;
    }
}