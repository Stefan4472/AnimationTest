package plainsimple.spaceships.sprites;

import android.graphics.Rect;
import android.util.Log;
import plainsimple.spaceships.activity.GameActivity;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;
import plainsimple.spaceships.util.Point2D;
import plainsimple.spaceships.util.SpriteAnimation;

import java.util.ArrayList;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien1 extends Alien {

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

    private Spaceship spaceship;
    private double difficulty;

    private BitmapData bulletBitmapData;
    private SpriteAnimation explodeAnimation;

    public Alien1(BitmapData bitmapData, int x, int y, Spaceship spaceship) {
        super(bitmapData, x, y);
        this.spaceship = spaceship;
        difficulty = GameActivity.getDifficulty();
        initAlien();
    }

    private void initAlien() { // todo: randomized stuff is pretty arbitrary
        startingY = y;
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        hp = 20 + (int) (difficulty / 3);
        bulletDelay = 30;
        framesSinceLastBullet = bulletDelay;
        bulletSpeed = -0.002f - random.nextInt(5) / 10000.0;
        hitBox.setDimensions((int) (getWidth() * 0.8), (int) (getHeight() * 0.8));
        hitBox.setOffsets(getWidth() - hitBox.getWidth(), getHeight() - hitBox.getHeight());
        damage = 50;
        speedX = -0.0035f;
    }

    public void injectResources(BitmapData bulletBitmapData, SpriteAnimation explodeAnimation) {
        this.bulletBitmapData = bulletBitmapData;
        this.explodeAnimation = explodeAnimation;
        //explodeAnimation = new SpriteAnimation(explodeSpriteSheet, width, height, 3, false);
    }

    @Override
    public void updateActions() { // todo: avoid straight vertical shots
        framesSinceLastBullet++;
        if (distanceTo(spaceship) < 0.8 && framesSinceLastBullet >= bulletDelay) {
            if (getP(0.2f)) {
                fireBullet(spaceship);
                framesSinceLastBullet = 0;
            }
        }
        // disappear if alien has exploded
        if(explodeAnimation.hasPlayed()) {
            vis = false;
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
    public void updateAnimations() {
        if (explodeAnimation.isPlaying()) {
            explodeAnimation.incrementFrame();
        }
    }

    @Override
    public void handleCollision(Sprite s) {
        if (s instanceof Bullet || s instanceof Rocket || s instanceof Spaceship) {
            hp -= s.damage;
            if (hp < 0 && !explodeAnimation.isPlaying()) {
                Log.d("Alien Class", "Alien Destroyed");
                explodeAnimation.start();
                hp = 0;
                collision = true;
            }
        }
    }

    @Override
    public ArrayList<DrawParams> getDrawParams() {
        ArrayList<DrawParams> params = new ArrayList<>();
        params.add(new DrawParams(bitmapData.getId(), x, y, 0, 0, getWidth(), getHeight()));
        if(explodeAnimation.isPlaying()) {
            Rect source = explodeAnimation.getCurrentFrameSrc();
            params.add(new DrawParams(explodeAnimation.getBitmapID(), x, y, source.left, source.top, source.right, source.bottom));
        }
        return params;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    @Override
    public void fireBullet(Sprite s) {
        Point2D target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(bulletBitmapData, x, y + (int) (getHeight() * 0.1));
        b.setSpeedX(bulletSpeed);
        double frames_to_impact = (x - s.getX()) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }
}
