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
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {
        vis = false;
        System.out.println("Coin collision");
    }
}
