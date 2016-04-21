package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien2 extends Alien { // todo: not implemented

    public Alien2(int defaultImageID, int spriteWidth, int spriteHeight, float x, float y) {
        super(defaultImageID,spriteWidth, spriteHeight, x, y);
        initAlien();
    }

    private void initAlien() { // todo: see Alien1 changes
        startingY = y;
        //hp = 40 + (int) map.getDifficulty() / 4;
        //bulletDelay = 1_000 - map.getDifficulty();
        bulletSpeed = -3.0f - random.nextInt(5) / 5;
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        damage = 100;
        speedX = -2.0f;
    }

    @Override
    public void updateActions() {

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
    public ArrayList<int[]> getDrawParams() {
        return new ArrayList<> ();
    }

    @Override
    public void fireBullet(Sprite s) {

    }
}
