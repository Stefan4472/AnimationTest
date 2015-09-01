/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(double x, double y) {
        super(x, y);
        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("bullet.png");

        hitBox.setDimensions(9, 3);

        speedX = 5.0f;

        damage = 10;
    }

    public void updateCurrentImage() {

    }

    public void updateActions() {

    }

    public void updateSpeeds() {

    }

    public void handleCollision(Sprite s) {
        collision = true;
        if(s instanceof Obstacle || s instanceof Alien)
            vis = false;
    }
}
