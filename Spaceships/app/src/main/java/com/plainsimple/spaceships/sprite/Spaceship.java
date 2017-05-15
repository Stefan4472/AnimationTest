package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
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

import static com.plainsimple.spaceships.view.GameView.playScreenH;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    private Context context;

    private SpriteAnimation move;
    private SpriteAnimation fireRocket;
    private SpriteAnimation explode;

    // DrawParam objects that specify how to draw the Spaceship
    private DrawImage DRAW_SHIP;
    private DrawImage DRAW_EXHAUST;
    private DrawImage DRAW_ROCKET_FIRED;
    private DrawImage DRAW_EXPLODE;
    // key used to register the image of the rendered spaceship in BitmapCache
    private String RENDERED_BMP_KEY = "SPACESHIP_RENDERED";

    // used to create the spaceship flash animation when hit
    private ColorMatrixAnimator colorMatrixAnimator = new ColorMatrixAnimator(3, 4, 2);

    // whether user has control over spaceship
    private boolean controllable;

    private CannonType cannonType = CannonType.CANNON_0;
    private int lastFiredBullet;

    // type of rocket spaceship fires
    private RocketType rocketType = RocketType.ROCKET_0;
    // number of frames spaceship has been in existence
    // (used to determine when rockets can be fired)
    private int frameCount;
    // enforces Rocket firing pattern
    private RocketManager rocketManager;

    private ArmorType armorType = ArmorType.ARMOR_0;

    // keeps track of fired bullets and rockets
    private List<Sprite> projectiles = new LinkedList<>();

    // available modes: shooting bullets, shooting rockets, or not shooting
    public enum FireMode {
        BULLET, ROCKET, NONE;
    }

    // current setting: not shooting
    private FireMode fireMode = FireMode.NONE;

    private int direction;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = -1;
    public static final int DIRECTION_NONE = 0;

    private static final SoundID ROCKET_SOUND = SoundID.ROCKET;
    private static final SoundID BULLET_SOUND = SoundID.LASER;
    private static final SoundID EXPLODE_SOUND = SoundID.EXPLOSION;

    public interface SpaceshipListener {
        // fired when Spaceship hp changes. Passes new hp
        void onHealthChanged(int newHealth);
        // fired when Spaceship is no longer visible and game is over
        void onInvisible();
    }

    // listener that receives Spaceship events
    private SpaceshipListener listener;

    // default constructor
    public Spaceship(float x, float y, Context context) {// todo: clean up
        super(x, y, BitmapCache.getData(BitmapID.SPACESHIP, context));
        move = AnimCache.get(BitmapID.SPACESHIP_MOVE, context);
        fireRocket = AnimCache.get(BitmapID.SPACESHIP_FIRE, context);
        explode = AnimCache.get(BitmapID.SPACESHIP_EXPLODE, context);

        hitBox = new FloatRect(x + getWidth() * 0.17f, y + getHeight() * 0.27f, x + getWidth() * 0.7f, y + getHeight() * 0.73f);

        // render spaceship using base image and default cannon/rocket types
        Bitmap rendered = renderSpaceship(context, getWidth(), getHeight(), CannonType.CANNON_0, RocketType.ROCKET_0);
        // register rendered image under RENDERED_BMP_KEY in BitmapCache.java
        BitmapCache.putBitmap(RENDERED_BMP_KEY, rendered);

        // init DrawParams with correct bitmap keys
        DRAW_SHIP = new DrawImage(RENDERED_BMP_KEY);
        DRAW_EXHAUST = new DrawImage(move.getBitmapID());
        DRAW_ROCKET_FIRED = new DrawImage(fireRocket.getBitmapID());
        DRAW_EXPLODE = new DrawImage(explode.getBitmapID());

        setInitValues(); // todo: reset() method, or something more elegant

        this.context = context;
    }

    // sets spaceship fields to initial values
    // used when spaceship is first constructed and when it is reset
    public void setInitValues() {
        collides = true;
        hp = armorType.getHP();
        if (listener != null) {
            listener.onHealthChanged(hp);
        }
        rocketManager = RocketManager.newInstance(rocketType);
        controllable = false;
        speedX = 0.003f;
        speedY = 0;
        move.reset();
        fireRocket.reset();
        explode.reset();
        move.start();
        projectiles.clear();
        lastFiredBullet = cannonType.getDelay();
        frameCount = 0;
    }

    // renders spaceship bitmap from modular components, scaled to given width/height
    // loads and draws R.id.spaceship_base, then overlays with the correct cannon overlay
    // (specified by cannonType) and finally with correct rocket overlay (specified by
    // rocketType). Returns the final bitmap.
    public static Bitmap renderSpaceship(Context context, int width, int height,
                                         CannonType cannonType, RocketType rocketType) {
        Bitmap rendered = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rendered);
        Bitmap base = ImageUtil.decodeAndScaleTo(context, BitmapID.SPACESHIP_BASE.getrId(), width, height);
        canvas.drawBitmap(base, 0, 0, null);
        Bitmap cannons = ImageUtil.decodeAndScaleTo(context, cannonType.getSpaceshipOverlayId().getrId(), width, height);
        canvas.drawBitmap(cannons, 0, 0, null);
        Bitmap rockets = ImageUtil.decodeAndScaleTo(context, rocketType.getSpaceshipOverlayId().getrId(), width, height);
        canvas.drawBitmap(rockets, 0, 0, null);
        return rendered;
    }

    @Override
    public void updateActions() {
        lastFiredBullet++;
        frameCount++;
        if (fireMode == FireMode.BULLET && lastFiredBullet >= cannonType.getDelay() && hp != 0) {
            fireCannons();
            lastFiredBullet = 0;
        } else if (fireMode == FireMode.ROCKET && hp != 0) { // todo: clean up?
            // queue RocketManager for permission to fire
            RocketManager.FireInstructions instructions = rocketManager.attemptFire(frameCount); // todo: force spaceship to fire on schedule in certain cases
            if (instructions.fireLeft()) { // fire left if allowed
                projectiles.add(Rocket.newInstance(context, x + getWidth() * 0.80f, y + 0.29f * getHeight(), rocketType));
            }
            if (instructions.fireRight()) { // fire right if allowed
                projectiles.add(Rocket.newInstance(context, x + getWidth() * 0.80f, y + 0.65f * getHeight(), rocketType));
                GameView.currentStats.addTo(GameStats.ROCKETS_FIRED, 1);
            }
            // play sound and start animation if at least one rocket was fired
            if (instructions.fireLeft() || instructions.fireRight()) {
                fireRocket.start();
                GameActivity.playSound(ROCKET_SOUND);
                GameView.currentStats.addTo(GameStats.ROCKETS_FIRED, 1);
            }
        }
        // make spaceship invisible and undetectable
        if (explode.hasPlayed() && !terminate) {
            terminate = true;
            collides = false;
            if (listener != null) {
                listener.onInvisible();
            }
        }
    }

    // fires two bullets
    public void fireCannons() {
        projectiles.add(new Bullet(x + getWidth() * 0.78f, y + 0.28f * getHeight(), context, cannonType));
        projectiles.add(new Bullet(x + getWidth() * 0.78f, y + 0.66f * getHeight(), context, cannonType));
        GameActivity.playSound(BULLET_SOUND);
        GameView.currentStats.addTo(GameStats.CANNONS_FIRED, 2);
    }

    public void updateInput(float value) {
        if (value == DIRECTION_UP) {
            speedY = -0.02f;
        } else if (value == DIRECTION_DOWN){
            speedY = 0.02f;
        } else {
            speedY /= 1.7;
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void move() {
        super.move();
        // prevent spaceship from going off-screen
        if (y < 0) {
            setY(0);
        } else if (y > playScreenH - getHeight()) {
            setY(playScreenH - getHeight());
        }
    }

    @Override
    public void updateAnimations() {
        // update ColorMatrixAnimator
        colorMatrixAnimator.update();

        // update the animations
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
            GameView.incrementScore(GameView.COIN_VALUE);
            GameView.currentStats.addTo(GameStats.COINS_COLLECTED, 1);
        } else {
            // trigger onHealthChanged event under proper conditions
            if (damage != 0 && !explode.isPlaying() && listener != null) {
                listener.onHealthChanged(hp);
                Log.d("Spaceship.java", "Firing onHealthChanged");
            }

            // trigger sprite flash using ColorMatrixAnimator
            colorMatrixAnimator.flash();

            // start explode animation under proper conditions
            if (hp == 0 && !explode.isPlaying()) {
                GameActivity.playSound(EXPLODE_SOUND);
                explode.start();
            }
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        if (!explode.hasPlayed()) {
            DRAW_SHIP.setCanvasX0(x);
            DRAW_SHIP.setCanvasY0(y);
            DRAW_SHIP.setFilter(colorMatrixAnimator.getMatrix());
            drawParams.add(DRAW_SHIP);

            // draw moving animation
            DRAW_EXHAUST.setCanvasX0(x);
            DRAW_EXHAUST.setCanvasY0(y);
            DRAW_EXHAUST.setDrawRegion(move.getCurrentFrameSrc());
            DRAW_EXHAUST.setFilter(colorMatrixAnimator.getMatrix());
            drawParams.add(DRAW_EXHAUST);

            if (fireRocket.isPlaying()) {
                DRAW_ROCKET_FIRED.setCanvasX0(x + getWidth() / 2);
                DRAW_ROCKET_FIRED.setCanvasY0(y);
                DRAW_ROCKET_FIRED.setDrawRegion(fireRocket.getCurrentFrameSrc());
                DRAW_ROCKET_FIRED.setFilter(colorMatrixAnimator.getMatrix());
                drawParams.add(DRAW_ROCKET_FIRED);
            }
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

    public void setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
    }

    // set CannonType and update rendered image in BitmapCache
    public void setCannonType(CannonType cannonType) {
        this.cannonType = cannonType;
        // render spaceship using base image and default cannon/rocket types
        Bitmap rendered = renderSpaceship(context, getWidth(), getHeight(), cannonType, rocketType);
        // register rendered image under RENDERED_BMP_KEY in BitmapCache.java
        BitmapCache.putBitmap(RENDERED_BMP_KEY, rendered);
    }

    // set RocketType, get correct RocketManager, and update rendered image in BitmapCache
    public void setRocketType(RocketType rocketType) {
        this.rocketType = rocketType;
        rocketManager = RocketManager.newInstance(rocketType);
        // render spaceship using base image and default cannon/rocket types
        Bitmap rendered = renderSpaceship(context, getWidth(), getHeight(), cannonType, rocketType);
        // register rendered image under RENDERED_BMP_KEY in BitmapCache.java
        BitmapCache.putBitmap(RENDERED_BMP_KEY, rendered);
    }

    // set ArmorType and corresponding hp
    public void setArmorType(ArmorType armorType) {
        this.armorType = armorType;
        hp = armorType.getHP();
    }

    // set listener to receive events
    public void setListener(SpaceshipListener listener) {
        this.listener = listener;
    }

    // class used by the Spaceship to animate its ColorMatrix. Currently the only supported animation
    // is a flash, where every pixel's Red/Green/Blue values are briefly jacked up to 255 before coming
    // back down to what they originally were.
    // The ColorMatrix uses a 20-element float[], visualized as a 4x5 matrix. The 5th column of each row
    // tells by how much to increase each Red/Green/Blue/Alpha value. So, to transition to full white,
    // we want to change elems 4, 9, and 14 to 255, before bringing them back down to zero.
    private class ColorMatrixAnimator {

        // used to count frames in an animation sequence
        private int frameCount;
        // whether the animation is currently running
        private boolean flashing;
        // number of frames it takes for sprite to reach completely white
        private int flashIn;
        // number of frames sprite will stay completely white
        private int flashStay;
        // number of frames it takes for sprite to go back to normal after being
        // completely white.
        private int flashOut;
        // total frame count for an animation
        private int totalFrames;
        // current calculated vals for ColorMatrix
        private float[] currentVals;
        // the actual ColorMatrix
        private ColorMatrix colorMatrix = new ColorMatrix();

        public ColorMatrixAnimator(int flashIn, int flashStay, int flashOut) {
            // set params
            this.flashIn = flashIn;
            this.flashStay = flashStay;
            this.flashOut = flashOut;
            totalFrames = flashIn + flashStay + flashOut;
            // set currentVals to default
            currentVals = colorMatrix.getArray();
        }

        // begins a flash animation sequence
        public void flash() {
            // if not currently flashing, set flashing to true and reset frameCount
            if (!flashing) {
                flashing = true;
                frameCount = 0;
            }
        }

        // updates the matrix by one frame
        public void update() {
            // check if frameCount has hit total frames, in which case stop flashing
            if (frameCount == totalFrames) {
                flashing = false;
            }
            // if currently flashing, update the matrix
            if (flashing) {
                frameCount++;
                int add_const = 0;
                // determine value to add to color chanel. We want it to go up to 255 over flashIn,
                // stay at 255 during flashStay, and go back to zero over flashOut
                if (frameCount <= flashIn) {
                    add_const = 255 / flashIn;
                } else if (frameCount > flashIn + flashStay) {
                    add_const = -255 / flashOut;
                }
                // update the 5th column of each color value
                for (int i = 0; i < 3; i++) {
                    currentVals[4 + 5 * i] += add_const;
                }
                // update the matrix
                colorMatrix.set(currentVals);
            }
        }

        // returns ColorMatrix with calculated values
        public ColorMatrix getMatrix() {
            return colorMatrix;
        }
    }
}