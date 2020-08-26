package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.List;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private static final BitmapID BITMAP_ID = BitmapID.BULLET_0;
    private DrawImage DRAW_BULLET;

    public static final int DAMAGE = 5;
    public static final int DELAY_FRAMES = 12;  // TODO: USE MILLISECOND DELAY INSTEAD
    public static final float TRAVEL_SPEED = 0.01f;  // TODO: MAKE DOUBLE

    public Bullet(int spriteId, float x, float y, GameContext gameContext) {
        super(spriteId, SpriteType.BULLET, x, y, BITMAP_ID, gameContext);
        hitBox = new FloatRect(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);
        hp = DAMAGE;
        speedX = TRAVEL_SPEED;
        DRAW_BULLET = new DrawImage(BITMAP_ID);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (!isInBounds()) {
            terminate = true;
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        collides = false;
        terminate = true;
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DRAW_BULLET.setCanvasX0(x);
        DRAW_BULLET.setCanvasY0(y);
        drawQueue.push(DRAW_BULLET);
    }
}
