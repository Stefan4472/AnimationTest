package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.helper.FontId;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

// TODO: needs a lot of cleanup
public class PauseOverlay extends UIElement {
    private final static double WIDTH_PCT = 0.4;
    private final static double HEIGHT_PCT = 0.3;
    private final static double TEXT_SIZE_PCT = 0.2;

    public PauseOverlay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        isTouchable = false;
        isVisible = false;
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                (gameContext.gameWidthPx - width) / 2,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    public void show() {
        isTouchable = true;
        isVisible = true;
    }

    public void hide() {
        isTouchable = false;
        isVisible = false;
    }

    @Override
    public void update(UpdateContext updateContext) {

    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        drawInstructions.push(DrawRect.filled(bounds.toRect(), Color.BLACK));

        // https://stackoverflow.com/a/26975371
        Paint paint = new Paint();
        Rect textBounds = new Rect();
        paint.setTypeface(gameContext.fontCache.get(FontId.GALAXY_MONKEY));
        paint.setTextSize((int) (gameContext.gameHeightPx * TEXT_SIZE_PCT));
        String text = "Paused";
        paint.getTextBounds(text, 0, text.length(), textBounds);
        int text_width =  textBounds.width();
        int text_height =  textBounds.height();
        drawInstructions.push(new DrawText(
                "Paused",
                (float) (bounds.getX() + (bounds.getWidth() - text_width) / 2),
                (float) (bounds.getY() + (bounds.getHeight() - text_height) / 2 + text_height),
                Color.YELLOW,
                (int) (gameContext.gameHeightPx * TEXT_SIZE_PCT),
                gameContext.fontCache.get(FontId.GALAXY_MONKEY)
        ));
    }

    @Override
    public void onTouchEnter(float x, float y) {

    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchLeave(float x, float y) {

    }
}
