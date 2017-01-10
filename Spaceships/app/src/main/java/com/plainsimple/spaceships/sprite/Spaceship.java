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
import com.plainsimple.spaceships.view.GameView;

import java.util.LinkedList;
import java.util.List;

import static com.plainsimple.spaceships.view.GameView.screenH;
import static com.plainsimple.spaceships.view.GameView.screenW;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    private SpriteAnimation move;
    private SpriteAnimation fireRocket;
    private SpriteAnimation explode;

    // whether user has control over spaceship
    boolean controllable;

    private BulletType bulletType = BulletType.LASER;
    private int lastFiredBullet; // todo: just one variable, lastFiredProjectile?
    private BitmapData bulletBitmapData;

    private RocketType rocketType = RocketType.ROCKET;
    private int lastFiredRocket;
    private BitmapData rocketBitmapData;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles = new LinkedList<>();

    // available modes: shooting bullets, shooting rockets, or not shooting
    public enum FireMode {
        BULLET, ROCKET, NONE;
    }

    // current setting: not shooting
    private FireMode fireMode = FireMode.NONE;

    // two possible input modes: using gyroscope, or using arrow buttons
    public enum InputMode {
        GYRO, BUTTON;
    }

    // tilt of screen as reported by gyroscope (y-axis)
    private float tilt;
    private float lastTilt;
    // minimum change to register
    private final static float MIN_TILT_CHANGE = 0.01f;
    private float maxSpeedY = 0.01f;

    private int direction;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = -1;
    public static final int DIRECTION_NONE = 0;

    private SoundParams rocketSound;
    private SoundParams bulletSound;
    private SoundParams explodeSound;

    // default constructor
    public Spaceship(float x, float y, Context context) {
        super(BitmapCache.getData(BitmapID.SPACESHIP, context), x, y);

        collides = true;
        controllable = false;
        speedX = 0.003f;

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
        lastFiredRocket++;
        if (fireMode == FireMode.BULLET && lastFiredBullet >= bulletType.getDelay()) {
            fireBullets();
            lastFiredBullet = 0;
        } else if (fireMode == FireMode.ROCKET && lastFiredRocket >= rocketType.getDelay()) {
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

    public void updateInput(InputMode inputType, float value) {
        //Log.d("Spaceship", "InputReceived: " + (inputType == InputMode.GYRO ? "gyro" : "button") + " with value " + value);
        if (inputType == InputMode.GYRO) { // handle gyro input // todo: refinement
            if (Math.abs(value - tilt) >= MIN_TILT_CHANGE) {
                lastTilt = tilt;
                tilt = value;
                //Log.d("Spaceship", "Registered Tilt Change of " + (tilt - lastTilt));
            } else {
                tilt = lastTilt;
            }
        } else { // handle non-gyro input
            if ((int) value == 0) {
                speedY /= 1.7;
            } else if (value > 0) {
                speedY = -0.02f;
            } else {
                speedY = 0.02f;
            }
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
        if (s instanceof Coin) {
            GameView.incrementScore(GameView.COIN_VALUE);
        } else {
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

    public List<Sprite> getProjectiles() {
        return projectiles;
    }

    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }

    public void setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
    }

    public void setBulletType(BulletType bulletType) {
        this.bulletType = bulletType;
    }

    public void setRocketType(RocketType rocketType) {
        this.rocketType = rocketType;
    }
}
