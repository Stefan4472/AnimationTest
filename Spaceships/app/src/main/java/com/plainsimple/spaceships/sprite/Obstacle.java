package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawParams;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(BitmapData bitmapData, int x, int y) {
        super(bitmapData, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox = new Rect(x, y, x + getWidth(), y + getHeight());
        damage = Integer.MAX_VALUE;
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
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        return params;
    }
}
