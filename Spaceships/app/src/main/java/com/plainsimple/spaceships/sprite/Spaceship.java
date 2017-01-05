package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.BulletType;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.RawResource;
import com.plainsimple.spaceships.helper.RocketType;
import com.plainsimple.spaceships.helper.SoundParams;
import com.plainsimple.spaceships.helper.SpriteAnimation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.plainsimple.spaceships.view.GameView.screenH;
import static com.plainsimple.spaceships.view.GameView.screenW;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // tilt of screen as reported by gyroscope (y-axis)
    private float tilt;
    private float lastTilt;
    // minimum change to register
    private final static float MIN_TILT_CHANGE = 0.01f;
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

    private boolean firesRockets; // todo: this should be established in GameActivity, which should only provide the buttons if this is the case
    private RocketType rocketType = RocketType.ROCKET;
    private int lastFiredRocket;
    private BitmapData rocketBitmapData;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles = new LinkedList<>();

    // current setting: bullets or rockets
    private int firingMode = BULLET_MODE;
    private boolean shooting = false;
    public static final int BULLET_MODE = 1;
    public static final int ROCKET_MODE = 2;

    private SoundParams rocketSound;
    private SoundParams bulletSound;
    private SoundParams explodeSound;

    public List<Sprite> getProjectiles() {
        return projectiles;
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
    public Spaceship(float x, float y, Context context) {
        super(BitmapCache.getData(BitmapID.SPACESHIP, context), x, y);

        collides = true;

        move = AnimCache.get(BitmapID.SPACESHIP_MOVE, context);
        move.start();
        fireRocket = AnimCache.get(BitmapID.SPACESHIP_FIRE, context);
        explode = AnimCache.get(BitmapID.SPACESHIP_EXPLODE, context);
        bulletBitmapData = BitmapCache.getData(BitmapID.LASER_BULLET, context);
        rocketBitmapData = BitmapCache.getData(BitmapID.ROCKET, context);

        hitBox = new Hitbox(x + getWidth() * 0.17f, y + getHeight() * 0.22f, x + getWidth() * 0.83f, y + getHeight() * 0.78f);

        bulletSound = new SoundParams(RawResource.LASER, 1.0f, 1.0f, 1, 0, 1.0f);
        rocketSound = new SoundParams(RawResource.ROCKET, 1.0f, 1.0f, 1, 0, 1.0f);
        explodeSound = new SoundParams(RawResource.EXPLOSION, 1.0f, 1.0f, 1, 0, 1.0f);
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
        } else {
            tilt = lastTilt;
        }
    }

    @Override
    public void updateSpeeds() {
        // negative is tilting away from player -> move up
        // positive is tilting toward player -> move down
       // float tiltChange = tilt - lastTilt;
        //speedY = tiltChange / 10.0f;
        //Log.d("Spaceship.java", "Tilt is " + tilt + " and change is " + tiltChange);
    }

    @Override
    public void move() {
        super.move();
        // for when spaceship first comes on to screen
        if (x < screenW / 4) { // todo: local variable
            setControllable(false);
            setSpeedX(0.003f);
        } else {
            setX(screenW / 4);
            setSpeedX(0.0f);
            setControllable(true);
        }
        // prevent spaceship from going off-screen
        if (y < 0) {
            setY(0);
        } else if (y > screenH - getHeight()) {
            setY(screenH - getHeight());
        }
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
        // handling damage this way prevents errors
        if (hp < s.getDamage()) {
            hp = -1; // debug purposes: normally would be zero
        } else {
            hp -= s.getDamage();
        }
        Log.d("Spaceship class", "Collided with " + (s instanceof Alien ? "alien" : "sprite") + " at " + s.getX());
        if (hp < 0 && !explode.isPlaying()) { // todo: set hp <= 0 (currently < 0 for debug)
            // todo: check when explode animation has played and use for end game logic
            explode.start();
            GameActivity.playSound(explodeSound);
            hp = 0; // todo: debug purposes
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        // define specifications for defaultImage
        drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        if (moving) {
            drawParams.add(new DrawSubImage(move.getBitmapID(), x, y, move.getCurrentFrameSrc()));
        }
        if (fireRocket.isPlaying()) {
            drawParams.add(new DrawSubImage(fireRocket.getBitmapID(), x, y, fireRocket.getCurrentFrameSrc()));
        }
        if (explode.isPlaying()) {
            drawParams.add(new DrawSubImage(explode.getBitmapID(), x, y, explode.getCurrentFrameSrc()));
        }
        return drawParams;
    }
}
