import java.io.IOException;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {
    private final int BOARD_WIDTH = 600;
    private final int MISSILE_SPEED = 5;

    public Bullet(int x, int y) {
        super(x, y);

        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("rocket.png");
        getImageDimensions();
    }


    public void move() {
        x += MISSILE_SPEED;

        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }
}
