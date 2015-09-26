package plainsimple.spaceships;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private SpriteAnimation bulletFiring;

    // bulletType defines bullet damage and sprite
    // bulletType 1: laser cannon
    // bulletType 2: ion cannon
    // bulletType 3: plasma cannon
    // bulletType 4: plutonium cannon
    public Bullet(double x, double y, int bulletType, Board board) {
        super(x, y, board);
        if(bulletType == 1) {
            damage = 10;
            // loadDefaultImage("laser_bullet.png");
        } else if(bulletType == 2) {
            damage = 20;
            // loadDefaultImage("ion_bullet.png");
        } else if(bulletType == 3) {
            damage = 30;
            // loadDefaultImage("plasma_bullet.png");
        } else {
            System.out.println("Invalid bulletType (" + bulletType + ")");
        }
        initBullet();
    }

    private void initBullet() {
        loadDefaultImage("sprites/spaceship/bullet_sprite.png");
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
