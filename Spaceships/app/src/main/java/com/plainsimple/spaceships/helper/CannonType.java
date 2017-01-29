package com.plainsimple.spaceships.helper;

/**
 * Created by Stefan on 8/27/2016.
 */
public enum CannonType {

    LASER(10, 12, 0.01f),
    ION(20, 10, 0.011f),
    PLASMA(30, 8, 0.012f),
    PLUTONIUM(40, 18, 0.013f);

    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX;

    CannonType(int damage, int delay, float speedX) {
        this.damage = damage;
        this.delay = delay;
        this.speedX = speedX;
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
