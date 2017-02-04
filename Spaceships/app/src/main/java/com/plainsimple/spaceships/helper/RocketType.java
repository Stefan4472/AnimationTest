package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 8/27/2016.
 */
public enum RocketType {

    ROCKET(BitmapID.ROCKET, 40, 40, 0.0067f);

    // BitmapID of fired bullet when it is shown on screen
    private BitmapID drawableId;
    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX; // todo: acceleration class?

    RocketType(BitmapID drawableId, int damage, int delay, float speedX) {
        this.drawableId = drawableId;
        this.damage = damage;
        this.delay = delay;
        this.speedX = speedX;
    }

    public BitmapID getDrawableId() {
        return drawableId;
    }

    public int getDamage() {
        return damage;
    }

    public int getDelay() {
        return delay;
    }

    public float getSpeedX() {
        return speedX;
    }
}
