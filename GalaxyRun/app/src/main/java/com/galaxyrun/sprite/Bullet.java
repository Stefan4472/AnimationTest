package com.galaxyrun.sprite;

import com.galaxyrun.engine.EventID;
import com.galaxyrun.engine.GameContext;
import com.galaxyrun.engine.UpdateContext;
import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.helper.BitmapID;
import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.util.ProtectedQueue;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(GameContext gameContext, double x, double y, double difficulty) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.BULLET_0));

        setHitboxOffsetX(getWidth() * 0.7);
        setHitboxOffsetY(-getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.45);
        setHitboxHeight(getHeight() * 1.4);

        // Damage and speed increase as difficulty increases
        setHealth(calcDamage(difficulty));
        setSpeedX(calcSpeed(gameContext.gameWidthPx, difficulty));
    }

    private static int calcDamage(double difficulty) {
        // Damage starts at 4, maxes out at 8
        return (int) (4 * (difficulty + 1));
    }

    private static double calcSpeed(int gameWidthPx, double difficulty) {
        // Speed starts at 30% of screen width per second,
        // maxes out at 50%
        double percent_per_sec = 0.3 + difficulty * 0.2;
        return gameWidthPx * percent_per_sec;
    }

    @Override
    public int getDrawLayer() {
        return 0;
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
        if (!(s instanceof Spaceship)) {
            updateContext.createEvent(EventID.BULLET_COLLIDED);
            setCollidable(false);
            setCurrState(SpriteState.TERMINATED);
            updateContext.createSound(SoundID.BULLET_DESTROYED);
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        drawQueue.push(new DrawImage(
                gameContext.bitmapCache.getBitmap(BitmapID.BULLET_0),
                (int) getX(),
                (int) getY()
        ));
    }
}
