package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * An AlienBullet is a projectile fired by an Alien at a
 * certain set of coordinates (specified in the constructor).
 * The AlienBullet will determine its own speedX and speedY
 * to reach the target. It will also request to be drawn at
 * the correct angle to reach its target.
 */
public class AlienBullet extends Sprite {

    // Angle at which bullet travels
    private double fireAngle;
    // DrawParam used to draw the AlienBullet
    private DrawImage DRAW_BULLET;

    private static final BitmapID BITMAP_ID = BitmapID.ALIEN_BULLET;


    public AlienBullet(
            int spriteId,
            double x,
            double y,
            double targetX,
            double targetY,
            GameContext gameContext
    ) { // todo: damage as a parameter?
        super(spriteId, SpriteType.ALIEN_BULLET, x, y, BITMAP_ID, gameContext);

        // DrawParam used to draw the AlienBullet
        DRAW_BULLET = new DrawImage(BITMAP_ID);
        setHealth(10);

        // SpeedX is fixed
        setSpeedX(-0.008f * gameContext.getGameWidthPx());

        // Calculate fireAngle based on distance to target in x and y.
        // Keep in mind this gets tricky because we're in canvas coordinates
        double dist_x = x - targetX;
        double dist_y = y - targetY;
        fireAngle = Math.toDegrees(Math.atan(dist_y / dist_x));

        // TODO: UPDATE
        // Calculate speedY using relative screen dimensions
        double frames_to_impact = Math.abs(dist_x / gameContext.getGameWidthPx() / getSpeedX());
        setSpeedY(-dist_y / gameContext.getGameHeightPx() / frames_to_impact);
        // Cap speedY if abs. value greater than 0.012f
        if (Math.abs(getSpeedY()) > 0.012) {
            setSpeedY(Math.signum(getSpeedY()) * 0.012);
        }
    }

    @Override
    public int getDrawLayer() {
        return 0;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (!isVisibleInBounds()) {
            updateContext.createEvent(EventID.ALIEN_BULLET_OFF_SCREEN);
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // Do nothing
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        // Do nothing
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        updateContext.createEvent(EventID.ALIEN_BULLET_COLLIDED);
        setCollidable(false);
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void die(UpdateContext updateContext) {
        // Do nothing (already handled in collision)
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DRAW_BULLET.setCanvasX0((float) getX());
        DRAW_BULLET.setCanvasY0((float) getY());
        DRAW_BULLET.setRotation((int) fireAngle);
        drawQueue.push(DRAW_BULLET);
    }
}
