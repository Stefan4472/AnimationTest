package plainsimple.spaceships.sprites;

import android.graphics.Rect;
import android.util.Log;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;
import plainsimple.spaceships.activity.GameActivity;

import java.util.ArrayList;

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

    public Rocket(BitmapData bitmapData, int x, int y, int rocketType) {
        super(bitmapData, x, y);
        hitBox = new Rect(x + (int) (getWidth() * 0.7), y - (int) (getHeight() * 0.2), x + (int) (getWidth() * 1.5), y + (int) (getHeight() * 1.2));
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
    public void updateSpeeds() { // todo: relative speeds, acceleration

    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Alien) {
            GameActivity.incrementScore(damage);
        }
        vis = false;
        collides = false;
    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        return params;
    }
}
