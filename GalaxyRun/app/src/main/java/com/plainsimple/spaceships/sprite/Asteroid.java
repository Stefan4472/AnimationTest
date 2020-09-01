package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.LinkedList;
import java.util.List;

/**
 * The Asteroid is a fairly slow-moving sprite that rotates. It bounces
 * off the edges of the screen and has relatively high hp. Rotation rate
 * as well as speedY are randomized to give each Asteroid an element of
 * uniqueness.
 */

public class Asteroid extends Sprite {

    private static final BitmapID BITMAP_ID = BitmapID.ASTEROID;
    // current rotation, in degrees, of asteroid
    private float currentRotation;
    // degrees rotated per frame (positive or negative)
    private float rotationRate;

    private DrawImage DRAW_ASTEROID;

    // draws animated healthbar above Asteroid if Asteroid takes damage
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing Asteroid taking damage
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Asteroid(
            int spriteId,
            double x,
            double y,
            double difficulty,
            GameContext gameContext
    ) {
        super(spriteId, SpriteType.ASTEROID, x, y, BITMAP_ID, gameContext);
        // Set speedX slower than scrollspeed (give the player a chance to destroy it)
//        speedX = scrollSpeed * 0.6f;
        setSpeedX(-gameContext.getGameWidthPx() * 0.05);
        // Set speedY to some randomized positive/negative value up to |0.03|
        // of screen width
        double rel_speed = (random.nextBoolean() ? -1 : +1) * random.nextDouble() * 0.03;
        setSpeedY(rel_speed * gameContext.getGameWidthPx());
        // Set health relatively high
        setHealth(10 + (int) (difficulty / 100));
        // Make hitbox 20% smaller than sprite
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);
        setHitboxOffsetX(getWidth() * 0.1);
        setHitboxOffsetY(getHealth() * 0.1);

        DRAW_ASTEROID = new DrawImage(BITMAP_ID);

        // Set the current rotation to a random angle
        currentRotation = random.nextInt(360);
        // Set rotation rate as function fo speedY (faster speed = faster rotation)
        rotationRate = (float) (getSpeedY() * 200 / gameContext.getGameHeightPx());
        // Init HealthBarAnimation for use if Asteroid takes damage
        healthBarAnimation = new HealthBarAnimation(getWidth(), getHeight(), getHealth());
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
                getY() >= (gameContext.getGameHeightPx() - getHeight()) &&
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
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        takeDamage(damage, updateContext);

        if (s.getSpriteType() == SpriteType.BULLET) {
            updateContext.createEvent(EventID.ASTEROID_SHOT);
        }

        // Start HealthBarAnimation and LoseHealthAnimations
        // if Asteroid took damage and isn't dead.
        if (damage > 0 && getCurrState() == SpriteState.ALIVE) {
            healthBarAnimation.start();
            loseHealthAnimations.add(new LoseHealthAnimation(
                    getWidth(),
                    getHeight(),
                    (float) (s.getX() - getX()),
                    (float) (s.getY() - getY()),
                    damage
            ));
        }
    }

    @Override
    public void die(UpdateContext updateContext) {
        updateContext.createEvent(EventID.ASTEROID_DIED);
        setCurrState(SpriteState.TERMINATED);
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // update DRAW_ASTEROID params with new coordinates and rotation
        DRAW_ASTEROID.setCanvasX0((float) getX());
        DRAW_ASTEROID.setCanvasY0((float) getY());
        DRAW_ASTEROID.setRotation((int) currentRotation);
        drawQueue.push(DRAW_ASTEROID);

        // Update and draw loseHealthAnimations
        for (LoseHealthAnimation anim : loseHealthAnimations) {
            if (!anim.isFinished()) {
                anim.update();
                anim.getDrawParams((float) getX(), (float) getY(), drawQueue);
            }
        }

        // update and draw healthBarAnimation if showing
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.update();
            healthBarAnimation.getDrawParams((float) getX(), (float) getY(), getHealth(), drawQueue);
        }
    }
}
