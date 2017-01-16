package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRect;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.helper.SpriteAnimation;

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
    private int difficulty;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnimation;
    private HealthBarAnimation healthBarAnimation;

    public Alien1(float x, float y, float scrollSpeed, Spaceship spaceship, int difficulty, Context context) {
        super(BitmapCache.getData(BitmapID.ALIEN, context), x, y);
        speedX = scrollSpeed / 2;
        this.spaceship = spaceship;

        bulletBitmapData = BitmapCache.getData(BitmapID.ALIEN_BULLET, context);
        explodeAnimation = AnimCache.get(BitmapID.SPACESHIP_EXPLODE, context);

        this.difficulty = difficulty;

        initAlien();
    }

    private void initAlien() { // todo: playability improvements. Esp. "wall of bullets"
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 10 + difficulty / 100;
        bulletDelay = 20;
        framesSinceLastBullet = bulletDelay;
        bulletSpeed = -0.002f - random.nextInt(5) / 10000.0f;
        hitBox = new Hitbox(x + getWidth() * 0.2f, y + getHeight() * 0.2f, x + getWidth() * 0.8f, y + getHeight() * 0.8f);
        damage = 50;
        healthBarAnimation = new HealthBarAnimation(x, y, getWidth(), getHeight(), hp);
        //speedX = -0.0035f;
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Alien at x = " + x);
        } else {
            framesSinceLastBullet++;
            if (x > (spaceship.getX() * 1.6) && framesSinceLastBullet >= bulletDelay) {
                if (getP(0.1f)) {
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
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.update(speedX, speedY);
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Bullet || s instanceof Rocket || s instanceof Spaceship) {
            hp -= s.damage;
            healthBarAnimation.show();
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
        drawParams.clear();
        drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        if(explodeAnimation.isPlaying()) {
            Rect source = explodeAnimation.getCurrentFrameSrc();
            drawParams.add(new DrawSubImage(explodeAnimation.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.addDrawParams(drawParams);
        }
        return drawParams;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    @Override
    public void fireBullet(Sprite s) {
        Point2D target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(bulletBitmapData, x, y + (int) (getHeight() * 0.1));
        b.setSpeedX(bulletSpeed);
        float frames_to_impact = (x - target.getX()) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }

    // private class used to draw the alien's healthbar
    // only displays when hp changes: has fade in/out animation
    private class HealthBarAnimation {

        // whether health bar is currently showing
        private boolean isShowing;
        // frame count on animation (0 if not in progress)
        private int frameCounter;
        // x-coordinate where healthbar starts
        private float healthBarX;
        // y-coordinate where healthbar starts drawing
        private float healthBarY;
        private int alienHP;
        private float healthBarWidth;
        private float healthBarHeight;
        private static final float WIDTH_RATIO = 0.9f;
        private static final float HEIGHT_RATIO = 0.2f;
        private static final float ELEVATION_RATIO = 0.1f;
        private static final int FRAMES_FADE = 6;
        private static final int FRAMES_STAY = 15;
        private static final int TOTAL_FRAMES = FRAMES_STAY + 2 * FRAMES_FADE;
        private static final int OUTLINE_COLOR = Color.GRAY;

        protected HealthBarAnimation(float x, float y, float alienWidth, float alienHeight, int alienHP) {
            healthBarWidth = alienWidth * WIDTH_RATIO;
            healthBarHeight = alienHeight * HEIGHT_RATIO;
            healthBarX = x + alienWidth * (1 - WIDTH_RATIO) / 2;
            healthBarY = y - alienHeight * (HEIGHT_RATIO + ELEVATION_RATIO);
            this.alienHP = alienHP;
        }

        // starts the animation if it hasn't been played and renews it if it is playing
        protected void show() {
            isShowing = true;
            if (frameCounter >= 0 && frameCounter < FRAMES_FADE) {
                // do nothing--keep fading in
            } else if (frameCounter >= FRAMES_FADE && frameCounter <= FRAMES_FADE + FRAMES_STAY) {
                // healthbar currently fully faded-in: reset counter to FRAMES_STAY
                frameCounter = FRAMES_FADE;
            } else if (frameCounter < TOTAL_FRAMES){ // animation was fading out: fade back in by inverting frame count
                frameCounter = Math.abs(TOTAL_FRAMES - frameCounter);
            }
        }

        protected void update(float speedX, float speedY) {
            healthBarX += speedX;
            healthBarY += speedY;
            if (frameCounter == TOTAL_FRAMES) {
                frameCounter = 0;
                isShowing = false;
            }
            if (isShowing) {
                frameCounter++;
            }
        }

        int outlineR = Color.red(OUTLINE_COLOR);
        int outlineG = Color.green(OUTLINE_COLOR);
        int outlineB = Color.blue(OUTLINE_COLOR);
        float innerPadding = healthBarHeight * 0.2f;
        // adds healthbar draw params to given list
        protected void addDrawParams(List<DrawParams> alienParams) {
            if (isShowing && frameCounter < TOTAL_FRAMES) {
                int alpha = 255;
                // fading in: calculate alpha
                if (frameCounter < FRAMES_STAY) { // todo: double-check
                    alpha = (int) (frameCounter / (float) FRAMES_STAY * 255);
                } else if (frameCounter > FRAMES_STAY + FRAMES_FADE) {
                    alpha = (int) ((TOTAL_FRAMES - frameCounter) * (255.0f / FRAMES_FADE));
                }
                int outline_color = Color.argb(alpha, outlineR, outlineG, outlineB);
                // draw healthbar outline
                alienParams.add(new DrawRect(healthBarX, healthBarY, healthBarX + healthBarWidth, healthBarY + healthBarHeight,
                        outline_color, Paint.Style.STROKE, innerPadding));
                // draw healthbar fill
                alienParams.add(new DrawRect(healthBarX + innerPadding, healthBarY + innerPadding,
                        healthBarX + healthBarWidth - innerPadding, healthBarY + healthBarHeight - innerPadding,
                        Color.GREEN, Paint.Style.FILL, innerPadding));
            }
        }

        protected boolean isShowing() {
            return isShowing;
        }
    }
}
