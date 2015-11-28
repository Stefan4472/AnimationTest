package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    private Map map;
    public Coin(Bitmap defaultImage, float x, float y, Map map) {
        super(defaultImage, x, y);
        this.map = map;
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions((int) (width * 0.4), (int) (height * 0.8));
        hitBox.setOffsets((width - hitBox.getWidth()) / 2, (height - hitBox.getHeight()) / 2);
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {
        if (s instanceof Spaceship) {
            vis = false;
            map.incrementScore(100);
        }
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
