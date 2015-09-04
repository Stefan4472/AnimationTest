import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Sprite {

    public AlienBullet(double x, double y) {
        super(x, y);
        initAlienBullet();
    }

    private void initAlienBullet() {
        loadDefaultImage("alien_bullet.png");

        hitBox.setDimensions(10, 10);

        damage = 20;
    }

    @Override
    public void updateCurrentImage() {

    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void handleCollision(Sprite s) {

    }

    @Override
    void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, (int) x, (int) y, o);
    }
}
