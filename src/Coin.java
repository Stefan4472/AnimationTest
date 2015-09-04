import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    public Coin(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Coin(String imageName, double x, double y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setOffsets(15, 5);
        hitBox.setDimensions(20, 40);
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {
        if(s instanceof Spaceship)
            vis = false;
    }

    @Override
    void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, (int) x, (int) y, o);
    }
}
