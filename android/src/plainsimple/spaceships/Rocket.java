package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    // todo: different rocket types (?)
    public final static int ROCKET = 1;

    public int delay;

    public static int getDelay(int rocketType) {
        switch (rocketType) {
            case ROCKET:
                return 420;
            default:
                return -1;
        }
    }

    // todo: figure out resource loading and whether to pass a bitmap
    public Rocket(Bitmap defaultImage, float x, float y, int rocketType) {
        super(defaultImage, x, y);
        switch(rocketType) {
            case ROCKET:
                delay = 420;
                speedX = 0.0067f;
                hitBox.setDimensions((int) (width * 1.5f), height);
                break;
        }
    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() { // todo: relative speeds
        /*if (speedX < 2.05)
            speedX += 0.001;
        else if (speedX < 2.1)
            speedX += 0.005;
        else if (speedX < 2.5)
            speedX += 0.05;
        else if (speedX < 3.0)
            speedX += 0.1;
        else if (speedX < 3.0)
            speedX += 0.15;
        else
            speedX += 0.05;*/
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
