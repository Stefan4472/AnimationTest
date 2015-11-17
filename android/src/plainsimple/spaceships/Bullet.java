package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private SpriteAnimation bulletFiring;
    // delay between fired bullets
    private int delay;

    // bulletType defines bullet damage and sprite
    public final static int LASER = 1;
    public final static int ION = 2;
    public final static int PLASMA = 3;
    public final static int PLUTONIUM = 4;

    public static int getDelay(int bulletType) {
        switch(bulletType) {
            case LASER:
                return 150;
            case ION:
                return 130;
            case PLASMA:
                return 100;
            case PLUTONIUM:
                return 170;
            default:
                return -1;
        }
    }

    // todo: when resources can be different defaultImage shouldn't be a parameter
    public Bullet(Bitmap defaultImage, float x, float y, int bulletType) {
        super(defaultImage, x, y);
        switch(bulletType) {
            case LASER: // todo: figure out how to load resource in each case
                damage = 10;
                delay = 150;
                break;
            case ION:
                damage = 20;
                delay = 130;
                break;
            case PLASMA:
                damage = 30;
                delay = 100;
                break;
            case PLUTONIUM:
                damage = 40;
                delay = 170;
                break;
            default:
                System.out.println("Invalid bulletType (" + bulletType + ")");
                break;
        }
        initBullet();
    }

    private void initBullet() {
        /*try {
            bulletFiring = new SpriteAnimation("sprites/spaceship/bullet_firing_spritesheet.png", 9, 3, 1, false);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        hitBox.setDimensions(9, 3);
        speedX = 5.0f;
        //bulletFiring.start();
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
        /*if (bulletFiring.isPlaying()) {
            canvas.drawBitmap(bulletFiring.nextFrame(), x, y, null);
        } else {
            canvas.drawBitmap(defaultImage, x, y, null);
        }*/
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
