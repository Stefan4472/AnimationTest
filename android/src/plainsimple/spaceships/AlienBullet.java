package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Sprite {

    public AlienBullet(Bitmap defaultImage, float x, float y) {
        super(defaultImage, x, y);
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
    public void handleCollision(Sprite s) {
        if(s instanceof Spaceship) {
            vis = false;
        }
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
