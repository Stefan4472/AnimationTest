import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private final int BOARD_WIDTH = 600;
    private final int MISSILE_SPEED = 2;
    private float acceleration;

    private SpriteAnimation startMoving;

    public Rocket(int x, int y) {
        super(x, y);

        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        getImageDimensions();
        acceleration = 0;

        hitBox = new Rectangle.Double(x, y, 9, 3);
        hitBoxOffsetX = 0;
        hitBoxOffsetY = 0;

        try {
            startMoving = new SpriteAnimation("rocket_starting1.png", 9, 3, 1, false);
        } catch(IOException e){}
    }

    public void move() {
        if(acceleration < 0.05)
            acceleration += 0.001;
        else if(acceleration < 0.1)
            acceleration += 0.005;
        else if(acceleration < 0.5)
            acceleration += 0.05;
        else if(acceleration < 1.0)
            acceleration += 0.1;
        else if(acceleration < 3.0)
            acceleration += 0.15;
        else
            acceleration += 0.05;

        x += MISSILE_SPEED + acceleration;
        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }

    public void update() {
        if(collision)
            System.out.println("Collision");
    }
}
