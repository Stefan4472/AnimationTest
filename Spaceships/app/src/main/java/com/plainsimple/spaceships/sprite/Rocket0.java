package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.Hitbox;

/**
 * Rocket0 subclass. Flies in a straight line at linear
 * speed.
 */

public class Rocket0 extends Rocket {

    public Rocket0(Context context, float x, float y) {
        super(BitmapCache.getData(BitmapID.ROCKET_0, context), x, y);
        speedX = 0.0067f;
        hp = 10;
        hitBox = new Hitbox(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);

        move = AnimCache.get(BitmapID.ROCKET_MOVE, context);
        explode = AnimCache.get(BitmapID.EXPLOSION_1, context);

        move.start();
    }
}
