package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Sprite {

    public AlienBullet(int defaultImageID, int spriteWidth, int spriteHeight, float x, float y) {
        super(defaultImageID, spriteWidth, spriteHeight, x, y);
        initAlienBullet();
    }

    private void initAlienBullet() {
        // todo: change size for better-looking hit detection? (see Issue #13)
        hitBox.setDimensions(width, height);
        damage = 20;
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
        if(s instanceof Spaceship) {
            vis = false;
        }
    }

    @Override
    public ArrayList<int[]> getDrawParams() {
        ArrayList<int[]> params = new ArrayList<>();
        params.add(new int[] {defaultImageID, (int) x, (int) y, 0, 0, getWidth(), getHeight()});
        return params;
    }
}
