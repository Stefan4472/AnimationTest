package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

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

    private SpriteAnimation movingAnimation; // todo: resources static?
    private SpriteAnimation fireRocketAnimation;
    private SpriteAnimation explodeAnimation;

    private Bitmap rocketBitmap;
    private Bitmap bulletBitmap;

    // whether user has control over spaceship
    boolean controllable;

    private boolean firesBullets;
    private int bulletType = Bullet.LASER;
    private long lastFiredBullet;

    private boolean firesRockets;
    private int rocketType = Rocket.ROCKET;
    private long lastFiredRocket;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles;

    // current setting: bullets or rockets
    private int firingMode = BULLET_MODE;
    private boolean shooting = false;
    public static final int BULLET_MODE = 1;
    public static final int ROCKET_MODE = 2;

    public List<Sprite> getProjectiles() { return projectiles; }
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
    public void setHP(int hp) {
        this.hp = hp;
    }
    public void setShooting(boolean shooting) { this.shooting = shooting; }
    public void setFiringMode(int firingMode) { this.firingMode = firingMode; }

    public void setBullets(boolean firesBullets, int bulletType) {
        this.firesBullets = firesBullets;
        this.bulletType = bulletType;
    }

    public void setRockets(boolean firesRockets, int rocketType) {
        this.firesRockets = firesRockets;
        this.rocketType = rocketType;
    }

    // default constructor
    public Spaceship(Bitmap defaultImage, int x, int y) {
        super(defaultImage, x, y);
        initCraft();
    }

    private void initCraft() {
        projectiles = new ArrayList<>();
        lastFiredBullet = 0;
        lastFiredRocket = 0;
        damage = 100;
        controllable = true;
        collides = true;
        hitBox.setDimensions((int) (width * 0.66), (int) (height * 0.55));
        hitBox.setOffsets((width - hitBox.getWidth()) / 2, (height - hitBox.getHeight()) / 2);
    }

    // get spritesheet bitmaps and construct them
    public void injectResources(Bitmap movingSpriteSheet, Bitmap fireRocketSpriteSheet,
                                Bitmap explodeSpriteSheet, Bitmap rocketBitmap, Bitmap bulletBitmap) { // todo: fix so dimensions are right
        movingAnimation = new SpriteAnimation(movingSpriteSheet, width, height, 5, true);
        fireRocketAnimation = new SpriteAnimation(fireRocketSpriteSheet, width, height, 8, false);
        explodeAnimation = new SpriteAnimation(explodeSpriteSheet, width, height, 5, false);
        this.rocketBitmap = rocketBitmap;
        this.bulletBitmap = bulletBitmap;
    }

    @Override
    public void updateActions() {
        if (shooting && firingMode == BULLET_MODE &&
                lastFiredBullet + Bullet.getDelay(bulletType) <= System.currentTimeMillis()) {
            fireBullets();
            lastFiredBullet = System.currentTimeMillis();
        }
        if (shooting && firingMode == ROCKET_MODE &&
                lastFiredRocket + Rocket.getDelay(rocketType) <= System.currentTimeMillis()) {
            fireRockets();
            lastFiredRocket = System.currentTimeMillis();
            fireRocketAnimation.start();
        }
    }

    // fires two rockets
    public void fireRockets() {
        projectiles.add(new Rocket(rocketBitmap, x + (int) (width * 0.86), y + (int) (0.3 * height), bulletType));
        projectiles.add(new Rocket(rocketBitmap, x + (int) (width * 0.86), y + (int) (0.66 * height), bulletType));
    }

    // fires two bullets
    public void fireBullets() {
        projectiles.add(new Bullet(bulletBitmap, x + (int) (width * 0.78), y + (int) (0.28 * height), bulletType));
        projectiles.add(new Bullet(bulletBitmap, x + (int) (width * 0.78), y + (int) (0.66 * height), bulletType));
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
        if (movingAnimation.isPlaying()) {
            movingAnimation.incrementFrame();
        }
        if (fireRocketAnimation.isPlaying()) {
            fireRocketAnimation.incrementFrame();
        }
        if (explodeAnimation.isPlaying()) {
            explodeAnimation.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        hp -= s.getDamage();
        if (hp < 0 && !explodeAnimation.isPlaying()) { // todo: check when explodeAnimation has played and use for end game logic
            explodeAnimation.start();
            hp = 0;
            collision = true;
        }
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
        if (moving) {
            canvas.drawBitmap(movingAnimation.currentFrame(), x, y, null);
        }
        if (fireRocketAnimation.isPlaying()) {
            canvas.drawBitmap(fireRocketAnimation.currentFrame(), x, y, null);
        }
        if (explodeAnimation.isPlaying()) {
            canvas.drawBitmap(explodeAnimation.currentFrame(), x, y, null);
        }
    }
}
