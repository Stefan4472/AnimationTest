package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    public Coin(Bitmap defaultImage, Board board) {
        super(defaultImage, board);
        initObstacle();
    }

    public Coin(Bitmap defaultImage, float x, float y, Board board) {
        super(defaultImage, x, y, board);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setOffsets(15, 5);
        hitBox.setDimensions(20, 40);
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
            board.incrementScore(100);
        }
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, (float) x, (float) y, null);
    }
}
