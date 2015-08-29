/**
 * Created by Stefan on 8/28/2015.
 */
public class Coin extends Sprite {

    public Coin(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Coin(String imageName, int x, int y) {
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
}
