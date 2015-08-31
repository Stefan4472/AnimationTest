/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    public Bullet(float x, float y) {
        super(x, y);
        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("rocket.png");

        hitBox.setDimensions(9, 3);

        speedX = 5.0f;
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
