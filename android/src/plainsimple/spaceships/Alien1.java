package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien1 extends Alien {

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

    public Alien1(Bitmap defaultImage, Board board) {
        super(defaultImage, board);
        initAlien();
    }

    public Alien1(Bitmap defaultImage, float x, float y, Board board) {
        super(defaultImage, x, y, board);
        initAlien();
    }

    private void initAlien() {
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 20 + (int) board.getDifficulty() / 3;
        bulletDelay = 2_000 - board.getDifficulty() * 5;
        bulletSpeed = -2.0f - random.nextInt(10) / 10;
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        damage = 50;
        speedX = -2.0f;
    }

    @Override
    public void updateActions() {
        if (distanceTo(board.getSpaceship()) < 400 &&
                lastFiredBullet + bulletDelay <= System.currentTimeMillis()) {
            if (getP(0.2f)) {
                fireBullet(board.getSpaceship());
                lastFiredBullet = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void updateSpeeds() {
        double projected_y;
        // if sprite in top half of screen, start flying down. Else start flying up
        if (startingY <= 150) {
            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        } else { // todo: flying up
            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        }
        speedY = projected_y - y;
        elapsedFrames++;
    }

    @Override
    public void handleCollision(Sprite s) {
        if (!(s instanceof AlienBullet)) {
            if (s instanceof Bullet || s instanceof Rocket) {
                board.incrementScore(s.damage);
            }
            hp -= s.damage;
            if (hp < 0) { // todo: death animation
                vis = false;
                hp = 0;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(defaultImage, x, y, null);
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    @Override
    void fireBullet(Sprite s) {
        Point2D target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(x, y + 20, board);
        b.setSpeedX(bulletSpeed);
        double frames_to_impact = (x - s.x) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }
}
