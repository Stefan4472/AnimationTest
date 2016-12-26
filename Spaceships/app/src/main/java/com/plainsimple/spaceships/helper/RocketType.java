package com.plainsimple.spaceships.helper;

/**
 * Created by Stefan on 8/27/2016.
 */
public class RocketType {

    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX; // todo: acceleration class?

    // default Rocket constants
    public static final int ROCKET_DAMAGE = 40;
    public static final int ROCKET_DELAY = 40;
    public static final float ROCKET_SPEED_X = 0.0067f; // todo: acceleration class?

    public static final RocketType ROCKET = new RocketType(ROCKET_DAMAGE, ROCKET_DELAY, ROCKET_SPEED_X);

    private RocketType(int damage, int delay, float speedX) {
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
