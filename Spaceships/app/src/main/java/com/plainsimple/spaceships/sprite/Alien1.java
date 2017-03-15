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
import com.plainsimple.spaceships.helper.DrawText;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.view.GameView;

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
    private int difficulty;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnimation;
    // draws animated healthbar above Alien if Alien is damaged
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing health leaving alien
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

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
        healthBarAnimation = new HealthBarAnimation(getWidth(), getHeight(), hp);
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        // terminate after explosion or if out of bounds
        if (explodeAnimation.hasPlayed() || !isInBounds()) {
            terminate = true;
//            Log.d("Termination", "Removing Alien at x = " + x);
            GameView.currentStats.addTo(GameStats.ALIENS_KILLED, 1);
        } else {
            framesSinceLastBullet++;
            if (x > (spaceship.getX() * 1.6) && framesSinceLastBullet >= bulletDelay) {
                if (getP(0.1f)) {
                    fireBullet(spaceship);
                    framesSinceLastBullet = 0;
                }
            }
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
    public void handleCollision(Sprite s, int damage) {
        if (s instanceof Bullet || s instanceof Rocket || s instanceof Spaceship) {
            GameView.incrementScore(damage);
            // check whether explodeAnimation should start
            if (hp == 0 && !explodeAnimation.isPlaying()) {
                explodeAnimation.start();
            } else if (damage > 0) {
                // start healthBarAnimation and init LoseHealthAnimations
                healthBarAnimation.start();
                loseHealthAnimations.add(new LoseHealthAnimation(getWidth(), getHeight(),
                        s.getX() - x, s.getY() - y, damage));
            }
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        // only draw alien if it is not in last frame of explode animation
        if (explodeAnimation.getFramesLeft() >= 1) { // todo: does this work?
            drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        }
        // draw loseHealthAnimations
        for (int i = 0; i < loseHealthAnimations.size(); i++) {
            if (!loseHealthAnimations.get(i).isFinished()) {
                loseHealthAnimations.get(i).updateAndDraw(x, y, drawParams);
            }
        }
        // update and draw healthBarAnimation if showing
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.updateAndDraw(x, y, hp, drawParams);
        }
        // add explodeAnimation params if showing
        if (explodeAnimation.isPlaying()) {
            Rect source = explodeAnimation.getCurrentFrameSrc();
            drawParams.add(new DrawSubImage(explodeAnimation.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
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
        float frames_to_impact = (b.getHitboxCenter().getX() - target.getX()) / bulletSpeed;
        b.setSpeedY((b.getHitboxCenter().getY() - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }
}
