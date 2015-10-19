package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private SpriteAnimation bulletFiring;

    // bulletType defines bullet damage and sprite // todo: make constants
    // bulletType 1: laser cannon
    // bulletType 2: ion cannon
    // bulletType 3: plasma cannon
    // bulletType 4: plutonium cannon
    public Bullet(Bitmap defaultImage, float x, float y, int bulletType, Board board) {
        super(defaultImage, x, y, board);
        if (bulletType == 1) {
            damage = 10;
            // loadDefaultImage("laser_bullet.png");
        } else if (bulletType == 2) {
            damage = 20;
            // loadDefaultImage("ion_bullet.png");
        } else if (bulletType == 3) {
            damage = 30;
            // loadDefaultImage("plasma_bullet.png");
        } else {
            System.out.println("Invalid bulletType (" + bulletType + ")");
        }
        initBullet();
    }

    private void initBullet() {
        try {
            bulletFiring = new SpriteAnimation("sprites/spaceship/bullet_firing_spritesheet.png", 9, 3, 1, false);
        } catch (IOException e) {
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
        if (s instanceof Obstacle || s instanceof Alien)
            vis = false;
    }

    @Override
    void draw(Canvas canvas) {
        if (bulletFiring.isPlaying()) {
            canvas.drawBitmap(bulletFiring.nextFrame(), x, y, null);
        } else {
            canvas.drawBitmap(defaultImage, x, y, null);
        }
    }
}
