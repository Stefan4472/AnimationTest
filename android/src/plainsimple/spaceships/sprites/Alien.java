package plainsimple.spaceships.sprites;

import plainsimple.spaceships.sprites.Sprite;
import plainsimple.spaceships.util.BitmapData;
import plainsimple.spaceships.util.DrawParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 8/28/2015.
 */
public abstract class Alien extends Sprite {

    // frames to wait between firing bullets
    protected int bulletDelay;
    protected int framesSinceLastBullet = 0;

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

    public Alien(BitmapData bitmapData, int x, int y) {
        super(bitmapData, x, y);
    }

    private void initLevel3Alien() { // todo: move to Alien3 class
        startingY = y;
        hp = 60;
        bulletDelay = 500;
        bulletSpeed = -3.5f;
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        damage = 500;
        speedX = -2.0f;
    }

    // update/handle any actions sprite takes
    @Override
    public abstract void updateActions();

    // updates speedX and speedY of spaceship
    @Override
    public abstract void updateSpeeds();

    @Override
    public abstract void updateAnimations();

    // handles collision with specified sprite
    @Override
    public abstract void handleCollision(Sprite s);

    @Override
    public abstract ArrayList<DrawParams> getDrawParams();

    // fires bullet/projectile at specified sprite
    public abstract void fireBullet(Sprite s); // todo: unnecessary
}
