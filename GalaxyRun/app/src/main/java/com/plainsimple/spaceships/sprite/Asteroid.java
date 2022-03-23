package com.plainsimple.spaceships.sprite;

import android.util.Log;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The Asteroid is a fairly slow-moving sprite that rotates. It bounces
 * off the edges of the screen and has relatively high hp. Rotation rate
 * as well as speedY are randomized to give each Asteroid an element of
 * uniqueness.
 */

public class Asteroid extends Sprite {

    // current rotation, in degrees, of asteroid
    private float currentRotation;
    // degrees rotated per frame (positive or negative)
    // TODO: base on game time elapsed
    private float rotationRate;

    // draws animated healthbar above Asteroid if Asteroid takes damage
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing Asteroid taking damage
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Asteroid(
            GameContext gameContext,
            double x,
            double y,
            double difficulty,
            double scrollSpeedPx
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ASTEROID));

        // Set SpeedX to a random value between 60% and 120% of current scrollSpeed
        setSpeedX(-scrollSpeedPx * (0.6 + random.nextInt(60) / 100.0));
        // Set SpeedY to a random value between -20% and +20% of current scrollSpeed
        setSpeedY(-scrollSpeedPx * (-0.2 + random.nextInt(40) / 100.0));
        // Set health relatively high
        setHealth(10 + (int) (difficulty * 20));
        // Make hitbox 20% smaller than sprite
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);
        setHitboxOffsetX(getWidth() * 0.1);
        setHitboxOffsetY(getHealth() * 0.1);

        // Set the current rotation to a random angle
        currentRotation = random.nextInt(360);
        // Set rotation rate as function fo speedY (faster speed = faster rotation)
        rotationRate = (float) (getSpeedY() * 200.0 / gameContext.gameHeightPx);
        // Init HealthBarAnimation for use if Asteroid takes damage
        healthBarAnimation = new HealthBarAnimation(
                gameContext.gameWidthPx,
                gameContext.gameHeightPx,
                getWidth(),
                getHeight(),
                getHealth()
        );
    }

    @Override
    public int getDrawLayer() {
        return 5;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        if (getX() < -getWidth()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // Reverse speedY if it is nearly headed off a screen edge (i.e. "bounce")
        boolean leaving_above =
                getY() >= (gameContext.gameHeightPx - getHeight()) &&
                getSpeedY() > 0;
        boolean leaving_below =
                getY() <= 0 && getSpeedY() < 0;

        if (leaving_above || leaving_below) {
            setSpeedY(-1 * getSpeedY());
        }
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        // Increment currentRotation to create the rotating animation
        currentRotation += rotationRate;

        // Update LoseHealthAnimations
        Iterator<LoseHealthAnimation> health_anims = loseHealthAnimations.iterator();
        while(health_anims.hasNext()) {
            LoseHealthAnimation anim = health_anims.next();
            // Remove animation if finished
            if (anim.isFinished()) {
                health_anims.remove();
            } else {  // Update animation
                anim.update(updateContext.getGameTime().msSincePrevUpdate);
            }
        }

        // Update HealthbarAnimation
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.update(updateContext.getGameTime().msSincePrevUpdate);
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        takeDamage(damage, updateContext);

        if (s instanceof Bullet) {
            updateContext.createEvent(EventID.ASTEROID_SHOT);
        }

        Log.d("Asteroid", String.format("Took damage %d, state is %s", damage, getCurrState().toString()));
        // Start HealthBarAnimation and LoseHealthAnimations
        if (damage > 0) {
            Log.d("Asteroid", "Creating LoseHealthAnimation");
            healthBarAnimation.setHealth(getHealth());
            healthBarAnimation.start();

            loseHealthAnimations.add(new LoseHealthAnimation(
                    getWidth(),
                    getHeight(),
                    s.getX() - getX(),
                    s.getY() - getY(),
                    damage
            ));
        }
    }

    @Override
    public void die(UpdateContext updateContext) {
        updateContext.createEvent(EventID.ASTEROID_DIED);  // TODO: rename "asteroid_destroyed"
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // update DRAW_ASTEROID params with new coordinates and rotation
        DrawImage drawAsteroid = new DrawImage(BitmapID.ASTEROID, (float) getX(), (float) getY());
        drawAsteroid.setRotation((int) currentRotation);
        drawQueue.push(drawAsteroid);

        // Draw loseHealthAnimations
        for (LoseHealthAnimation anim : loseHealthAnimations) {
            anim.getDrawParams(getX(), getY(), drawQueue);
        }

        // Draw healthBarAnimation
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.getDrawParams(getX(), getY(), getHealth(), drawQueue);
        }
    }
}
