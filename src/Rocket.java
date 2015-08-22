import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    private SpriteAnimation startMoving;

    public Rocket(int x, int y) {
        super(x, y);

        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        getImageDimensions();
        speedX = 0.0f;

        hitBox = new Rectangle.Double(x, y, 9, 3);
        hitBoxOffsetX = 0;
        hitBoxOffsetY = 0;

        try {
            startMoving = new SpriteAnimation("rocket_starting1.png", 9, 3, 1, false);
        } catch(IOException e){}
    }

    public void update() {
        if(collision)
            System.out.println("Collision");
    }

    private void updateSpeedX() {
        if(speedX < 2.05)
            speedX += 0.001;
        else if(speedX < 2.1)
            speedX += 0.005;
        else if(speedX < 2.5)
            speedX += 0.05;
        else if(speedX < 3.0)
            speedX += 0.1;
        else if(speedX < 3.0)
            speedX += 0.15;
        else
            speedX += 0.05;
    }
}
