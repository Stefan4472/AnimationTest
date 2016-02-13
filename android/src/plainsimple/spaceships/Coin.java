package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private Map map;
    private SpriteAnimation spin;
    private SpriteAnimation disappear;

    public Coin(Bitmap defaultImage, Bitmap spinAnimation, Bitmap disappearAnimation, float x, float y, Map map) {
        super(defaultImage, x, y);
        spin = new SpriteAnimation(spinAnimation, width, height, 5, true);
        spin.start();
        disappear = new SpriteAnimation(disappearAnimation, width, height, 1, false);
        this.map = map;
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
            map.incrementScore(100);
        }
    }

    @Override
    void draw(Canvas canvas) {
        if (disappear.isPlaying()) {
            canvas.drawBitmap(disappear.currentFrame(), x, y, null);
        } else {
            canvas.drawBitmap(spin.currentFrame(), x, y, null);
        }

    }
}
