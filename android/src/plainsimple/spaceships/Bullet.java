package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    // number of frames to delay between fired bullets
    private int delay;

    // points awarded for scoring a hit
    private int score;

    // bulletType defines bullet damage and sprite // todo: use R.drawable constants
    public final static int LASER = 1;
    public final static int ION = 2;
    public final static int PLASMA = 3;
    public final static int PLUTONIUM = 4;

    // laser bullet constants
    public static final int LASER_DAMAGE = 10;
    public static final int LASER_DELAY = 12;
    public static final float LASER_SPEED_X = 0.01f;

    // ion bullet constants
    public static final int ION_DAMAGE = 20;
    public static final int ION_DELAY = 10;
    public static final float ION_SPEED_X = 0.011f;

    // plasma bullet constants
    public static final int PLASMA_DAMAGE = 30;
    public static final int PLASMA_DELAY = 8;
    public static final float PLASMA_SPEED_X = 0.012f;

    // plutonium bullet constants
    public static final int PLUTONIUM_DAMAGE = 40;
    public static final int PLUTONIUM_DELAY = 18;
    public static final float PLUTONIUM_SPEED_X = 0.013f;

    // todo: when resources can be different defaultImage shouldn't be a parameter
    public Bullet(int defaultImageID, int spriteWidth, int spriteHeight, float x, float y, int bulletType) {
        super(defaultImageID, spriteWidth, spriteHeight, x, y);
        hitBox.setDimensions((int) (width * 1.5f), height);
        switch(bulletType) {
            case LASER: // todo: figure out how to load resource in each case
                damage = LASER_DAMAGE;
                delay = LASER_DELAY;
                speedX = LASER_SPEED_X;
                break;
            case ION:
                damage = ION_DAMAGE;
                delay = ION_DELAY;
                speedX = ION_SPEED_X;
                break;
            case PLASMA:
                damage = PLASMA_DAMAGE;
                delay = PLASMA_DELAY;
                speedX = PLASMA_SPEED_X;
                break;
            case PLUTONIUM:
                damage = PLUTONIUM_DAMAGE;
                delay = PLUTONIUM_DELAY;
                speedX = PLUTONIUM_SPEED_X;
                break;
            default:
                System.out.println("Invalid bulletType (" + bulletType + ")");
                break;
        }
    }

    // returns delay (frames) given bulletType
    public static int getDelay(int bulletType) {
        switch(bulletType) {
            case LASER:
                return LASER_DELAY;
            case ION:
                return ION_DELAY;
            case PLASMA:
                return PLASMA_DELAY;
            case PLUTONIUM:
                return PLUTONIUM_DELAY;
            default:
                return -1;
        }
    }

    // returns score and resets it to zero
    public int getAndClearScore() {
        int score_copy = score;
        score = 0;
        return score_copy;
    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s) {
        collision = true;
        if (s instanceof Alien) {
            score += damage;
        }
        vis = false;
    }

    @Override
    public ArrayList<int[]> getDrawParams() {
        ArrayList<int[]> params = new ArrayList<>();
        params.add(new int[] {defaultImageID, (int) x, (int) y, 0, 0, getWidth(), getHeight()});
        return params;
    }
}
