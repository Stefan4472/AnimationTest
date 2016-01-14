package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien2 extends Alien { // todo: not implemented

    public Alien2(Bitmap defaultImage, float x, float y, Map map) {
        super(defaultImage, x, y, map);
        initAlien();
    }

    private void initAlien() {
        startingY = y;
        hp = 40 + (int) map.getDifficulty() / 4;
        bulletDelay = 1_000 - map.getDifficulty();
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
    public void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }

    @Override
    public void fireBullet(Sprite s) {

    }
}
