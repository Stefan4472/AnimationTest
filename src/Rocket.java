import java.io.File;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private final int BOARD_WIDTH = 390;
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
        x += MISSILE_SPEED + acceleration;
        acceleration += 0.2;

        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }
}
