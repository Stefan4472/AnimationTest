package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;
    private SpriteAnimation disappear;

    private static int spinBitmapID;
    private static int disappearBitmapID;

    public static void setSpinBitmapID(int spinBitmapID) {
        Coin.spinBitmapID = spinBitmapID;
    }

    public static void setDisappearBitmapID(int disappearBitmapID) {
        Coin.disappearBitmapID = disappearBitmapID;
    }

    public Coin(int defaultImageID, int spriteWidth, int spriteHeight, Bitmap spinAnimation, Bitmap disappearAnimation, float x, float y) {
        super(defaultImageID, spriteWidth, spriteHeight, x, y);
        spin = new SpriteAnimation(spinAnimation, width, height, 5, true);
        spin.start();
        disappear = new SpriteAnimation(disappearAnimation, width, height, 1, false);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions((int) (width * 0.4), (int) (height * 0.8));
        hitBox.setOffsets((width - hitBox.getWidth()) / 2, (height - hitBox.getHeight()) / 2);
    }

    @Override
    public void updateActions() {
        if (disappear.hasPlayed()) {
            vis = false;
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {
        if (disappear.isPlaying()) {
            disappear.incrementFrame();
        } else {
            spin.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Spaceship) {
            disappear.start();
        }
    }

    @Override
    public ArrayList<float[]> getDrawParams() {
        ArrayList<float[]> params = new ArrayList<>();
        if (disappear.isPlaying()) {
            params.add(new float[]{disappearBitmapID, x, y, disappear.getStartX(), disappear.getStartY(), disappear.getEndX(), disappear.getEndY()});
        } else {
            params.add(new float[] {spinBitmapID, x, y, spin.getStartX(), spin.getStartY(), spin.getEndX(), spin.getEndY()});
        }

    }
}
