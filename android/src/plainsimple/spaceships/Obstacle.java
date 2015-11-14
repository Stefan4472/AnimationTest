package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(Bitmap defaultImage, float x, float y) {
        super(defaultImage, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions(40, 40);
        hitBox.setOffsets(5, 5);
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
