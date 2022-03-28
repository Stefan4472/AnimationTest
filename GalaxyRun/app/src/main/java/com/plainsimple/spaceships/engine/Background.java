package com.plainsimple.spaceships.engine;

import android.graphics.*;

import com.plainsimple.spaceships.engine.draw.DrawImage2;
import com.plainsimple.spaceships.engine.draw.DrawInstruction;
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

    // relative speed of background scrolling to foreground scrolling TODO
    public static final float SCROLL_SPEED_CONST = 0.4f;

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
                gameContext.screenWidthPx, gameContext.screenHeightPx, Bitmap.Config.ARGB_8888);
        drawSpace.drawSpace(background);
    }

    public void update(UpdateContext updateContext) {
        // Scroll at 30% of Map scroll speed
        scroll(updateContext.scrollSpeedPx * 0.3 *
                updateContext.gameTime.msSincePrevUpdate / 1000.0);
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        // Requires two draw calls to split over the screen
        int offset = pixelsScrolled % gameContext.screenWidthPx;
        Rect src = new Rect(offset, 0, gameContext.screenWidthPx, gameContext.screenHeightPx);
        Rect dst = new Rect(0, 0, src.width(), gameContext.screenHeightPx);
        drawInstructions.push(new DrawImage2(background, src, dst));

        Rect src2 = new Rect(0, 0, offset, gameContext.screenHeightPx);
        Rect dst2 = new Rect(
                gameContext.screenWidthPx - offset,
                0,
                gameContext.screenWidthPx,
                gameContext.screenHeightPx
        );
        drawInstructions.push(new DrawImage2(background, src2, dst2));
    }

    // returns "distance" travelled: 1 screen width = 1 kilometer (for now) todo: change?
    public float getDistanceTravelled() {
        return (float) pixelsScrolled / gameContext.screenWidthPx;
    }
}