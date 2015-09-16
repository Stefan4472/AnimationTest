import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(String imageName, Board board) {
        super(imageName, board);
        initObstacle();
    }

    public Obstacle(String imageName, double x, double y, Board board) {
        super(imageName, x, y, board);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions(50, 50);
        damage = Integer.MAX_VALUE;
    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {

    }

    @Override
    void render(Graphics2D g, ImageObserver o) {
        g.drawImage(defaultImage, (int) x, (int) y, o);
    }
}
