package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(Bitmap defaultImage, float x, float y) {
        super(defaultImage, x, y);
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
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
