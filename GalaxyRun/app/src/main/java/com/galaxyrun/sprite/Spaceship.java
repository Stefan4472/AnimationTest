package com.galaxyrun.sprite;

import com.galaxyrun.engine.AnimID;
import com.galaxyrun.engine.EventID;
import com.galaxyrun.engine.GameContext;
import com.galaxyrun.engine.GameEngine;
import com.galaxyrun.engine.UpdateContext;
import com.galaxyrun.helper.ColorMatrixAnimator;
import com.galaxyrun.helper.BitmapID;
import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.helper.SpriteAnimation;
import com.galaxyrun.util.ProtectedQueue;

import static com.galaxyrun.sprite.Spaceship.Direction.DOWN;
import static com.galaxyrun.sprite.Spaceship.Direction.UP;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // SpriteAnimations used
    private SpriteAnimation moveAnim;
    private SpriteAnimation explodeAnim;
    private SpriteAnimation shootAnim;

    // used to create the spaceship flash animation when hit
    private ColorMatrixAnimator colorMatrixAnimator = new ColorMatrixAnimator(90, 120, 60);

    // whether user has control over spaceship
    private boolean isControllable;

    // Timestamp *in game time* at which the cannons were last fired
    private long prevCannonShotTime = 0;
    // TODO: WOULD BE COOL IF THE SHOOTING DELAY DECREASED AS DIFFICULTY INCREASES
    private static final int SHOOTING_DELAY_MS = 500;
    // Is the player in the processes of shooting the cannons?
    private boolean isShooting;

    // available directions Spaceship can moveAnim in (up, down, or continue straight horizontally)
    public enum Direction {
        UP,
        DOWN,
        NONE
    }

    // Spaceship's current direction
    private Direction direction;

    // SoundIDs Spaceship uses
    private static final SoundID BULLET_SOUND = SoundID.PLAYER_SHOOT;
    private static final SoundID EXPLODE_SOUND = SoundID.PLAYER_EXPLODE;

    public Spaceship(GameContext gameContext, double x, double y) {
        super(gameContext, x, y, gameContext.bitmapCache.getData(BitmapID.SPACESHIP));

        setHealth(GameEngine.STARTING_PLAYER_HEALTH);

        // Position hitbox
        setHitboxWidth(getWidth() * 0.7);
        setHitboxHeight(getHeight() * 0.6);
        setHitboxOffsetX(getWidth() * 0.17);
        setHitboxOffsetY(getHeight() * 0.2f);

        // Load animations from AnimCache
        moveAnim = gameContext.animFactory.get(AnimID.SPACESHIP_MOVE);
        explodeAnim = gameContext.animFactory.get(AnimID.SPACESHIP_EXPLODE);
        shootAnim = gameContext.animFactory.get(AnimID.SPACESHIP_SHOOT);
        moveAnim.start();
    }

    @Override
    public int getDrawLayer() {
        return 6;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        // fires cannons if in correct FireMode, has waited long enough, and is still alive
        if (canShoot(updateContext)) {
            fireCannons(updateContext);
            prevCannonShotTime = updateContext.getGameTime().runTimeMs;
        }

        // Checks if explosion has played, in which case terminate should be set to true and onInvisible() called
        if (getState() == SpriteState.DEAD && explodeAnim.hasPlayed()) {
            setCollidable(false);
            setCurrState(SpriteState.TERMINATED);
            updateContext.createEvent(EventID.SPACESHIP_INVISIBLE);
        }
    }

    private boolean canShoot(UpdateContext updateContext) {
        double ms_since_last_shot =
                updateContext.getGameTime().runTimeMs - prevCannonShotTime;
        return isControllable &&
                isShooting &&
                (ms_since_last_shot >= SHOOTING_DELAY_MS || prevCannonShotTime == 0) &&
                getState() == SpriteState.ALIVE;
    }

    // It is assumed that the programmer has called `canShoot()` to make sure that
    // the cannons can be fired before calling this method. This method does not
    // check for validity.
    private void fireCannons(UpdateContext updateContext) {
        assert canShoot(updateContext);
        updateContext.registerSprite(new Bullet(
                gameContext,
                getX() + getWidth() * 0.78f,
                getY() + 0.28f * getHeight(),
                updateContext.difficulty
        ));
        updateContext.registerSprite(new Bullet(
                gameContext,
                getX() + getWidth() * 0.78f,
                getY() + 0.66f * getHeight(),
                updateContext.difficulty
        ));
        updateContext.createSound(BULLET_SOUND);
        updateContext.createEvent(EventID.BULLET_FIRED);
        updateContext.createEvent(EventID.BULLET_FIRED);
        shootAnim.reset();
        shootAnim.start();
    }

    // updates the direction the Spaceship is moving in
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setShooting(boolean isShooting) {
        this.isShooting = isShooting;
    }

    @Override
    public void updateSpeeds(UpdateContext updateContext) {
        // TODO: MORE NUANCED CONTROLS, WITH SIMPLE ACCELERATION/DECELLERATION. ALSO, BECOME MORE RESPONSIVE AS DIFFICULTY INCREASES
        if (isControllable) {
            if (direction == UP) {
                setSpeedY(-0.4 * gameContext.gameHeightPx);
            } else if (direction== DOWN){
                setSpeedY(0.4 * gameContext.gameHeightPx);
            } else {
                // Slow down
                setSpeedY(getSpeedY() / 1.7);
            }
        }
    }

    @Override
    public void move(UpdateContext updateContext) {
        super.move(updateContext);
        // prevent spaceship from going off-screen
        if (getY() < 0) {
            setY(0);
        } else if (getY() > gameContext.gameHeightPx - getHeight()) {
            setY(gameContext.gameHeightPx - getHeight());
        }
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {
        colorMatrixAnimator.update(updateContext.getGameTime().msSincePrevUpdate);
        if (moveAnim.isPlaying()) {
            moveAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
        if (shootAnim.isPlaying()) {
            shootAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
        if (explodeAnim.isPlaying()) {
            explodeAnim.update(updateContext.getGameTime().msSincePrevUpdate);
        }
    }

    @Override
    public void handleCollision(
            Sprite s,
            int damage,
            UpdateContext updateContext
    ) {
        if (!(s instanceof Bullet)) {
            takeDamage(damage);

            // Handle coin collision
            if (s instanceof Coin) {
                updateContext.createEvent(EventID.COIN_COLLECTED);
                updateContext.createSound(SoundID.PLAYER_COLLECT_COIN);
            }

            if (getState() == SpriteState.ALIVE && damage > 0 && getHealth() > 0) {
                // Took damage but still alive
                updateContext.createEvent(EventID.SPACESHIP_DAMAGED);
                updateContext.createSound(SoundID.PLAYER_TAKE_DAMAGE);
                colorMatrixAnimator.flash();
            }
            else if (getState() == SpriteState.ALIVE && getHealth() == 0) {
                // The damage just killed us
                updateContext.createEvent(EventID.SPACESHIP_KILLED);
                updateContext.createSound(EXPLODE_SOUND);
                explodeAnim.start();
                setCurrState(SpriteState.DEAD);
            }
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        if (!explodeAnim.hasPlayed()) {
            // Draw the Spaceship
            DrawImage drawShip = new DrawImage(
                    gameContext.bitmapCache.getBitmap(BitmapID.SPACESHIP),
                    (int) getX(),
                    (int) getY()
            );
            drawShip.setColorMatrix(colorMatrixAnimator.getMatrix());
            drawQueue.push(drawShip);

            // Draw the moving animation
            DrawImage drawExhaust = new DrawImage(
                    gameContext.bitmapCache.getBitmap(moveAnim.getBitmapID()),
                    moveAnim.getCurrentFrameSrc(),
                    (int) getX(),
                    (int) getY()
            );
            drawExhaust.setColorMatrix(colorMatrixAnimator.getMatrix());
            drawQueue.push(drawExhaust);

            // Draw the shooting animation
            if (shootAnim.isPlaying()) {
                DrawImage drawShooting = new DrawImage(
                        gameContext.bitmapCache.getBitmap(shootAnim.getBitmapID()),
                        shootAnim.getCurrentFrameSrc(),
                        (int) getX(),
                        (int) getY()
                );
                drawShooting.setColorMatrix(colorMatrixAnimator.getMatrix());
                drawQueue.push(drawShooting);
            }

            // Draw the explosion animation if it is playing
            if (explodeAnim.isPlaying()) {
                DrawImage drawExplosion = new DrawImage(
                        gameContext.bitmapCache.getBitmap(explodeAnim.getBitmapID()),
                        explodeAnim.getCurrentFrameSrc(),
                        (int) getX(),
                        (int) getY()
                );
                drawQueue.push(drawExplosion);
            }
        }
    }

    public void setControllable(boolean controllable) {
        this.isControllable = controllable;
    }
}