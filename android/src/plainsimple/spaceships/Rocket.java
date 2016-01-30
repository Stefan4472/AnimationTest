package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    // todo: different rocket types (?)
    public final static int ROCKET = 1;

    // number of to delay between firing rockets
    public int delay;

    public static final int ROCKET_DAMAGE = 40;
    public static final int ROCKET_DELAY = 40;
    public static final float ROCKET_SPEED_X = 0.0067f;

    public Rocket(Bitmap defaultImage, float x, float y, int rocketType) {
        super(defaultImage, x, y);
        hitBox.setDimensions((int) (width * 1.5f), height);
        switch(rocketType) {
            case ROCKET:
                delay = ROCKET_DELAY;
                speedX = ROCKET_SPEED_X;
                damage = ROCKET_DAMAGE;
                break;
        }
    }

    public static int getDelay(int rocketType) {
        switch (rocketType) {
            case ROCKET:
                return ROCKET_DELAY;
            default:
                return -1;
        }
    }


    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() { // todo: relative speeds

    }

    @Override
    public void handleCollision(Sprite s) {
        collision = true;
        vis = false;
    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
