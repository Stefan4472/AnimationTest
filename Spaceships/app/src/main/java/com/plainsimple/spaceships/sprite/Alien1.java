package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.helper.SpriteAnimation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien1 extends Alien {

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

    private Spaceship spaceship;
    private double difficulty;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnimation;

    public Alien1(BitmapData bitmapData, float x, float y, Spaceship spaceship) {
        super(bitmapData, x, y);
        Log.d("Alien Class", "Alien Initialized and inBounds = " + isInBounds());
        this.spaceship = spaceship;
        difficulty = GameActivity.getDifficulty();
        initAlien();
    }

    private void initAlien() { // todo: randomized stuff is pretty arbitrary
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 20 + (int) (difficulty / 3);
        bulletDelay = 30;
        framesSinceLastBullet = bulletDelay;
        bulletSpeed = -0.002f - random.nextInt(5) / 10000.0f;
        hitBox = new Hitbox(x + getWidth() * 0.2f, y + getHeight() * 0.2f, x + getWidth() * 0.8f, y + getHeight() * 0.8f);
        //Log.d("Alien1.java", "hitBox initialized to " + hitBox.flattenToString());
        //Log.d("Alien1.java", "Alien is at " + x + "," + y + " with dimens. " + getWidth() + "," + getHeight());
        damage = 50;
        speedX = -0.0035f;
    }

    public void injectResources(BitmapData bulletBitmapData, SpriteAnimation explodeAnimation) {
        this.bulletBitmapData = bulletBitmapData;
        this.explodeAnimation = explodeAnimation;
        //explodeAnimation = new SpriteAnimation(explodeSpriteSheet, width, height, 3, false);
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Alien at x = " + x);
        } else {
            framesSinceLastBullet++;
            if (distanceTo(spaceship) < 0.8 && framesSinceLastBullet >= bulletDelay) {
                if (getP(0.2f)) {
                    fireBullet(spaceship);
                    framesSinceLastBullet = 0;
                }
            }
        }
        /*// set collides to false after explodeAnimation has progressed five frames
        if (explodeAnimation.getFrameNumber() == 10) {
            collides = false;
        }*/
        // disappear if alien has exploded
        if(explodeAnimation.hasPlayed()) {
            collides = false;
            terminate = true;
        }
    }

    @Override
    public void updateSpeeds() {
        float projected_y;
        // if sprite in top half of screen, start flying down. Else start flying up
        if (startingY <= 150) {
            projected_y = amplitude * (float) Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        } else { // todo: flying up
            projected_y = amplitude * (float) Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        }
        speedY = (projected_y - y) / 600.0f; // todo: more elegant
        elapsedFrames++;
    }

    @Override
    public void updateAnimations() {
        if (explodeAnimation.isPlaying()) {
            explodeAnimation.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Bullet || s instanceof Rocket || s instanceof Spaceship) {
            hp -= s.damage;
            if (hp < 0 && !explodeAnimation.isPlaying()) {
                explodeAnimation.start();
            }
            // on spaceship collision set damage to zero so it only applies damage once
            if (s instanceof Spaceship) {
                damage = 0;
            }
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        List<DrawParams> params = new LinkedList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        if(explodeAnimation.isPlaying()) {
            Rect source = explodeAnimation.getCurrentFrameSrc();
            params.add(new DrawParams(explodeAnimation.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        return params;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    @Override
    public void fireBullet(Sprite s) {
        Log.d("Alien1 Class", "Firing on target at " + s.getHitboxCenter().getX() + "," + s.getHitboxCenter().getY());
        Point2D target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(bulletBitmapData, x, y + (int) (getHeight() * 0.1));
        b.setSpeedX(bulletSpeed);
        float frames_to_impact = (x - target.getX()) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        Log.d("Alien1 Class", "Setting speedY to " + ((y - target.getY()) / frames_to_impact));
        projectiles.add(b);
    }
}
