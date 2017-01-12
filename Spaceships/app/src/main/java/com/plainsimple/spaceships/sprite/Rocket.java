package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.RocketType;
import com.plainsimple.spaceships.helper.SpriteAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private SpriteAnimation move;

    public Rocket(Context context, float x, float y, RocketType rocketType) {
        super(BitmapCache.getData(BitmapID.ROCKET, context), x, y);
        move = AnimCache.get(BitmapID.ROCKET_MOVE, context);
        speedX = rocketType.getSpeedX();
        damage = rocketType.getDamage();
        hitBox = new Hitbox(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);
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
        move.incrementFrame();
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        // draw moving animation behind the rocket
        drawParams.add(new DrawSubImage(move.getBitmapID(), x - move.getFrameW(), y, move.getCurrentFrameSrc()));
        return drawParams;
    }
}
