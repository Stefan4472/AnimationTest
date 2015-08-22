import java.awt.*;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Obstacle(String imageName, int x, int y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox = new Rectangle.Double(x, y, 50, 50);
        hitBoxOffsetX = 0;
        hitBoxOffsetY = 0;
    }

    public void move() {

    }

    public void update() {

    }
}
