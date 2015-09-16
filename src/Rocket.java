import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    public Rocket(double x, double y, Board board) {
        super(x, y, board);
        damage = 20;
        initMissile();
    }

    public Rocket(double x, double y, int damage, Board board) {
        super(x, y, board);
        this.damage = damage;
        initMissile();
    }

    private void initMissile() {
        loadDefaultImage("rocket.png");
        speedX = 2.0f;
        hitBox.setDimensions(9, 3);
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

    public void render(Graphics2D g, ImageObserver o) {
        g.drawImage(defaultImage, (int) x, (int) y, o);
    }
}
