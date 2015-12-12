package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien1 extends Alien {

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

    private Bitmap bulletBitmap;

    public Alien1(Bitmap defaultImage, float x, float y, Map map) {
        super(defaultImage, x, y, map);
        initAlien();
    }

    private void initAlien() {
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 20 + (int) map.getDifficulty() / 3;
        bulletDelay = 2_000 - map.getDifficulty() * 5;
        bulletSpeed = -0.002f - random.nextInt(5) / 10000.0;
        hitBox.setDimensions((int) (width * 0.8), (int) (height * 0.8));
        hitBox.setOffsets(width - hitBox.getWidth(), height - hitBox.getHeight());
        damage = 50;
        speedX = -0.0035f;
    }

    public void injectResources(Bitmap bulletBitmap) {
        this.bulletBitmap = bulletBitmap;
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        if (distanceTo(map.getSpaceship()) < 400 &&
                lastFiredBullet + bulletDelay <= System.currentTimeMillis()) {
            if (getP(0.2f)) {
                fireBullet(map.getSpaceship());
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
        speedY = (projected_y - y) / 600; // todo: more elegant
        elapsedFrames++;
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Bullet || s instanceof Rocket) {
            map.incrementScore(s.getDamage());
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
        AlienBullet b = new AlienBullet(bulletBitmap, x, y + (int) (height * 0.4));
        b.setSpeedX(bulletSpeed);
        double frames_to_impact = (x - s.getX()) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }
}
