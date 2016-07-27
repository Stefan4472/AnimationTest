package plainsimple.spaceships.sprites;

import android.graphics.Rect;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;
import plainsimple.spaceships.activity.GameActivity;
import plainsimple.spaceships.util.SpriteAnimation;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private SpriteAnimation spin;
    private SpriteAnimation disappear;

    public Coin(BitmapData bitmapData, SpriteAnimation spinAnimation, SpriteAnimation disappearAnimation, int x, int y) {
        super(bitmapData, x, y);
        //spin = new SpriteAnimation(spinAnimation, width, height, 5, true);
        spin = spinAnimation;
        spin.start();
        //disappear = new SpriteAnimation(disappearAnimation, width, height, 1, false);
        disappear = disappearAnimation;
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions((int) (getWidth() * 0.4), (int) (getHeight() * 0.8));
        hitBox.setOffsets((getWidth() - hitBox.getWidth()) / 2, (getHeight() - hitBox.getHeight()) / 2);
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
            //disappear.start();
            vis = false;
            GameActivity.incrementScore(GameActivity.COIN_VALUE);
        }
    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>(); // todo: store one list that gets reset?
        /*if (disappear.isPlaying()) {
            Rect source = disappear.getCurrentFrameSrc();
            params.add(new DrawParams(disappear.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        } else {*/
            Rect source = spin.getCurrentFrameSrc();
            params.add(new DrawParams(spin.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        //}
        return params;
    }
}
