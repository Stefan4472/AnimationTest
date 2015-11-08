package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 8/17/2015.
 */
public class Bullet extends Sprite {

    private SpriteAnimation bulletFiring;

    // bulletType defines bullet damage and sprite
    public final static int BULLET_LASER = 1;
    public final static int BULLET_ION = 2;
    public final static int BULLET_PLASMA = 3;
    public final static int BULLET_PLUTONIUM = 4;
    // todo: when resources can be different defaultImage shouldn't be a parameter
    public Bullet(Bitmap defaultImage, float x, float y, int bulletType, Board board) {
        super(defaultImage, x, y, board);
        switch(bulletType) {
            case BULLET_LASER: // todo: figure out how to load resource in each case
                damage = 10;
                break;
            case BULLET_ION:
                damage = 20;
                break;
            case BULLET_PLASMA:
                damage = 30;
                break;
            case BULLET_PLUTONIUM:
                damage = 40;
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
        /*if (bulletFiring.isPlaying()) {
            canvas.drawBitmap(bulletFiring.nextFrame(), x, y, null);
        } else {
            canvas.drawBitmap(defaultImage, x, y, null);
        }*/
        canvas.drawBitmap(defaultImage, x, y, null);
    }
}
