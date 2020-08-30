package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private static final BitmapID BITMAP_ID = BitmapID.BULLET_0;
    private DrawImage DRAW_BULLET;

    public static final int DAMAGE = 5;
    public static final int DELAY_FRAMES = 12;  // TODO: USE MILLISECOND DELAY INSTEAD
    // Bullet speed per second, as percentage of screen width
    public static final double SPEED_PERCENT_PER_SEC = 0.1;


    public Bullet(int spriteId, double x, double y, GameContext gameContext) {
        super(spriteId, SpriteType.BULLET, x, y, BITMAP_ID, gameContext);

        setHitboxOffsetX(getWidth() * 0.7);
        setHitboxOffsetY(-getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.45);
        setHitboxHeight(getHeight() * 1.4);

        setHealth(DAMAGE);
        setSpeedX(SPEED_PERCENT_PER_SEC * gameContext.getGameWidthPx());

        DRAW_BULLET = new DrawImage(BITMAP_ID);
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (!isVisibleInBounds()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {

    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {

    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        updateContext.createEvent(EventID.BULLET_COLLIDED);
        setCollidable(false);
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void die(UpdateContext updateContext) {

    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DRAW_BULLET.setCanvasX0((float) getX());
        DRAW_BULLET.setCanvasY0((float) getY());
        drawQueue.push(DRAW_BULLET);
    }
}
