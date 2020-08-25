package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.ColorMatrixAnimator;
import com.plainsimple.spaceships.helper.RocketManager;
import com.plainsimple.spaceships.store.ArmorType;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.store.CannonType;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.util.ImageUtil;
import com.plainsimple.spaceships.view.GameView;

import java.util.LinkedList;
import java.util.List;

import static com.plainsimple.spaceships.sprite.Spaceship.Direction.DOWN;
import static com.plainsimple.spaceships.sprite.Spaceship.Direction.UP;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    private GameContext gameContext;

    // SpriteAnimations used
    private SpriteAnimation move;
    private SpriteAnimation fireRocket;
    private SpriteAnimation explode;

    // DrawParam objects that specify how to draw the Spaceship
    private DrawImage DRAW_SHIP;
    private DrawImage DRAW_EXHAUST;
    private DrawImage DRAW_ROCKET_FIRED;
    private DrawImage DRAW_EXPLODE;

    // used to create the spaceship flash animation when hit
    private ColorMatrixAnimator colorMatrixAnimator = new ColorMatrixAnimator(3, 4, 2);

    // whether user has control over spaceship
    private boolean controllable;

    // type of cannon Spaceship has (default to CANNON_0)
    private CannonType cannonType = CannonType.CANNON_0;
    // type of rocket Spaceship has (default to ROCKET_0)
    private RocketType rocketType = RocketType.ROCKET_0;
    // type of armor Spaceship has (default to ARMOR_0
    private ArmorType armorType = ArmorType.ARMOR_0;

    // available modes for Spaceship to fire (firing cannons, rockets, or nothing)
    public enum FireMode {
        BULLET, ROCKET, NONE;
    }

    // current setting Spaceship is in (defalut to not shooting)
    private FireMode fireMode = FireMode.NONE;
    // enforces Rocket firing pattern
    private RocketManager rocketManager;
    // number of frames spaceship has been in existence (used to determine when rockets can be fired)
    private int frameCount;
    // number of frames elapsed since cannon was last fired
    private int lastFiredCannon;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles = new LinkedList<>();

    // available directions Spaceship can move in (up, down, or continue straight horizontally)
    public enum Direction {
        UP,
        DOWN,
        NONE
    }

    // Spaceship's current direction
    private Direction direction;

    // SoundIDs Spaceship uses
    private static final SoundID ROCKET_SOUND = SoundID.ROCKET;
    private static final SoundID BULLET_SOUND = SoundID.LASER;
    private static final SoundID EXPLODE_SOUND = SoundID.EXPLOSION;

    // listener interface for a classes to receive events from the Spaceship
    public interface SpaceshipListener {
        // fired when Spaceship hp changes. Passes new hp
        void onHealthChanged(int newHealth);
        // fired when Spaceship has exploded and is no longer visible (game is over)
        void onInvisible();
    }

    // listener that receives Spaceship events
    private SpaceshipListener listener;

    public Spaceship(float x, float y, GameContext gameContext) {
        super(x, y, BitmapID.SPACESHIP, gameContext);
        this.gameContext = gameContext;

        // load animations from AnimCache
        move = gameContext.getAnimCache().get(BitmapID.SPACESHIP_MOVE);
        fireRocket = gameContext.getAnimCache().get(BitmapID.SPACESHIP_FIRE);
        explode = gameContext.getAnimCache().get(BitmapID.SPACESHIP_EXPLODE);

        // init DrawParams with correct bitmap keys
        DRAW_SHIP = new DrawImage(BitmapID.SPACESHIP);
        DRAW_EXHAUST = new DrawImage(move.getBitmapID());
        DRAW_ROCKET_FIRED = new DrawImage(fireRocket.getBitmapID());
        DRAW_EXPLODE = new DrawImage(explode.getBitmapID());

        hp = armorType.getHP();
        hitBox = new FloatRect(x + getWidth() * 0.17f, y + getHeight() * 0.2f, x + getWidth() * 0.7f, y + getHeight() * 0.8f);
        rocketManager = RocketManager.newInstance(rocketType);

        collides = true;
        speedX = 0.003f;
        move.start();
        lastFiredCannon = cannonType.getDelay();
    }

    // resets spaceship to initial values
    public void reset() {
        move.reset();
        fireRocket.reset();
        explode.reset();
        collides = true;
        controllable = false;
        terminate = false;
        hp = armorType.getHP();
        if (listener != null) {
            listener.onHealthChanged(hp);
        }
        speedX = 0.003f;
        speedY = 0;
        projectiles.clear();
        move.start();
        frameCount = 0;
        lastFiredCannon = cannonType.getDelay();
        rocketManager = RocketManager.newInstance(rocketType);
    }

    // calls ImageUtil.renderSpaceship() to render a Bitmap of the Spaceship with its current
    // cannonType and rocketType
