package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BulletType;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.RawResource;
import com.plainsimple.spaceships.helper.RocketType;
import com.plainsimple.spaceships.helper.SoundParams;
import com.plainsimple.spaceships.helper.SpriteAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // tilt of screen as reported by gyroscope (y-axis)
    private float tilt;
    private float lastTilt;
    // minimum change to register
    private final static float MIN_TILT_CHANGE = 0.005f;
    private float maxSpeedY = 0.01f;

    private SpriteAnimation move; // todo: resources static?
    private SpriteAnimation fireRocket;
    private SpriteAnimation explode;

    // whether user has control over spaceship
    boolean controllable;

    private boolean firesBullets;
    private BulletType bulletType = BulletType.LASER;
    private int lastFiredBullet;
    private BitmapData bulletBitmapData;

    private boolean firesRockets;
    private RocketType rocketType = RocketType.ROCKET;
    private int lastFiredRocket;
    private BitmapData rocketBitmapData;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles;

    // current setting: bullets or rockets
    private int firingMode = BULLET_MODE;
    private boolean shooting = false;
    public static final int BULLET_MODE = 1;
    public static final int ROCKET_MODE = 2;

    private SoundParams rocketSound;
    private SoundParams bulletSound;
    private SoundParams explodeSound;

    public List<Sprite> getAndClearProjectiles() {
        List<Sprite> copy = new ArrayList<>();
        for(Sprite p : projectiles) {
            copy.add(p);
        }
        projectiles.clear();
        return copy;
    }

    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }
    public void setShooting(boolean shooting) { this.shooting = shooting; }
    public void setFiringMode(int firingMode) { this.firingMode = firingMode; }

    public void setBullets(boolean firesBullets, BulletType bulletType) {
        this.firesBullets = firesBullets;
        this.bulletType = bulletType;
    }

    public void setRockets(boolean firesRockets, RocketType rocketType) {
        this.firesRockets = firesRockets;
        this.rocketType = rocketType;
    }

    // default constructor
    public Spaceship(BitmapData bitmapData, float x, float y) {
        super(bitmapData, x, y);
        initCraft();
    }

    private void initCraft() {
        projectiles = new ArrayList<>();
        lastFiredBullet = 0;
        lastFiredRocket = 0;
        damage = 100;
        hp = 100;
        controllable = true;
        collides = true;

        hitBox = new Rect((int) (x + getWidth() * 0.17f), (int) (y + getHeight() * 0.22f), (int) (x + getWidth() * 0.83f), (int) (y + getHeight() * 0.78f));
        bulletSound = new SoundParams(RawResource.LASER, 1.0f, 1.0f, 1, 0, 1.0f);
        rocketSound = new SoundParams(RawResource.ROCKET, 1.0f, 1.0f, 1, 0, 1.0f);
        explodeSound = new SoundParams(RawResource.EXPLOSION, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void injectResources(SpriteAnimation movingAnimation, SpriteAnimation fireRocketAnimation, // todo: make part of constructor
            SpriteAnimation explodeAnimation, BitmapData bulletBitmapData, BitmapData rocketBitmapData) { // todo: fix so dimensions are right
        this.move = movingAnimation;
        this.move.start();
        this.fireRocket = fireRocketAnimation;
        this.explode = explodeAnimation;
        this.bulletBitmapData = bulletBitmapData;
        this.rocketBitmapData = rocketBitmapData;
    }

    @Override
    public void updateActions() {
        lastFiredBullet++;
        if (shooting && firingMode == BULLET_MODE && lastFiredBullet >= bulletType.getDelay()) {
            fireBullets();
            lastFiredBullet = 0;
        }
        lastFiredRocket++;
        if (shooting && firingMode == ROCKET_MODE && lastFiredRocket >= rocketType.getDelay()) {
            fireRockets();
            lastFiredRocket = 0;
            fireRocket.start();
        }
    }

    // fires two rockets
    public void fireRockets() {
        projectiles.add(new Rocket(rocketBitmapData, x + getWidth() * 0.80f, y + 0.29f * getHeight(), rocketType));
        projectiles.add(new Rocket(rocketBitmapData, x + getWidth() * 0.80f, y + 0.65f * getHeight(), rocketType));
        GameActivity.playSound(rocketSound);
    }

    // fires two bullets
    public void fireBullets() {
        projectiles.add(new Bullet(bulletBitmapData, x + getWidth() * 0.78f, y + 0.28f * getHeight(), bulletType));
        projectiles.add(new Bullet(bulletBitmapData, x + getWidth() * 0.78f, y + 0.66f * getHeight(), bulletType));
        GameActivity.playSound(bulletSound);
    }

    // sets current tilt of device and determines dy
    public void setTilt(float newTilt) {
        if (Math.abs(newTilt - tilt) >= MIN_TILT_CHANGE) {
            lastTilt = tilt;
            tilt = newTilt;
            //Log.d("Spaceship", "Registered Tilt Change of " + (tilt - lastTilt));
        }
    }

    @Override
    public void updateSpeeds() {
        // negative is tilting away from player -> move up
        // positive is tilting toward player -> move down
        float tiltChange = tilt - lastTilt;
        //Log.d("Spaceship.java", "Tilt is " + tilt + " and change is " + tiltChange);
    }

    @Override
    public void updateAnimations() {
        if (move.isPlaying()) {
            move.incrementFrame();
        }
        if (fireRocket.isPlaying()) {
            fireRocket.incrementFrame();
        }
        if (explode.isPlaying()) {
            explode.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        hp -= s.getDamage();
        Log.d("Spaceship class", "Collided with " + (s instanceof Alien ? "alien" : "sprite") + " at " + s.getX());
        if (hp < 0 && !explode.isPlaying()) { // todo: set hp <= 0 (currently < 0 for debug)
            // todo: check when explode animation has played and use for end game logic
            explode.start();
            GameActivity.playSound(explodeSound);
        }
    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        // define specifications for defaultImage
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        Rect source;
        if (moving) {
            source = move.getCurrentFrameSrc();
            params.add(new DrawParams(move.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        if (fireRocket.isPlaying()) {
            source = fireRocket.getCurrentFrameSrc();
            params.add(new DrawParams(fireRocket.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        if (explode.isPlaying()) {
            source = explode.getCurrentFrameSrc();
            params.add(new DrawParams(explode.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        return params;
    }
}
