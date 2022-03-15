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
    private DrawImage DRAW_COIN;

    public Coin(int spriteId, double x, double y, GameContext gameContext) {
        super(spriteId, SpriteType.COIN, x, y, gameContext);

        setHitboxOffsetX(getWidth() * 0.15);
        setHitboxOffsetY(getHeight() * 0.1);
        setHitboxWidth(getWidth() * 0.7);
        setHitboxHeight(getHeight() * 0.8);

        spin = gameContext.getAnimFactory().get(AnimID.COIN_SPIN);
        setWidth(spin.getFrameW());
        setHeight(spin.getFrameH());
        spin.start();
        DRAW_COIN = new DrawImage(spin.getBitmapID());
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
//        speedX = GameView.getScrollSpeed();
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        spin.update(updateContext.getGameTime().getMsSincePrevUpdate());
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
        DRAW_COIN.setCanvasX0((float) getX());
        DRAW_COIN.setCanvasY0((float) getY());
        DRAW_COIN.setDrawRegion(spin.getCurrentFrameSrc());
        drawQueue.push(DRAW_COIN);
    }
}