//    private Bitmap render() {
//        return ImageUtil.renderSpaceship(context, getWidth(), getHeight(), cannonType, rocketType);
//    }

    @Override
    public void updateActions() {
        lastFiredCannon++;
        frameCount++;

        // fires cannons if in correct FireMode, has waited long enough, and is still alive
        if (fireMode == FireMode.BULLET && lastFiredCannon >= cannonType.getDelay() && hp != 0) {
            fireCannons();
            lastFiredCannon = 0;
        } else if (fireMode == FireMode.ROCKET && hp != 0) {
            // queue RocketManager for permission to fire
            RocketManager.FireInstructions instructions = rocketManager.attemptFire(frameCount); // todo: force spaceship to fire on schedule in certain cases?

            // fire left if allowed and increment ROCKETS_FIRED stat
            if (instructions.fireLeft()) {
                projectiles.add(Rocket.newInstance(gameContext, x + getWidth() * 0.80f, y + 0.29f * getHeight(), rocketType));
//                GameView.currentStats.addTo(GameStats.ROCKETS_FIRED, 1);
            }
            // fire right if allowed and increment ROCKETS_FIRED stat
            if (instructions.fireRight()) {
                projectiles.add(Rocket.newInstance(gameContext, x + getWidth() * 0.80f, y + 0.65f * getHeight(), rocketType));
//                GameView.currentStats.addTo(GameStats.ROCKETS_FIRED, 1);
            }
            // play sound and start animation if at least one rocket was fired
            if (instructions.fireLeft() || instructions.fireRight()) {
                fireRocket.start();
//                GameActivity.playSound(ROCKET_SOUND);
            }
        }

        // checks if explosion has played, in which case terminate should be set to true and onInvisible() called
        if (explode.hasPlayed() && !terminate) {
            terminate = true;
            collides = false;
            if (listener != null) {
                listener.onInvisible();
            }
        }
    }

    // fires both cannons. Adds new instances of Bullet to projectiles, plays sound, and updates GameStats
    public void fireCannons() {
        projectiles.add(new Bullet(x + getWidth() * 0.78f, y + 0.28f * getHeight(), gameContext, cannonType));
        projectiles.add(new Bullet(x + getWidth() * 0.78f, y + 0.66f * getHeight(), gameContext, cannonType));
//        GameActivity.playSound(BULLET_SOUND);
//        GameView.currentStats.addTo(GameStats.CANNONS_FIRED, 2);
    }

    // updates the direction the Spaceship is moving in
    public void updateInput(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void updateSpeeds() {
        if (direction == UP) {
            speedY = -0.015f;
//            speedY = -0.02f;
        } else if (direction== DOWN){
            speedY = 0.015f;
//            speedY = 0.02f;
        } else {
            speedY /= 1.7;
        }
    }

    @Override
    public void move() {
        super.move();
        // prevent spaceship from going off-screen
//        if (y < 0) {
//            setY(0);
//        } else if (y > playScreenH - getHeight()) {
//            setY(playScreenH - getHeight());
//        }
    }

    @Override
    public void updateAnimations() {
        // update ColorMatrixAnimator
        colorMatrixAnimator.update();

        // update the other animations
        if (move.isPlaying()) {
            move.incrementFrame();
        }
        if (fireRocket.isPlaying()) {
            fireRocket.incrementFrame();
        }
        if (explode.isPlaying()) {
            explode.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        takeDamage(damage);

        if (s instanceof Coin) { // todo: play sound
//            GameView.incrementScore(GameView.COIN_VALUE);
//            GameView.currentStats.addTo(GameStats.COINS_COLLECTED, 1);
        } else {
            // trigger onHealthChanged event under proper conditions
            if (damage != 0 && !explode.isPlaying()) {
                // trigger sprite flash using ColorMatrixAnimator
                colorMatrixAnimator.flash();

                if (listener != null) {
                    Log.d("Spaceship.java", "Firing onHealthChanged");
                    listener.onHealthChanged(hp);
                }
            }

            // start explode animation under proper conditions
            if (hp == 0 && !explode.isPlaying()) {
//                GameActivity.playSound(EXPLODE_SOUND);
                explode.start();
            }
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();

        if (!explode.hasPlayed()) {
            // draw the Spaceship itself
            DRAW_SHIP.setCanvasX0(x);
            DRAW_SHIP.setCanvasY0(y);
            DRAW_SHIP.setFilter(colorMatrixAnimator.getMatrix());
            drawParams.add(DRAW_SHIP);

            // draw moving animation behind it
            DRAW_EXHAUST.setCanvasX0(x);
            DRAW_EXHAUST.setCanvasY0(y);
            DRAW_EXHAUST.setDrawRegion(move.getCurrentFrameSrc());
            DRAW_EXHAUST.setFilter(colorMatrixAnimator.getMatrix());
            drawParams.add(DRAW_EXHAUST);

            // draw fireRocket animation if it is in progress
            if (fireRocket.isPlaying()) {
                DRAW_ROCKET_FIRED.setCanvasX0(x + getWidth() / 2);
                DRAW_ROCKET_FIRED.setCanvasY0(y);
                DRAW_ROCKET_FIRED.setDrawRegion(fireRocket.getCurrentFrameSrc());
                DRAW_ROCKET_FIRED.setFilter(colorMatrixAnimator.getMatrix());
                drawParams.add(DRAW_ROCKET_FIRED);
            }
            // draw explode animation if it is in progress
            if (explode.isPlaying()) {
                DRAW_EXPLODE.setCanvasX0(x);
                DRAW_EXPLODE.setCanvasY0(y);
                DRAW_EXPLODE.setDrawRegion(explode.getCurrentFrameSrc());
                drawParams.add(DRAW_EXPLODE);
            }
        }
        return drawParams;
    }

    public List<Sprite> getProjectiles() {
        return projectiles;
    }

    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }

    // set listener to receive events
    public void setListener(SpaceshipListener listener) {
        this.listener = listener;
    }
}