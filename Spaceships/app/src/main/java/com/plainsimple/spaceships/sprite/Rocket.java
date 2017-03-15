package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawSubImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.helper.SpriteAnimation;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A superclass for all rocket types. Because rockets
 * share so much common functionality, this class is
 * almost complete. Subclasses may override whichever
 * methods they choose.
 * Rockets are required to have a move animation and
 * an explode animation. todo: work this out. Perhaps a SpriteAnimation superclass?
 */
public abstract class Rocket extends Sprite {

    // static method used to create a new instance of Rocket. Uses rocketType
    // to determine which subclass to instantiate. This allows the spaceship
    // to get the Rocket instance without needing to know which subclass to
    // call.
    public static Rocket newInstance(Context context, float x, float y, RocketType rocketType) {
        switch (rocketType) {
            case ROCKET_0:
            case ROCKET_1:
            case ROCKET_2:
            case ROCKET_3:
                return new Rocket0(context, x, y);
            default:
                throw new NoSuchElementException("Did not recognize RocketType " + rocketType);
        }
    }


    protected SpriteAnimation move;
    protected SpriteAnimation explode;

    protected Rocket(BitmapData bitmapData, float x, float y) {
        super(bitmapData, x, y);
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
