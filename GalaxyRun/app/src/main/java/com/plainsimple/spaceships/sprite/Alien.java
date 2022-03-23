package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.AnimID;
import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawImage;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Iterator;
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

    // frames since alien was constructed
    // used for calculating trajectory
    private int elapsedFrames = 1;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    private double startingY;

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnim;
    // draws animated healthbar above Alien if Alien is damaged
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing health leaving alien
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Alien(
            GameContext gameContext,
            double x,
            double y,
            double currDifficulty
    ) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.ALIEN));
//        speedX = scrollSpeed / 2.5f;
        // TODO: NEED A WAY TO CALCULATE SPEED

        bulletBitmapData = gameContext.bitmapCache.getData(BitmapID.ALIEN_BULLET);
        explodeAnim = gameContext.animFactory.get(AnimID.ALIEN_EXPLODE);

        setHitboxOffsetX(getWidth() * 0.2);
        setHitboxOffsetY(getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);

        startingY = y;
        amplitude = 70 + gameContext.rand.nextInt(60);
        period = 250 + gameContext.rand.nextInt(100);
        vShift = gameContext.rand.nextInt(20);
        hShift = -gameContext.rand.nextInt(3);
        // TODO: BETTER FORMULA
        setHealth((int) (currDifficulty * 30));
        bulletDelay = 20;
        framesSinceLastBullet = -bulletDelay;
        bulletsLeft = 4;
        healthBarAnimation = new HealthBarAnimation(
                gameContext.gameWidthPx,
                gameContext.gameHeightPx,
                getWidth(),
                getHeight(),
                getHealth()
        );
    }

    @Override
    public int getDrawLayer() {
        return 5;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        framesSinceLastBullet++;

        if (shouldTerminate(updateContext)) {
            updateContext.createEvent(EventID.ALIEN_DIED);
            setCurrState(SpriteState.TERMINATED);
        }

        if (canFire(updateContext)) {
            updateContext.createEvent(EventID.ALIEN_FIRED_BULLET);
            updateContext.createSound(SoundID.ALIEN_FIRED_BULLET);
            fireBullet(updateContext.playerSprite, updateContext);
            framesSinceLastBullet = 0;
            bulletsLeft--;
        }
    }

    private boolean shouldTerminate(UpdateContext updateContext) {
        // Terminate if dead and explosion has finished playing,
        // or if no longer visible
        // TODO: RATHER THAN CHECKING WHETHER VISIBLE, SHOULD WE CHECK IF WE'RE OFF THE LEFT OF THE SCREEN?
        return (getCurrState() == SpriteState.DEAD && explodeAnim.hasPlayed()) || getX() < 0;
    }

    private boolean canFire(UpdateContext updateContext) {
        // rules for firing: alien has waited long enough, spaceship is alive, alien
        // has bullets left to fire, and alien is on right half of the screen.
        // To slightly randomize fire rate there is also only a 30% chance it will fire
        // in this frame, even if all conditions are met
        return getCurrState() == SpriteState.ALIVE &&
                framesSinceLastBullet >= bulletDelay &&
                updateContext.playerSprite.isAlive() &&
                bulletsLeft > 0 &&
                gameContext.rand.nextFloat() <= 0.3f &&
                getX() > gameContext.gameWidthPx / 2.0;
    }

    // fires bullet at sprite with small randomized inaccuracy, based on
    // current coordinates. Bullet initialized halfway down the alien on the left side
    public void fireBullet(Sprite s, UpdateContext updateContext) {
        Point2D target_center = s.getHitbox().getCenter();
        updateContext.registerSprite(new AlienBullet(
                gameContext,
                getX(),
                getY() + getHeight() * 0.5,
                (float) target_center.getX(),
                (float) target_center.getY() + (gameContext.rand.nextBoolean() ? -1 : +1) * gameContext.rand.nextInt(50)
        ));
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // TODO: comment, improve
//        double projected_y;
//        // if sprite in top half of screen, start flying down. Else start flying up
//        if (startingY <= gameContext.gameWidthPx / 2.0) {
//            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
//        } else { // todo: flying up
//            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
//        }
//        setSpeedY((projected_y - getY()) / 600);
//        elapsedFrames++;
        setSpeedX(-updateContext.scrollSpeedPx / 3);
        setSpeedY(0);
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        if (explodeAnim.isPlaying()) {
            explodeAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }

        // Update LoseHealthAnimations
        Iterator<LoseHealthAnimation> health_anims = loseHealthAnimations.iterator();
        while(health_anims.hasNext()) {
            LoseHealthAnimation anim = health_anims.next();
            // Remove animation if finished
            if (anim.isFinished()) {
                health_anims.remove();
            } else {  // Update animation
                anim.update(updateContext.getGameTime().msSincePrevUpdate);

            }
        }

        // Update HealthbarAnimation
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.update(updateContext.getGameTime().msSincePrevUpdate);
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        takeDamage(damage, updateContext);

        if (s instanceof Spaceship) {
            updateContext.createEvent(EventID.ALIEN_SHOT);
        }

        // Start HealthBarAnimation and LoseHealthAnimations
        if (damage > 0) {
            healthBarAnimation.setHealth(getHealth());
            healthBarAnimation.start();

            loseHealthAnimations.add(new LoseHealthAnimation(
                    gameContext.gameWidthPx,
                    gameContext.gameHeightPx,
                    s.getX() - getX(),
                    s.getY() - getY(),
                    damage
            ));
        }
    }

    @Override
    public void die(UpdateContext updateContext) {
        explodeAnim.start();
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // Draw alien, unless it is exploding and in the last frame of the explosion animation
        if (!(explodeAnim.isPlaying() && explodeAnim.getFramesLeft() <= 1)) {
            drawQueue.push(new DrawImage(BitmapID.ALIEN, (float) getX(), (float) getY()));
        }
        // Draw loseHealthAnimations
        for (LoseHealthAnimation anim : loseHealthAnimations) {
            anim.getDrawParams(getX(), getY(), drawQueue);
        }
        // Draw healthBarAnimation if showing
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.getDrawParams(getX(), getY(), getHealth(), drawQueue);
        }
        // add explodeAnim params if showing
        if (explodeAnim.isPlaying()) {
            DrawImage explodeImg = new DrawImage(explodeAnim.getBitmapID(), (float) getX(), (float) getY());
            explodeImg.setDrawRegion(explodeAnim.getCurrentFrameSrc());
            drawQueue.push(explodeImg);
        }
    }
}
