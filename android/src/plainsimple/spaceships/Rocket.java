package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    public Rocket(Bitmap defaultImage, double x, double y, Board board) {
        super(defaultImage, x, y, board);
        damage = 20;
        initMissile();
    }

    public Rocket(Bitmap defaultImage, double x, double y, int damage, Board board) {
        super(defaultImage, x, y, board);
        this.damage = damage;
        initMissile();
    }

    private void initMissile() {
        speedX = 2.0f;
        hitBox.setDimensions(9, 3);
    }

    public void updateActions() {

    }

    public void updateSpeeds() {
        if (speedX < 2.05)
            speedX += 0.001;
        else if (speedX < 2.1)
            speedX += 0.005;
        else if (speedX < 2.5)
            speedX += 0.05;
        else if (speedX < 3.0)
            speedX += 0.1;
        else if (speedX < 3.0)
            speedX += 0.15;
        else
            speedX += 0.05;
    }

    public void handleCollision(Sprite s) {
        collision = true;
        vis = false;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
