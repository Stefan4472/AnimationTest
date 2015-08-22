import java.awt.*;
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
        hitBox = new Rectangle.Double(x, y, 9, 3);
        hitBoxOffsetX = 0;
        hitBoxOffsetY = 0;
    }

    public float getSpeedX() { return MISSILE_SPEED; }
    public float getSpeedY() { return 0; }

    public void move() {
        x += getSpeedX();

        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }

    public void update() {
        if(collision)
            System.out.println("Collision!");
    }
}
