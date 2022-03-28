package com.plainsimple.spaceships.engine.ui;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

public class ShootButton extends UIElement {
    private boolean isShooting;
    private static final double WIDTH_PCT = 0.6;
    private static final double HEIGHT_PCT = 0.8;

    public ShootButton(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                gameContext.gameWidthPx - width,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    @Override
    public void update(UpdateContext updateContext) {
        if (isShooting) {
            createdInput.add(UIInputId.SHOOT);
        }
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {

    }

    @Override
    public void onTouchEnter(float x, float y) {
        isShooting = true;
    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchLeave(float x, float y) {
        isShooting = false;
    }
}
