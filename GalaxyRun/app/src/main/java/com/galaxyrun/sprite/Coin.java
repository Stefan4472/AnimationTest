package com.galaxyrun.sprite;

import com.galaxyrun.engine.AnimID;
import com.galaxyrun.engine.GameContext;
import com.galaxyrun.engine.UpdateContext;
import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.helper.BitmapID;
import com.galaxyrun.helper.SpriteAnimation;
import com.galaxyrun.util.ProtectedQueue;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;

    public Coin(GameContext gameContext, double x, double y) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.COIN));

        setHitboxOffsetX(getWidth() * 0.15);
        setHitboxOffsetY(getHeight() * 0.1);
        setHitboxWidth(getWidth() * 0.7);
        setHitboxHeight(getHeight() * 0.8);

        spin = gameContext.animFactory.get(AnimID.COIN_SPIN);
        spin.start();
    }

    @Override
    public int getDrawLayer() {
        return 3;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (getX() < -getWidth()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override // speed tracks with game's scrollspeed for
    // smooth acceleration and decelleration
    public void updateSpeeds(UpdateContext updateContext) {
        setSpeedX(-updateContext.scrollSpeedPx);
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        spin.update(updateContext.getGameTime().msSincePrevUpdate);
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        if (s instanceof Spaceship) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        DrawImage img = new DrawImage(
                gameContext.bitmapCache.getBitmap(spin.getBitmapID()),
                spin.getCurrentFrameSrc(),
                (int) getX(),
                (int) getY()
        );
        drawQueue.push(img);
    }
}
