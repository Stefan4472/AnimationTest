package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Rect;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.view.GameView;

import java.util.LinkedList;
import java.util.List;

/**
 * An Alien is an enemy sprite that flies across the screen
 * in a sine wave. Its hp is calculated based on the difficulty,
 * and Aliens become steadily stronger as difficulty increases.
 * An Alien fires AlienBullets at the Spaceship, with this behavior
 * determined by the bulletDelay. These bullets are stored in
 * the projectiles LinkedList, which is accessible and clearable
 * via the getAndClearProjectiles() method.
 * The Alien also has a HealthBarAnimation, which
 * displays when it loses health, and additionally displays
 * LoseHealthAnimations when it loses damage.
 */
public class Alien extends Sprite {

    // frames to wait between firing bullets
    private int bulletDelay;
    // number of frames since last bullet was fired
    private int framesSinceLastBullet = 0;
    // number of bullets left alien can fire
    private int bulletsLeft;

    private List<Sprite> projectiles = new LinkedList<>();

    // frames since alien was constructed
    // used for calculating trajectory
    private int elapsedFrames = 1;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    private float startingY;

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

    public Alien(float x, float y, float scrollSpeed, Spaceship spaceship, int difficulty, Context context) {
        super(BitmapCache.getData(BitmapID.ALIEN, context), x, y);
        speedX = scrollSpeed / 2.5f;
        this.spaceship = spaceship;

        bulletBitmapData = BitmapCache.getData(BitmapID.ALIEN_BULLET, context);
        explodeAnimation = AnimCache.get(BitmapID.SPACESHIP_EXPLODE, context);

        this.difficulty = difficulty;

        initAlien();
    }

    private void initAlien() {
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 10 + difficulty / 100;
        bulletDelay = 20;
        framesSinceLastBullet = -bulletDelay;
        bulletsLeft = 4; // todo: implement AlienBullet FireManager?
        hitBox = new Hitbox(x + getWidth() * 0.2f, y + getHeight() * 0.2f, x + getWidth() * 0.8f, y + getHeight() * 0.8f);
        healthBarAnimation = new HealthBarAnimation(getWidth(), getHeight(), hp);
    }

    @Override
    public void updateActions() {
        // terminate after explosion or if out of bounds
        if (explodeAnimation.hasPlayed() || !isInBounds()) {
            terminate = true;
            GameView.currentStats.addTo(GameStats.ALIENS_KILLED, 1);
        } else {
            framesSinceLastBullet++;
            // rules for firing: alien has waited long enough, spaceship is alive, alien
            // has bullets left to fire, and alien is on right half of the screen.
            // To slightly randomize fire rate there is also only a 30% chance it will fire
            // in this frame, even if all conditions are met
            if (framesSinceLastBullet >= bulletDelay && !spaceship.terminate() && bulletsLeft > 0 && getP(0.3f)
                    && x > GameView.screenW / 2) {
                fireBullet(spaceship);
                framesSinceLastBullet = 0;
                bulletsLeft--;
            }
        }
    }

    // fires bullet at sprite with small randomized inaccuracy, based on
    // current coordinates. Bullet initialized halfway down the alien on the left side
    public void fireBullet(Sprite s) {
        projectiles.add(new AlienBullet(bulletBitmapData, x, y + (int) (getHeight() * 0.5),
                s.getHitboxCenter().getX(),
                s.getHitboxCenter().getY() + (random.nextBoolean() ? -1 : +1) * random.nextInt(50)));
    }

    @Override
    public void updateSpeeds() { // todo: comment, improve
        float projected_y;
        // if sprite in top half of screen, start flying down. Else start flying up
        if (startingY <= 150) {
            projected_y = amplitude * (float) Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        } else { // todo: flying up
            projected_y = amplitude * (float) Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        }
        speedY = (projected_y - y) / 600.0f;
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
        takeDamage(damage);
        // increment score and start HealthBarAnimation and LoseHealthAnimations
        // if Alien took damage and isn't dead.
        if (!dead && damage > 0) {
            GameView.incrementScore(damage);
            healthBarAnimation.start();
            loseHealthAnimations.add(new LoseHealthAnimation(getWidth(), getHeight(),
                    s.getX() - x, s.getY() - y, damage));
        }
        // if hp has hit zero and dead is false, set it to true.
        // This means hp has hit zero for the first time, and
        // Alien was "killed" by the collision. Start explodeAnimation.
        if (hp == 0 && !dead) {
            dead = true;
            explodeAnimation.start();
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

    public List<Sprite> getProjectiles() {
        return projectiles;
    }

    public List<Sprite> getAndClearProjectiles() {
        List<Sprite> copy = new LinkedList<>();
        for(Sprite p : projectiles) {
            copy.add(p);
        }
        projectiles.clear();
        return copy;
    }

}
