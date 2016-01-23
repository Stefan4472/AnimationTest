package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    // number of frames to delay between fired bullets
    private int delay;

    // bulletType defines bullet damage and sprite
    public final static int LASER = 1;
    public final static int ION = 2;
    public final static int PLASMA = 3;
    public final static int PLUTONIUM = 4;

    // todo: when resources can be different defaultImage shouldn't be a parameter
    public Bullet(Bitmap defaultImage, float x, float y, int bulletType) {
        super(defaultImage, x, y);
        switch(bulletType) {
            case LASER: // todo: figure out how to load resource in each case
                damage = 10;
                delay = 12;
                hitBox.setDimensions((int) (width * 1.5f), height);
                speedX = 0.01f;
                break;
            case ION:
                damage = 20;
                delay = 10;
                hitBox.setDimensions((int) (width * 1.5f), height);
                speedX = 0.011f;
                break;
            case PLASMA:
                damage = 30;
                delay = 8;
                hitBox.setDimensions((int) (width * 1.5f), height);
                speedX = 0.012f;
                break;
            case PLUTONIUM:
                damage = 40;
                delay = 18;
                hitBox.setDimensions((int) (width * 1.5f), height);
                speedX = 0.013f;
                break;
            default:
                System.out.println("Invalid bulletType (" + bulletType + ")");
                break;
        }
    }

    // todo: this should return some constants, refactor this
    // returns delay (ms) given bulletType
    public static int getDelay(int bulletType) {
        switch(bulletType) {
            case LASER:
                return 12;
            case ION:
                return 10;
            case PLASMA:
                return 8;
            case PLUTONIUM:
                return 18;
            default:
                return -1;
        }
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
        if (s instanceof Obstacle || s instanceof Alien) {
            vis = false;
        }
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
