package com.plainsimple.spaceships.helper;

/**
 * Created by Stefan on 8/27/2016.
 */
public class BulletType {

    // damage bullet does on contact with another sprite
    private int damage;
    // the number of frames that must pass before the Spaceship can fire again
    private int delay;
    // speed the bullet travels when fired
    private float speedX;

    // laser bullet constants
    private static final int LASER_DAMAGE = 10;
    private static final int LASER_DELAY = 12;
    private static final float LASER_SPEED_X = 0.01f;

    // ion bullet constants
    private static final int ION_DAMAGE = 20;
    private static final int ION_DELAY = 10;
    private static final float ION_SPEED_X = 0.011f;

    // plasma bullet constants
    private static final int PLASMA_DAMAGE = 30;
    private static final int PLASMA_DELAY = 8;
    private static final float PLASMA_SPEED_X = 0.012f;

    // plutonium bullet constants
    private static final int PLUTONIUM_DAMAGE = 40;
    private static final int PLUTONIUM_DELAY = 18;
    private static final float PLUTONIUM_SPEED_X = 0.013f;

    public static final BulletType LASER = new BulletType(LASER_DAMAGE, LASER_DELAY, LASER_SPEED_X);
    public static final BulletType ION = new BulletType(ION_DAMAGE, ION_DELAY, ION_SPEED_X);
    public static final BulletType PLASMA = new BulletType(PLASMA_DAMAGE, PLASMA_DELAY, PLASMA_SPEED_X);
    public static final BulletType PLUTONIUM = new BulletType(PLUTONIUM_DAMAGE, PLUTONIUM_DELAY, PLUTONIUM_SPEED_X);

    private BulletType(int damage, int delay, float speedX) {
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
