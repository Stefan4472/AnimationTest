import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Obstacle(String imageName, double x, double y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions(50, 50);
        damage = Integer.MAX_VALUE;
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {

    }

    @Override
    void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, (int) x, (int) y, o);
    }
}
