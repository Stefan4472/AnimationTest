package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

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
    private double tilt;
    private double lastTilt;
    private double tiltThreshold = 0.9;

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
    private boolean firingBullets = false;

    private boolean firesRockets;
    private int rocketType = Rocket.ROCKET;
    private long lastFiredRocket;
    private boolean firingRockets = false;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles;

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
    public void setFiringBullets(boolean firingBullets) { this.firingBullets = firingBullets; }
    public void setFiringRockets(boolean firingRockets) { this.firingRockets = firingRockets; }

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

    public void updateActions() {
        if (firingBullets && lastFiredBullet + Bullet.getDelay(bulletType) <= System.currentTimeMillis()) {
            fireBullets();
            lastFiredBullet = System.currentTimeMillis();
        }

        if (firingRockets && lastFiredRocket + Rocket.getDelay(rocketType) <= System.currentTimeMillis()) {
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
    public void setTilt(double tilt) {
        lastTilt = this.tilt;
        this.tilt = tilt;
        lastdy = dy;
        double tilt_change = tilt - lastTilt;
        if(Math.abs(tilt_change) > tiltThreshold) {
            if (tilt_change < 0) {
                dy = -1;
            } else if (tilt_change > 0) {
                dy = +1;
            }
        } else {
            dy = 0;
        }
    }
    public void updateSpeeds() {
        if(dy == lastdy) {
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
                    speedY += accelerateConst * Math.abs((tilt - lastTilt)); // todo: speed a factor of tilt change
                } else if (speedY > maxSpeedY) {
                    speedY = maxSpeedY;
                }
                speedY *= dy;
            }
        }
    }

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
            canvas.drawBitmap(movingAnimation.nextFrame(), x, y, null);
        }
        if (fireRocketAnimation.isPlaying()) {
            canvas.drawBitmap(fireRocketAnimation.nextFrame(), x, y, null);
        }
        if (explodeAnimation.isPlaying()) {
            canvas.drawBitmap(explodeAnimation.nextFrame(), x, y, null);
        }
    }
}
