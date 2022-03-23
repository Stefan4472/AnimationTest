package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.AnimID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;

    public Coin(double x, double y, GameContext gameContext) {
        super(SpriteType.COIN, x, y, gameContext);

        spin = gameContext.animFactory.get(AnimID.COIN_SPIN);
        setWidth(spin.getFrameW());
        setHeight(spin.getFrameH());
        spin.start();

        setHitboxOffsetX(getWidth() * 0.15);
        setHitboxOffsetY(getHeight() * 0.1);
        setHitboxWidth(getWidth() * 0.7);
        setHitboxHeight(getHeight() * 0.8);
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
        if (s.getSpriteType() == SpriteType.SPACESHIP) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void die(UpdateContext updateContext) {

    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DrawImage img = new DrawImage(spin.getBitmapID(), (float) getX(), (float) getY());
        img.setDrawRegion(spin.getCurrentFrameSrc());
        drawQueue.push(img);
    }
}
