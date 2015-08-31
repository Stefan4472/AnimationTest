import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Projectile {

    private SpriteAnimation startMoving;

    public Rocket(double x, double y) {
        super(x, y);

        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        speedX = 2.0f;
        damage = 25;
        hitBox.setDimensions(9, 3);

        try {
            startMoving = new SpriteAnimation("rocket_starting1.png", 9, 3, 1, false);
        } catch(IOException e){}
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {
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

    public void handleCollision(Sprite s) {
        collision = true;
        vis = false;
    }
}
