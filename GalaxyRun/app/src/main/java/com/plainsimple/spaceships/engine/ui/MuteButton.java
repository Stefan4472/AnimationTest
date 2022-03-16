package com.plainsimple.spaceships.engine.ui;

import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.util.ProtectedQueue;

public class MuteButton {

    // Layout configuration
    private final float WIDTH_PCT = 0.08f;
    private final float X_OFFSET_PCT = 0.06f;
    private final float Y_OFFSET_PCT = 0.02f;

    private GameContext gameContext;
    private boolean isMuted;
    // Calculated position
    private final int x, y, width;

    public MuteButton(GameContext gameContext) {
        this.gameContext = gameContext;
        x = (int) (gameContext.gameWidthPx * (1.0f - X_OFFSET_PCT - WIDTH_PCT));
        y = (int) (gameContext.gameWidthPx * Y_OFFSET_PCT);
        width = (int) (gameContext.gameWidthPx * WIDTH_PCT);
    }

    public void update(UpdateContext updateContext) {

    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        // TODO: need width and height
        BitmapID bitmap = (isMuted ? BitmapID.MUTE_BUTTON_MUTED : BitmapID.MUTE_BUTTON_UNMUTED);
        DrawImage drawBtn = new DrawImage(bitmap);
        drawBtn.setCanvasX0(x);
        drawBtn.setCanvasY0(y);
        drawParams.push(drawBtn);
    }
}
