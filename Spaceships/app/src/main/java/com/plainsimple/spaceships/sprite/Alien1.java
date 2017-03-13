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
import com.plainsimple.spaceships.helper.GameStats;
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
        damage = 50;
        healthBarAnimation = new HealthBarAnimation(getWidth(), getHeight(), hp);
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        // terminate after explosion or if out of bounds
        if (explodeAnimation.hasPlayed() || !isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Alien at x = " + x);
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
    public void handleCollision(Sprite s) {
        if (s instanceof Bullet || s instanceof Rocket || s instanceof Spaceship) {
            hp -= s.damage;
            hp = hp < 0 ? 0 : hp;
            // check whether explodeAnimation should start
            if (hp == 0 && !explodeAnimation.isPlaying()) {
                explodeAnimation.start();
            } else {
                // start healthBarAnimation and init LoseHealthAnimations
                healthBarAnimation.start();
                loseHealthAnimations.add(new LoseHealthAnimation(getWidth(), getHeight(),
                        s.getX() - x, s.getY() - y, s.damage));
            }
            // on spaceship collision set damage to zero so it only applies damage once
            if (s instanceof Spaceship) {
                damage = 0; // todo: collides = false?
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
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.updateAndDraw(x, y, hp, drawParams);
        }
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
        // x-offset from alien x-coordinate
        private float offsetX;
        // y-offset from alien y-coordinate
        private float offsetY;
        private int maxHP;
        private int currentHP;
        // calculated healthbar dimensions (px)
        private float healthBarWidth;
        private float healthBarHeight;
        // width of healthbar outline
        private float innerPadding;
        // define width, height, and elevation of health bar relative to alien demensions
        private static final float WIDTH_RATIO = 0.8f;
        private static final float HEIGHT_RATIO = 0.1f;
        private static final float ELEVATION_RATIO = 0;
        // number of frames to fade in and stay, respectively
        private static final int FRAMES_FADE = 6;
        private static final int FRAMES_STAY = 15;
        private static final int TOTAL_FRAMES = FRAMES_STAY + 2 * FRAMES_FADE;
        // color of healthbar outline
        private static final int OUTLINE_COLOR = Color.GRAY;
        // rgb values of OUTLINE_COLOR broken down
        private final int outlineR = Color.red(OUTLINE_COLOR);
        private final int outlineG = Color.green(OUTLINE_COLOR);
        private final int outlineB = Color.blue(OUTLINE_COLOR);

        protected HealthBarAnimation(float alienWidth, float alienHeight, int alienMaxHP) {
            healthBarWidth = alienWidth * WIDTH_RATIO;
            healthBarHeight = alienHeight * HEIGHT_RATIO;
            offsetX = (alienWidth - healthBarWidth) / 2;
            offsetY = alienHeight * (ELEVATION_RATIO + HEIGHT_RATIO);
            maxHP = alienMaxHP;
            currentHP = alienMaxHP;
            innerPadding = healthBarHeight * 0.2f;
        }

        // signals the animation should start playing, or refresh if it is already playing
        protected void start() {
            if (isShowing) {
                refresh();
            }
            isShowing = true;
        }

        // refreshes the frameCount
        protected void refresh() {
            if (frameCounter >= 0 && frameCounter < FRAMES_FADE) {
                // do nothing--keep fading in
            } else if (frameCounter >= FRAMES_FADE && frameCounter <= FRAMES_FADE + FRAMES_STAY) {
                // healthbar currently fully faded-in: reset counter to FRAMES_STAY
                frameCounter = FRAMES_FADE;
            } else if (frameCounter < TOTAL_FRAMES){ // animation was fading out: fade back in by inverting frame count
                frameCounter = Math.abs(TOTAL_FRAMES - frameCounter);
            } else {
                frameCounter = 0;
            }
        }

        // updates the animation if it is playing, including shifting it to the given alien coordinates.
        // Adds the animation's DrawParams to the given list.
        protected void updateAndDraw(float alienX, float alienY, int alienHP, List<DrawParams> alienParams) {
            if (frameCounter == TOTAL_FRAMES) { // reset
                frameCounter = 0;
                isShowing = false;
            } else if (isShowing) {
                frameCounter++;
                this.currentHP = alienHP;
                // top-left drawing coordinates of healthbar
                float x0 = alienX + offsetX;
                float y0 = alienY - offsetY;
                int alpha = calculateAlpha();
                int outline_color = Color.argb(alpha, outlineR, outlineG, outlineB);
                // draw healthbar outline
                alienParams.add(new DrawRect(x0, y0, x0 + healthBarWidth, y0 + healthBarHeight,
                        outline_color, Paint.Style.STROKE, innerPadding));
                // draw healthbar fill
                float width = (healthBarWidth - 2 * innerPadding) * ((float) currentHP / maxHP);
                int fill_color = getFillColor(currentHP, maxHP, alpha);
                alienParams.add(new DrawRect(x0 + innerPadding, y0 + innerPadding,
                        x0 + innerPadding + width, y0 + healthBarHeight - innerPadding,
                        fill_color, Paint.Style.FILL, innerPadding));
            }
        }

        // calculates alpha of healthbar (for fade-in animation)
        protected int calculateAlpha() {
            // fading in: calculate alpha
            if (frameCounter < FRAMES_STAY) {
                return (int) (frameCounter / (float) FRAMES_STAY * 255);
            } else if (frameCounter > FRAMES_STAY + FRAMES_FADE) {
                return (int) ((TOTAL_FRAMES - frameCounter) * (255.0f / FRAMES_FADE));
            } else {
                return 255;
            }
        }

        // calculates fill color of healthbar based on hp and pre-calculated alpha
        protected int getFillColor(int currentHP, int maxHP, int alpha) {
            float ratio = (float) currentHP / maxHP;
            int color;
            if (ratio > 0.7f) {
                color = Color.GREEN;
            } else if (ratio > 0.3f) {
                color = Color.YELLOW;
            } else {
                color = Color.RED;
            }
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        }

        protected boolean isShowing() {
            return isShowing;
        }
    }

    // private class used to draw the alien's health floating up from it when damaged.
    // tracks with the Alien and moves relative to the alien to the right and up.
    private class LoseHealthAnimation {

        // x-offset from Alien's x-coordinate where text will be drawn
        private float offsetX;
        // y-offset from Alien's y-coordinate where text will be drawn
        private float offsetY;
        // amount of health lost, which will be displayed
        private String healthLost;
        // whether text/animation is still running/showing
        private boolean showing;
        // counts number of frames elapsed
        private int frameCounter;
        // color of text to be drawn
        private static final int TEXT_COLOR = Color.WHITE;
        // size of text to be drawn
        private static final int TEXT_SIZE = 25;
        // number of frames to display
        private static final int NUM_FRAMES = 20;
        // movement of the text in x (as fraction of Alien sprite width)
        private static final float REL_MVMNT_X = 0.4f;
        // movement of the text in y (as fraction of Alien sprite height)
        private static final float REL_MVMNT_Y = -0.4f;
        // movement of text in x and y per frame (px)
        private float frameDx, frameDy;

        // constructs and starts animation
        public LoseHealthAnimation(int alienWidth, int alienHeight, float offsetX,
                                   float offsetY, int healthLost) {
            // calculate px text should move in x and y each frame
            frameDx = alienWidth * REL_MVMNT_X / NUM_FRAMES;
            frameDy = alienHeight * REL_MVMNT_Y / NUM_FRAMES;
            // set other vars and start animation
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.healthLost = Integer.toString(healthLost);
            showing = true;
        }

        // updates the animation if it is playing, including shifting based on the updated
        // Alien coordinates. Adds the animation's DrawParams to the given list.
        protected void updateAndDraw(float alienX, float alienY, List<DrawParams> alienParams) {
            if (frameCounter == NUM_FRAMES) { // reset
                frameCounter = 0;
                showing = false;
            } else if (showing) {
                frameCounter++;
                offsetX += frameDx;
                offsetY += frameDy;
                alienParams.add(new DrawText(healthLost,
                        alienX + offsetX, alienY + offsetY, TEXT_COLOR, TEXT_SIZE));
            }
        }

        // returns whether animation has finished
        protected boolean isFinished() {
            return !showing;
        }
    }
}
