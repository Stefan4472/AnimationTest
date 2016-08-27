package plainsimple.spaceships.sprites;

import android.graphics.Rect;
import android.util.Log;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;
import plainsimple.spaceships.activity.GameActivity;
import plainsimple.spaceships.util.RocketType;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    public Rocket(BitmapData bitmapData, int x, int y, RocketType rocketType) {
        super(bitmapData, x, y);
        hitBox = new Rect(x + (int) (getWidth() * 0.7), y - (int) (getHeight() * 0.2), x + (int) (getWidth() * 1.5), y + (int) (getHeight() * 1.2));
        speedX = rocketType.getSpeedX();
        damage = rocketType.getDamage();
    }

    @Override
    public void updateActions() {
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Rocket at x = " + x);
        }
    }

    @Override
    public void updateSpeeds() { // todo: relative speeds, acceleration

    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Alien) {
            //GameActivity.incrementScore(damage);
        }
        terminate = true;
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
