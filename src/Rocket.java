import java.io.File;

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

        startMoving = new SpriteAnimation(new File[] {
                new File("rocket.png"),
                new File("rocket_starting1.png"),
        }, false);
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

    // returns x-coordinate adjusted for leading empty space
    public int getX() {
        return x - 3;
    }
}
