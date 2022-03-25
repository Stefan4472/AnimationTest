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

public class MuteButton extends UIElement {

    private boolean isMuted;
    // Calculated position
    private final int x, y, width;

    // Layout configuration
    private static final float WIDTH_PCT = 0.08f;
    private static final float X_OFFSET_PCT = 0.06f;
    private static final float Y_OFFSET_PCT = 0.02f;

    public MuteButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        x = (int) bounds.getX();
        y = (int) bounds.getY();
        width = (int) bounds.getWidth();
    }

    public static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
                gameContext.screenWidthPx * (1.0f - X_OFFSET_PCT - WIDTH_PCT),
                gameContext.screenWidthPx * Y_OFFSET_PCT,
                gameContext.screenWidthPx * WIDTH_PCT,
                gameContext.screenWidthPx * WIDTH_PCT
        );
    }

    public void update(UpdateContext updateContext) {
        isMuted = updateContext.isMuted;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        // TODO: need width and height
        BitmapID bitmap = (isMuted ? BitmapID.MUTE_BUTTON_MUTED : BitmapID.MUTE_BUTTON_UNMUTED);
        DrawImage drawBtn = new DrawImage(bitmap, x, y);
        drawParams.push(drawBtn);
    }

    @Override
    public boolean handleEvent(MotionEvent e, Queue<UIInputId> createdInput) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInBounds(e.getX(), e.getY())) {
                // Create event to toggle state
                createdInput.add((isMuted ? UIInputId.UNMUTE_GAME : UIInputId.MUTE_GAME));
                return true;
            }
        }
        return false;
    }
}
