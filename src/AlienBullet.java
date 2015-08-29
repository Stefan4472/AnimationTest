/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Sprite {

    public AlienBullet(int x, int y) {
        super(x, y);
        initAlienBullet();
    }

    private void initAlienBullet() {
        loadDefaultImage("alien_bullet.png");

        hitBox.setDimensions(10, 10);

        speedX = 1.0f;
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
}
