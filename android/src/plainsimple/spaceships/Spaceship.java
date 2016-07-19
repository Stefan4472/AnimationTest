package plainsimple.spaceships;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // arrowkey direction in y
    private int dy = 0;
    private int lastdy = 0;

    // tilt of screen
    private float tiltChange;
    private float tiltThreshold = 0.01f;

    private float maxSpeedY = 0.01f;
    // values to add to speed when accelerating and "decelerating"
    private final float accelerateConst = 0.002f;
    private final float decelerate = 0.004f;

    // current tally of points scored from coins and alien hits
    private int score = 0;

    private SpriteAnimation move; // todo: resources static?
    private SpriteAnimation fireRocket;
    private SpriteAnimation explode;

    // whether user has control over spaceship
    boolean controllable;

    private boolean firesBullets;
    private int bulletType = Bullet.LASER;
    private int lastFiredBullet;
    private BitmapData bulletBitmapData;

    private boolean firesRockets;
    private int rocketType = Rocket.ROCKET;
    private int lastFiredRocket;
    private BitmapData rocketBitmapData;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles;

    // current setting: bullets or rockets
    private int firingMode = BULLET_MODE;
    private boolean shooting = false;
    public static final int BULLET_MODE = 1;
    public static final int ROCKET_MODE = 2;

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

    // convert drawable id of bullet sprite to a constant
    public void setBullets(boolean firesBullets, int drawableID) { // todo: this is confusing. Use R.drawable constants
        this.firesBullets = firesBullets;
        switch (drawableID) {
            case R.drawable.laserbullet:
                this.bulletType = Bullet.LASER;
                break;
            case R.drawable.ionbullet:
                this.bulletType = Bullet.ION;
                break;
            default:
                this.bulletType = Bullet.LASER;
                break;
        }
    }

    // convert drawable id of rocket sprite to a constant
    public void setRockets(boolean firesRockets, int drawableID) {
        this.firesRockets = firesRockets;
        switch (drawableID) {
            case R.drawable.rocket:
                this.rocketType = Rocket.ROCKET;
                break;
            default:
                this.rocketType = Rocket.ROCKET;
                break;
        }
    }

    // default constructor
    public Spaceship(BitmapData bitmapData, int x, int y) {
        super(bitmapData, x, y);
        initCraft();
    }

    private void initCraft() {
        projectiles = new ArrayList<>();
        lastFiredBullet = 0;
        lastFiredRocket = 0;
        damage = 100;
        controllable = true;
        collides = true;
        hitBox.setDimensions((int) (getWidth() * 0.66), (int) (getHeight() * 0.55));
        hitBox.setOffsets((getWidth() - hitBox.getWidth()) / 2, (getHeight() - hitBox.getHeight()) / 2);
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
        if (shooting && firingMode == BULLET_MODE && lastFiredBullet >= Bullet.getDelay(bulletType)) {
            fireBullets();
            lastFiredBullet = 0;
        }
        lastFiredRocket++;
        if (shooting && firingMode == ROCKET_MODE && lastFiredRocket >= Rocket.getDelay(rocketType)) {
            fireRockets();
            lastFiredRocket = 0;
            fireRocket.start();
        }
    }

    // fires two rockets
    public void fireRockets() {
        projectiles.add(new Rocket(rocketBitmapData, x + (int) (getWidth() * 0.80), y + (int) (0.29 * getHeight()), rocketType));
        projectiles.add(new Rocket(rocketBitmapData, x + (int) (getWidth() * 0.80), y + (int) (0.65 * getHeight()), rocketType));
        soundEffects.add(R.raw.rocket_fired);
    }

    // fires two bullets
    public void fireBullets() {
        projectiles.add(new Bullet(bulletBitmapData, x + (int) (getWidth() * 0.78), y + (int) (0.28 * getHeight()), bulletType));
        projectiles.add(new Bullet(bulletBitmapData, x + (int) (getWidth() * 0.78), y + (int) (0.66 * getHeight()), bulletType));
        soundEffects.add(R.raw.laser_fired);
    }

    // sets current tilt of device and determines dy
    public void setTiltChange(float tiltChange) {
        this.tiltChange = tiltChange;
    }

    @Override
    public void updateSpeeds() {
        // filter noise
        if(Math.abs(tiltChange) > tiltThreshold) {
            // negative is tilting away from player -> move up
            // positive is tilting toward player -> move down
            if(tiltChange > 0) {
                dy = +1;
            } else {
                dy = -1;
            }
        } else {
            dy = 0;
        }
        if (dy == 0) { // slow down
            if (speedY < 0 && speedY > -decelerate || speedY > 0 && speedY < decelerate) {
                speedY = 0;
            } else if (speedY < 0) {
                speedY += decelerate;
            } else if (speedY > 0) {
                speedY -= decelerate;
            }
        } else {
            speedY = Math.abs(speedY);
            if (speedY < maxSpeedY) {
                speedY += accelerateConst * Math.abs(tiltChange); // todo: speed a factor of tilt change
            } else if (speedY > maxSpeedY) {
                speedY = maxSpeedY;
            }
            speedY *= dy;
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

    // calculates score from bullets and rockets that may have hit aliens
    public int getAndClearScore() {
        for (Sprite s : projectiles) {
            if (s instanceof Bullet) {
                score += ((Bullet) s).getAndClearScore();
            } else if (s instanceof Rocket) {
                score += ((Rocket) s).getAndClearScore();
            }
        }
        int score_copy = score;
        score = 0;
        return score_copy;
    }

    @Override
    public void handleCollision(Sprite s) {
        hp -= s.getDamage();
        if (hp < 0 && !explode.isPlaying()) { // todo: check when explode animation has played and use for end game logic
            explode.start();
            soundEffects.add(R.raw.explosion_1);
            hp = 0;
            collision = true;
        }
        if (s instanceof Coin) {
            score += GameView.COIN_VALUE;
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
