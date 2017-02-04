package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 8/27/2016.
 */
public enum CannonType {

    LASER(BitmapID.LASER_BULLET, 10, 12, 0.01f),
    ION(BitmapID.ION_BULLET, 20, 10, 0.011f),
    PLASMA(BitmapID.LASER_BULLET, 30, 8, 0.012f), // todo: graphics
    PLUTONIUM(BitmapID.LASER_BULLET, 40, 18, 0.013f);

    // R.drawable of fired bullet when it is shown on screen
    private BitmapID drawableId;
    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX;

    CannonType(BitmapID drawableId, int damage, int delay, float speedX) {
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
