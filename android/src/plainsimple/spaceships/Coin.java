package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

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

    public Coin(int defaultImageID, int spriteWidth, int spriteHeight, SpriteAnimation spinAnimation, SpriteAnimation disappearAnimation, float x, float y) {
        super(defaultImageID, spriteWidth, spriteHeight, x, y);
        //spin = new SpriteAnimation(spinAnimation, width, height, 5, true);
        spin = spinAnimation;
        spin.start();
        //disappear = new SpriteAnimation(disappearAnimation, width, height, 1, false);
        disappear = disappearAnimation;
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
            Rect spritesheet_src = disappear.getCurrentFrameSrc();
            params.add(new float[]{disappear.getBitmapID(), x, y, spritesheet_src.left, spritesheet_src.top, spritesheet_src.bottom, spritesheet_src.right});
        } else {
            Rect spritesheet_src = spin.getCurrentFrameSrc();
            params.add(new float[]{spin.getBitmapID(), x, y, spritesheet_src.left, spritesheet_src.top, spritesheet_src.bottom, spritesheet_src.right});
        }
        return params;
    }
}
