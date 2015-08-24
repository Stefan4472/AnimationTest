import java.awt.*;
import java.io.IOException;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(int x, int y) {
        super(x, y);
        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("rocket.png");

        hitBox.setDimensions(9, 3);

        speedX = 5.0f;
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeedX() {

    }

    public void updateSpeedY() {

    }

    public void handleCollision(Sprite s) {
        collision = true;
        vis = false;
    }
}
