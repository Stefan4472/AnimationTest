package com.galaxyrun.engine;

import android.graphics.*;

import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.galaxydraw.GalaxyDrawOptions;
import com.galaxyrun.galaxydraw.GalaxyDrawer;
import com.galaxyrun.util.ProtectedQueue;

/**
 * Draws the background of the game. Renders a galaxy background using a
 * GalaxyDrawer, then scrolls from left to right in a loop.
 */
public class Background {

    private final GameContext gameContext;
    // Number of pixels that the background has scrolled.
    private int pixelsScrolled;
    // Used to render a starry "galaxy" background
    private final GalaxyDrawer galaxyDrawer;
    // Rendered background image.
    private final Bitmap background;
    // Relative speed of background scrolling to foreground scrolling.
    // A value below 1 gives a "parallax" effect.
    public static final float SCROLL_SPEED_FACTOR = 0.3f;

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
        // Only scroll the background while the rest of the game is moving.
        if (updateContext.gameState == GameState.PLAYING || updateContext.gameState == GameState.DEAD) {
            this.pixelsScrolled +=
                    updateContext.scrollSpeedPx * SCROLL_SPEED_FACTOR * updateContext.gameTime.secSincePrevUpdate;
        }
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