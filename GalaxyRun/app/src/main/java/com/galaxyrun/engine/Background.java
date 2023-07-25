package com.galaxyrun.engine;

import android.graphics.*;

import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.galaxydraw.GalaxyDrawOptions;
import com.galaxyrun.galaxydraw.GalaxyDrawer;
import com.galaxyrun.util.ProtectedQueue;

/**
 * Draws the background of the game.
 * // todo: explanation
 */
public class Background {

    private GameContext gameContext;
    // number of pixels scrolled
    private int pixelsScrolled;
    // Used to render a starry "galaxy" background
    private GalaxyDrawer galaxyDrawer;
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
        galaxyDrawer = new GalaxyDrawer(gameContext.rand);

        GalaxyDrawOptions options = new GalaxyDrawOptions(
                /*startColor=*/Color.BLACK,
                /*endColor=*/Color.BLACK,
                /*starDensity=*/2,
                /*starColor=*/Color.argb(200, 255, 255, 238),
                /*starSize=*/2,
                /*sizeVariance=*/0.5f,
                /*colorVariance=*/0.3f
        );
        background = galaxyDrawer.drawGalaxy(
                gameContext.screenWidthPx, gameContext.screenHeightPx, options);
    }

    public void update(UpdateContext updateContext) {
        // Scroll at 30% of Map scroll speed
        scroll(updateContext.scrollSpeedPx * 0.3 * updateContext.gameTime.msSincePrevUpdate / 1000.0);
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        // Requires two draw calls to split over the screen
        int offset = pixelsScrolled % gameContext.screenWidthPx;
        Rect src = new Rect(offset, 0, gameContext.screenWidthPx, gameContext.screenHeightPx);
        Rect dst = new Rect(0, 0, src.width(), gameContext.screenHeightPx);
        drawInstructions.push(new DrawImage(background, src, dst));

        Rect src2 = new Rect(0, 0, offset, gameContext.screenHeightPx);
        Rect dst2 = new Rect(gameContext.screenWidthPx - offset, 0, gameContext.screenWidthPx, gameContext.screenHeightPx);
        drawInstructions.push(new DrawImage(background, src2, dst2));
    }
}