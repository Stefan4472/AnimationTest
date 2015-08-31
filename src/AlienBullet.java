/**
 * Created by Stefan on 8/29/2015.
 */
public class AlienBullet extends Projectile {

    public AlienBullet(float x, float y) {
        super(x, y);
        initAlienBullet();
    }

    private void initAlienBullet() {
        loadDefaultImage("alien_bullet.png");

        hitBox.setDimensions(10, 10);

        speedX = 2.0f;

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
}
