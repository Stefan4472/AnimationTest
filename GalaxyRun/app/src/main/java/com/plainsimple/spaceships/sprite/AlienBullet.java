package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
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
    private final double travelAngle;

    public AlienBullet(
            GameContext gameContext,
            double x,
            double y,
            double targetX,
            double targetY,
            double scrollSpeedPx
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ALIEN_BULLET));

        setHealth(10);
        // SpeedX is fixed TODO: use current scrollspeed
        setSpeedX(-scrollSpeedPx * 0.8);

        // Calculate travelAngle based on distance to target in x and y.
        // Keep in mind this gets tricky because we're in canvas coordinates
        double distX = x - targetX;
        double distY = y - targetY;
        travelAngle = Math.toDegrees(Math.atan(distY / distX));

        // Calculate speedY using relative screen dimensions
        double timeToImpactSec = Math.abs(distX / getSpeedX());
        setSpeedY(-distY / timeToImpactSec);
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

    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {

    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        updateContext.createEvent(EventID.ALIEN_BULLET_COLLIDED);
        setCollidable(false);
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        DrawImage drawBullet = new DrawImage(BitmapID.ALIEN_BULLET, (float) getX(), (float) getY());
        drawBullet.setRotation((int) travelAngle);
        drawQueue.push(drawBullet);
    }
}
