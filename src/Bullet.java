import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private SpriteAnimation bulletFiring;

    public Bullet(double x, double y) {
        super(x, y);
        damage = 10;
        initBullet();
    }

    public Bullet(double x, double y, int damage) {
        super(x, y);
        this.damage = damage;
        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("bullet.png");
        try {
            bulletFiring = new SpriteAnimation("bullet_firing_spritesheet.png", 9, 3, 1, false);
        } catch(IOException e) {
            e.printStackTrace();
        }
        hitBox.setDimensions(9, 3);
        speedX = 5.0f;
        bulletFiring.start();
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

    @Override
    void render(Graphics2D g, ImageObserver o) {
        if(bulletFiring.isPlaying()) {
            g.drawImage(bulletFiring.nextFrame(), (int) x, (int) y, o);
        } else {
            g.drawImage(defaultImage, (int) x, (int) y, o);
       }
    }
}
