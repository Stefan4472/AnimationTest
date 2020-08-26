package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;
    private DrawImage DRAW_COIN;

    public Coin(float x, float y, GameContext gameContext) {
        super(x, y, gameContext);
        spin = gameContext.getAnimCache().get(BitmapID.COIN_SPIN);
        width = spin.getFrameW();
        height = spin.getFrameH();
        spin.start();
        DRAW_COIN = new DrawImage(spin.getBitmapID());
        hitBox = new FloatRect(x + getWidth() * 0.15f, y + getHeight() * 0.1f, x + getWidth() * 0.85f, y + getHeight() * 0.9f);
    }

    @Override
    public void updateActions() {
        if (!isInBounds()) {
            terminate = true;
        }
    }

    @Override // speed tracks with game's scrollspeed for
    // smooth acceleration and decelleration
    public void updateSpeeds() {
//        speedX = GameView.getScrollSpeed();
    }

    @Override
    public void updateAnimations() {
        spin.incrementFrame();
    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        if (s instanceof Spaceship) {
            terminate = true;
        }
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DRAW_COIN.setCanvasX0(x);
        DRAW_COIN.setCanvasY0(y);
        DRAW_COIN.setDrawRegion(spin.getCurrentFrameSrc());
        drawQueue.push(DRAW_COIN);
    }
}
