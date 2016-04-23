package plainsimple.spaceships;

import android.graphics.Rect;

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

    public Alien1(BitmapData bitmapData, float x, float y, double difficulty, Spaceship spaceship) {
        super(bitmapData, x, y);
        this.spaceship = spaceship;
        this.difficulty = difficulty;
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
                explodeAnimation.start();
                hp = 0;
                collision = true;
            }
        }
    }

    @Override
    public ArrayList<int[]> getDrawParams() {
        ArrayList<int[]> params = new ArrayList<>();
        int[] default_img_params = {bitmapData.getId(), 0, 0, getWidth(), getHeight()};
        params.add(default_img_params);
        if(explodeAnimation.isPlaying()) {
            Rect spritesheet_src = explodeAnimation.getCurrentFrameSrc();
            int[] explode_params = {explodeAnimation.getBitmapID(), spritesheet_src.left, spritesheet_src.top, spritesheet_src.right, spritesheet_src.bottom};
            params.add(explode_params);
        }
        return params;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    @Override
    void fireBullet(Sprite s) {
        Point2D target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(bulletBitmapData, x, y + (int) (getHeight() * 0.4));
        b.setSpeedX(bulletSpeed);
        double frames_to_impact = (x - s.getX()) / bulletSpeed;
        b.setSpeedY((y - target.getY()) / frames_to_impact);
        projectiles.add(b);
    }
}
