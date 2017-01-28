package com.plainsimple.spaceships.helper;

/**
 * Created by Stefan on 8/27/2016.
 */
public enum RocketType {

    ROCKET(40, 40, 0.0067f);

    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX; // todo: acceleration class?

    RocketType(int damage, int delay, float speedX) {
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
