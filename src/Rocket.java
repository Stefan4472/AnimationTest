/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private final int BOARD_WIDTH = 390;
    private final int MISSILE_SPEED = 2;
    private float acceleration;

    public Rocket(int x, int y) {
        super(x, y);

        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        getImageDimensions();
        acceleration = 0;
    }


    public void move() {
        x += MISSILE_SPEED + acceleration;
        acceleration += 0.2;
        System.out.println("Acceleration is " + acceleration);
        if (x > BOARD_WIDTH) {
            vis = false;
        }
    }
}
