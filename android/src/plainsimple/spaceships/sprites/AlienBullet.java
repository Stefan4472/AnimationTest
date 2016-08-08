package plainsimple.spaceships.sprites;

import android.graphics.Rect;
import android.util.Log;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Sprite {

    public AlienBullet(BitmapData bitmapData, int x, int y) {
        super(bitmapData, x, y);
        initAlienBullet();
    }

    private void initAlienBullet() {
        // todo: change size for better-looking hit detection? (see Issue #13)
        hitBox = new Rect(x, y, x + getWidth(), y + getHeight());
        damage = 20;
    }

    @Override
    public void updateActions() {
        if (!isInBounds()) {
            terminate = true;
            Log.d("Termination", "Removing AlienBullet at x = " + x);
        }
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
            collides = false;
            terminate = true;
        }
    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        return params;
    }
}
