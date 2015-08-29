/**
 * Created by Stefan on 8/28/2015.
 */
public class Alien extends Sprite {

    public Alien(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Alien(String imageName, int x, int y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
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
        vis = false;
    }
}
