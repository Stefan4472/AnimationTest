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
        hitBox.setDimensions((int) (width * 0.9), (int) (height * 0.9));
        hitBox.setOffsets((width - hitBox.getWidth()) / 2, (height - hitBox.getHeight()) / 2);
        damage = Integer.MAX_VALUE;
    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {

    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
