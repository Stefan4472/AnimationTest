package com.plainsimple.spaceships.sprite;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Hitbox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(float x, float y, boolean collides, Context context) {
        super(BitmapCache.getData(BitmapID.OBSTACLE, context), x, y);
        this.collides = collides;
        hitBox = new Hitbox(x, y, x + getWidth(), y + getHeight());
        damage = 10_000; // todo: some impossible to reach number?
    }

    @Override
    public void updateActions() {
        /*if (x > GameView.screenW + bitmapData.getWidth()) {
            Log.d("Obstacle Class", "Obstacle is Out of Bounds because x = " + x + " and x must be less than " + (GameView.screenW + bitmapData.getWidth()));
        }*/
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing Obstacle at x = " + x);
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

    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        drawParams.add(new DrawImage(bitmapData.getId(), x, y));
        return drawParams;
    }
}
