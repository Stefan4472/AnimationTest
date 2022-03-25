package com.plainsimple.spaceships.engine.ui;

import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Queue;

public class PauseButton extends UIElement {

    private boolean isPaused;
    // Calculated position
    private final int x, y, width;

    // Layout configuration
    private static final float WIDTH_PCT = 0.08f;
    private static final float X_OFFSET_PCT = 0.12f;
    private static final float Y_OFFSET_PCT = 0.02f;

    public PauseButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        x = (int) bounds.getX();
        y = (int) bounds.getY();
        width = (int) bounds.getWidth();
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
            gameContext.screenWidthPx * (1.0f - X_OFFSET_PCT - WIDTH_PCT),
            gameContext.screenWidthPx * Y_OFFSET_PCT,
            gameContext.screenWidthPx * WIDTH_PCT,
            gameContext.screenWidthPx * WIDTH_PCT
        );
    }

    public void update(UpdateContext updateContext) {
        isPaused = updateContext.isPaused;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        // TODO: need width and height
        BitmapID bitmap = (isPaused ? BitmapID.PAUSE_BUTTON_UNPAUSED : BitmapID.PAUSE_BUTTON_PAUSED);
        DrawImage drawBtn = new DrawImage(bitmap, x, y);
        drawParams.push(drawBtn);
    }

    @Override
    public boolean handleEvent(MotionEvent e, Queue<UIInputId> createdInput) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInBounds(e.getX(), e.getY())) {
                // Register event to toggle state
                createdInput.add((isPaused ? UIInputId.RESUME_GAME : UIInputId.PAUSE_GAME));
                return true;
            }
        }
        return false;
    }
}
