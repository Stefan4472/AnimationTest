package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/28/2015.
 */
public abstract class Alien extends Sprite {

    // ms to wait between firing bullets
    protected double bulletDelay;
    protected long lastFiredBullet = 0;
    protected Map map;

    protected double bulletSpeed;
    protected List<Sprite> projectiles = new ArrayList<>();

    // frames since alien was constructed
    // used for calculating trajectory
    protected int elapsedFrames = 1;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    protected double startingY;

    public List<Sprite> getProjectiles() {
        return projectiles;
    }
    public List<Sprite> getAndClearProjectiles() {
        List<Sprite> copy = new ArrayList<>();
        for(Sprite p : projectiles) {
            copy.add(p);
        }
        projectiles.clear();
        return copy;
    }

    public Alien(Bitmap defaultImage, float x, float y, Map map) {
        super(defaultImage, x, y);
        this.map = map;
    }

    private void initLevel3Alien() { // todo: move to Alien3 class
        startingY = y;
        hp = 60 + (int) map.getDifficulty() / 5;
        bulletDelay = 500;
        bulletSpeed = -3.5f;
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        damage = 500;
        speedX = -2.0f;
    }

    // update/handle any actions sprite takes
    @Override
    abstract void updateActions();

    // updates speedX and speedY of spaceship
    @Override
    abstract void updateSpeeds();

    // handles collision with specified sprite
    @Override
    public void handleCollision(Sprite s) {
        if (!(s instanceof AlienBullet)) { // todo: clean up
            if (s instanceof Bullet || s instanceof Rocket) {
                map.incrementScore(s.getDamage());
            }
            hp -= s.damage;
            if (hp < 0) { // todo: death animation
                vis = false;
                // collides = false;
                hp = 0;
            }
        }
    }

    @Override
    abstract void draw(Canvas canvas);

    // fires bullet/projectile at specified sprite
    abstract void fireBullet(Sprite s); // todo: unnecessary
}
