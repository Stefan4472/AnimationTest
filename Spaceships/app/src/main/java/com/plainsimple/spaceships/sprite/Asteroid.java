package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.view.GameView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 3/14/2017.
 */

public class Asteroid extends Sprite {

    public Asteroid(float x, float y, float scrollSpeed, int difficulty, Context context) {
        super(BitmapCache.getData(BitmapID.ASTEROID, context), x, y);
        // speedX: slightly faster than scrollspeed
        speedX = scrollSpeed * 1.1f;
        // speedY: randomized positive/negative and up to |0.03| or so
        speedY = (random.nextBoolean() ? -1 : +1) * random.nextFloat() / 30;
        // hp: high
        hp = 40 + difficulty / 100;
        // make hitbox 20% smaller than sprite
        hitBox = new Hitbox(x + getWidth() * 0.1f, y + getHeight() * 0.1f, x + getWidth() * 0.9f, y + getHeight() * 0.9f);
        damage = 50; // todo: remove damage, use only hp
    }

    @Override
    public void updateActions() {
        if (!isInBounds() || hp <= 0) {
            terminate = true;
        }
    }

    @Override
    public void updateSpeeds() {
        // reverse speedY if it is nearly headed off a screen edge
        if ((y < 10 && speedY < 0) || (y > GameView.screenH - 10 && speedY > 0)) {
            speedY *= -1;
        }
    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s) {
        hp -= s.getHP();
        hp = hp < 0 ? 0 : hp;
    }

    @Override
    public List<DrawParams> getDrawParams() {
        List<DrawParams> draw_params = new LinkedList<>();
        draw_params.add(new DrawImage(bitmapData.getId(), x, y));
        return draw_params;
    }
}
