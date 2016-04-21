package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(int defaultImageID, int spriteWidth, int spriteHeight, float x, float y) {
        super(defaultImageID, spriteWidth, spriteHeight, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions(width, height);
        damage = Integer.MAX_VALUE;
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
        ArrayList<int[]> params = new ArrayList<>();
        params.add(new int[] {defaultImageID, (int) x, (int) y, 0, 0, getWidth(), getHeight()});
        return params;
    }
}
