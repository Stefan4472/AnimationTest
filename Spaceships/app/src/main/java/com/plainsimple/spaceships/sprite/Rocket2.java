package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRotatedImage;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;

/**
 * Rocket that has a large hitbox that functions as a proximity
 * detector for Aliens and Asteroids. The proximity detector will also
 * go off if the Rocket is close to a head-on collision with an Obstacle..
 * When the proximity detector
 * is triggered, the explosion animation starts and the hitbox is
 * made small again. Each subsequent frame the hitbox increases and
 * hp increases until it hits maximum size/strength. This action is
 * configurable.
 * The rocket also has a rotate animation.
 */

public class Rocket2 extends Rocket {

    // current rotation of rocket
    private int currentRotation;
    // degrees to rotate image of rocket each frame
    private static final int ROTATION_RATE = 5;

    // explosion animation
    private SpriteAnimation explode;
    // whether explosion has been triggered by collision with initial hitbox
    private boolean explodeTriggered;
    // hitbox triggered by obstacles (only for head-on collisions)
    private Hitbox obstacleHitbox;
    // number of frames since explosion
    private int explodeFrameCount;
    // number of frames explosion lasts
    private int explodeLength = 5;
    // number of pixels hitbox expands on each coordinate
    // per frame of explosion
    private int explosionExpandRate;

    // hp at the center of the explosion
    private static final int FULL_HP = 50;
    // hp lost per frame of the explosion
    private static final int HP_LOSS_RATE = 4;

    protected Rocket2(Context context, float x, float y) {
        super(BitmapCache.getData(BitmapID.ROCKET_2, context), x, y);

        speedX = 0.005f;

        // create large hitbox with side approx. equal to 5 * width
        hitBox = new Hitbox(x - 2 * getWidth(), y - 2 * getWidth(), x + 3 * getWidth(), y + getHeight() + 2 * getWidth());

        // create smaller hitbox used only with obstacles
        obstacleHitbox = new Hitbox(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);

        // init explode animation
        explode = AnimCache.get(BitmapID.EXPLOSION_1, context);

        explosionExpandRate = getWidth() / 2;
    }

    @Override
    public void updateActions() {
        // terminate if out of bounds or explode anim has played
        if (!isInBounds() || explode.hasPlayed()) {
            terminate = true;
        } else if (explodeTriggered && explodeFrameCount < explodeLength) {
            explodeFrameCount++;
            // expand hitbox outward and decrease hp
            hitBox.reset(hitBox.getX() - explosionExpandRate, hitBox.getY() - explosionExpandRate,
                    hitBox.getWidth() + 2 * explosionExpandRate, hitBox.getHeight() + 2 * explosionExpandRate);
            hp = (hp >= HP_LOSS_RATE ? hp - HP_LOSS_RATE : hp);
        }
        // update obstacleHitbox
        obstacleHitbox.offset(GameView.screenW * speedX, GameView.screenH * speedY);
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {
        if (explode.isPlaying()) {
            explode.incrementFrame();
        } else {
            currentRotation += ROTATION_RATE;
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        // check if conditions met to trigger explosion
        if (!explodeTriggered && ((s instanceof Alien || s instanceof Asteroid)
                || obstacleHitbox.intersects(s.getHitBox()))) {
            explodeTriggered = true;
            speedX = s.speedX;
            explode.start();
            hitBox.reset(x, y, getWidth(), getHeight()); // todo: want it to be square?
            hp = FULL_HP;
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        if (explode.isPlaying()) {
            drawParams.add(new DrawSubImage(explode.getBitmapID(), x + (explode.getFrameW() - getWidth()) / 2, y - (explode.getFrameH() - getHeight()) / 2,
                    explode.getCurrentFrameSrc())); // todo: refine
        } else if (!explode.hasPlayed()){
            drawParams.add(new DrawRotatedImage(bitmapData.getId(), x, y, currentRotation, x + getWidth() / 2, y + getHeight() / 2));
        }
        return drawParams;
    }
}
