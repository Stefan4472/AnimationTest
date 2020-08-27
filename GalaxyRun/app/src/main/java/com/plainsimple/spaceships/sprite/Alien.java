package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.HealthBarAnimation;
import com.plainsimple.spaceships.helper.LoseHealthAnimation;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.util.ProtectedQueue;

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

    private static final BitmapID BITMAP_ID = BitmapID.ALIEN;

    // DrawParams to draw Alien and Explosion
    private DrawImage DRAW_ALIEN;
    private DrawImage DRAW_EXPLOSION;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnim;
    // draws animated healthbar above Alien if Alien is damaged
    private HealthBarAnimation healthBarAnimation;
    // stores any running animations showing health leaving alien
    private List<LoseHealthAnimation> loseHealthAnimations = new LinkedList<>();

    public Alien(
            int spriteId,
            double x,
            double y,
            double currDifficulty,
            GameContext gameContext
    ) {
        super(spriteId, SpriteType.ALIEN, x, y, BITMAP_ID, gameContext);
//        speedX = scrollSpeed / 2.5f;
        // TODO: NEED A WAY TO CALCULATE SPEED

        bulletBitmapData = gameContext.getBitmapCache().getData(BitmapID.ALIEN_BULLET);
        explodeAnim = gameContext.getAnimCache().get(BitmapID.SPACESHIP_EXPLODE);

        setHitboxOffsetX(getWidth() * 0.2);
        setHitboxOffsetY(getHeight() * 0.2);
        setHitboxWidth(getWidth() * 0.8);
        setHitboxHeight(getHeight() * 0.8);

        DRAW_ALIEN = new DrawImage(BITMAP_ID);
        DRAW_EXPLOSION = new DrawImage(explodeAnim.getBitmapID());

        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        // TODO: BETTER FORMULA
        setHealth((int) currDifficulty);
        bulletDelay = 20;
        framesSinceLastBullet = -bulletDelay;
        bulletsLeft = 4;
        healthBarAnimation = new HealthBarAnimation(getWidth(), getHeight(), getHealth());
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
            fireBullet(gameContext.getPlayerSprite(), updateContext);
            framesSinceLastBullet = 0;
            bulletsLeft--;
        }
    }

    private boolean shouldTerminate(UpdateContext updateContext) {
        // Terminate if dead and explosion has finished playing,
        // or if no longer visible
        // TODO: RATHER THAN CHECKING WHETHER VISIBLE, SHOULD WE CHECK IF WE'RE OFF THE LEFT OF THE SCREEN?
        return (getCurrState() == SpriteState.DEAD && explodeAnim.hasPlayed()) ||
                !isVisibleInBounds();
    }

    private boolean canFire(UpdateContext updateContext) {
        // rules for firing: alien has waited long enough, spaceship is alive, alien
        // has bullets left to fire, and alien is on right half of the screen.
        // To slightly randomize fire rate there is also only a 30% chance it will fire
        // in this frame, even if all conditions are met
        return getCurrState() == SpriteState.ALIVE &&
                framesSinceLastBullet >= bulletDelay &&
                gameContext.getPlayerSprite().isAlive() &&
                bulletsLeft > 0 &&
                random.nextFloat() <= 0.3f &&
                getX() > gameContext.getGameWidthPx() / 2;
    }

    // fires bullet at sprite with small randomized inaccuracy, based on
    // current coordinates. Bullet initialized halfway down the alien on the left side
    public void fireBullet(Sprite s, UpdateContext updateContext) {
        Point2D target_center = s.getHitbox().getCenter();
        updateContext.registerChild(gameContext.createAlienBullet(
                getX(),
                getY() + getHeight() * 0.5,
                (float) target_center.getX(),
                (float) target_center.getY() + (random.nextBoolean() ? -1 : +1) * random.nextInt(50)
        ));
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // TODO: comment, improve
//        double projected_y;
//        // if sprite in top half of screen, start flying down. Else start flying up
//        if (startingY <= 150) {
//            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
//        } else { // todo: flying up
//            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
//        }
//        speedY = (projected_y - y) / 600.0f;
        elapsedFrames++;
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        if (explodeAnim.isPlaying()) {
            explodeAnim.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        takeDamage(damage, updateContext);

        if (s.getSpriteType() == SpriteType.SPACESHIP) {
            updateContext.createEvent(EventID.ALIEN_SHOT);
        }

        // Start HealthBarAnimation and LoseHealthAnimations
        if (damage > 0) {
            healthBarAnimation.start();
            loseHealthAnimations.add(new LoseHealthAnimation(
                    getWidth(),
                    getHeight(),
                    (float) (s.getX() - getX()),
                    (float) (s.getY() - getY()),
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
        // only draw alien if it is not in last frame of explode animation
        if (explodeAnim.getFramesLeft() >= 1) {
            DRAW_ALIEN.setCanvasX0((float) getX());
            DRAW_ALIEN.setCanvasY0((float) getY());
            drawQueue.push(DRAW_ALIEN);
        }
        // Update and draw loseHealthAnimations
        for (LoseHealthAnimation anim : loseHealthAnimations) {
            if (!anim.isFinished()) {
                anim.update();
                anim.getDrawParams((float) getX(), (float) getY(), drawQueue);
            }
        }
        // update and draw healthBarAnimation if showing
        if (healthBarAnimation.isShowing()) {
            healthBarAnimation.update();
            healthBarAnimation.getDrawParams((float) getX(), (float) getY(), getHealth(), drawQueue);
        }
        // add explodeAnim params if showing
        if (explodeAnim.isPlaying()) {
            DRAW_EXPLOSION.setCanvasX0((float) getX());
            DRAW_EXPLOSION.setCanvasY0((float) getY());
            DRAW_EXPLOSION.setDrawRegion(explodeAnim.getCurrentFrameSrc());
            drawQueue.push(DRAW_EXPLOSION);
        }
    }
}
