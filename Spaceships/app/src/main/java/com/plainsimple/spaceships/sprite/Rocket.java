package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private SpriteAnimation move;
    private SpriteAnimation explode;

    public Rocket(Context context, float x, float y, RocketType rocketType) {
        super(BitmapCache.getData(rocketType.getDrawableId(), context), x, y);
        move = AnimCache.get(BitmapID.ROCKET_MOVE, context);
        move.start();
        explode = AnimCache.get(BitmapID.EXPLOSION_1, context);
        speedX = rocketType.getSpeedX();
        hp = rocketType.getDamage();
        hitBox = new Hitbox(x + getWidth() * 0.7f, y - getHeight() * 0.2f, x + getWidth() * 1.5f, y + getHeight() * 1.2f);
    }

    @Override
    public void updateActions() {
        if (!isInBounds() || explode.hasPlayed()) { // todo: potential bug in SpriteAnimation hasPlayed()
            terminate = true;
//            Log.d("Termination", "Removing Rocket at x = " + x);
        }
    }

    @Override
    public void updateSpeeds() { // todo: relative speeds, acceleration

    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        if (!(s instanceof Spaceship)) {
            move.stop();
            explode.start();
            speedX = s.speedX;
            collides = false;
            GameView.incrementScore(damage);
        }
    }

    @Override
    public void updateAnimations() {
        if (move.isPlaying()) {
            move.incrementFrame();
        } else if (explode.isPlaying()) {
            explode.incrementFrame();
        }
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        if (explode.isPlaying()) {
            drawParams.add(new DrawSubImage(explode.getBitmapID(), x + (explode.getFrameW() - getWidth()) / 2, y - (explode.getFrameH() - getHeight()) / 2,
                    explode.getCurrentFrameSrc())); // todo: refine
        } else if (!explode.hasPlayed()){
            drawParams.add(new DrawImage(bitmapData.getId(), x, y));
            // draw moving animation behind the rocket
            drawParams.add(new DrawSubImage(move.getBitmapID(), x - move.getFrameW(), y, move.getCurrentFrameSrc()));
        }
        return drawParams;
    }
}
