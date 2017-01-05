package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.SpriteAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;

    public Coin(float x, float y, Context context) {
        super(BitmapCache.getData(BitmapID.COIN, context), x, y);
        spin = AnimCache.get(BitmapID.COIN_SPIN, context);
        spin.start();
        hitBox = new Hitbox(x + getWidth() * 0.3f, y + getHeight() * 0.1f, x + getWidth() * 0.7f, y + getHeight() * 0.9f);
    }

    @Override
    public void updateActions() {
        if (!isInBounds() || terminate) {
            terminate = true;
            Log.d("Termination", "Removing Coin at x = " + x);
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {
        spin.incrementFrame();
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Spaceship) {
            //disappear.start();
            GameActivity.incrementScore(GameActivity.COIN_VALUE);
            terminate = true;
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        drawParams.add(new DrawSubImage(spin.getBitmapID(), x, y, spin.getCurrentFrameSrc()));
        return drawParams;
    }
}
