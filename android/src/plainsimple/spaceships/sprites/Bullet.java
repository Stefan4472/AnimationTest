package plainsimple.spaceships.sprites;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.BulletType;
import plainsimple.spaceships.util.DrawParams;
import plainsimple.spaceships.activity.GameActivity;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(BitmapData bitmapData, int x, int y, BulletType bulletType) {
        super(bitmapData, x, y);
        hitBox = new Rect(x + (int) (getWidth() * 0.7), y - (int) (getHeight() * 0.2), x + (int) (getWidth() * 1.5), y + (int) (getHeight() * 1.2));
        damage = bulletType.getDamage();
        speedX = bulletType.getSpeedX();
    }

    @Override
    public void updateActions() {
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Bullet at x = " + x);
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Alien) {
            //GameActivity.incrementScore(damage);
        }
        collides = false;
        terminate = true;
    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        return params;
    }
}
