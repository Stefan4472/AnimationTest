import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private final int BOARD_WIDTH = 600;
    private final int MISSILE_SPEED = 2;

    private SpriteAnimation startMoving;

    public Rocket(int x, int y) {
        super(x, y);

        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        getImageDimensions();

        try {
            startMoving = new SpriteAnimation("rocket_starting1.png", 9, 3, 1, false);
        } catch(IOException e){}
    }

    public void move() {
        x += MISSILE_SPEED + getSpeedX();
        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }

    // calculates and returns horizontal speed
    public float getSpeedX() {
        if(speedX < 0.05)
            speedX += 0.001;
        else if(speedX < 0.1)
            speedX += 0.005;
        else if(speedX < 0.5)
            speedX += 0.05;
        else if(speedX < 1.0)
            speedX += 0.1;
        else if(speedX < 3.0)
            speedX += 0.15;
        else
            speedX += 0.05;
        return speedX;
    }

    public float getSpeedY() {
        return 0.0f;
    }

    // returns x-coordinate
    public int getX() {
        return x;
    }
}
