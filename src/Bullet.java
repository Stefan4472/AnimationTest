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
        getImageDimensions();

        hitBoxOffsetX = 0;
        hitBoxOffsetY = 0;
        hitBoxWidth = 9;
        hitBoxHeight = 3;

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
}
