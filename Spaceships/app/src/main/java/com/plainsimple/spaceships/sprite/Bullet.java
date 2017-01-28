package com.plainsimple.spaceships.sprite;

import android.util.Log;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.CannonType;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Hitbox;

import java.util.List;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(BitmapData bitmapData, float x, float y, CannonType cannonType) {
        super(bitmapData, x, y);
        hitBox = new Hitbox(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);
        damage = cannonType.getDamage();
        speedX = cannonType.getSpeedX();
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
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        return drawParams;
    }
}
