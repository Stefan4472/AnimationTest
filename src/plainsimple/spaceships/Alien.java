package plainsimple.spaceships;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 * Created by Stefan on 8/28/2015.
 */
public abstract class Alien extends Sprite {

    // ms to wait between firing bullets
    protected double bulletDelay;
    protected long lastFiredBullet = 0;

    protected double bulletSpeed;
    protected ArrayList<Sprite> projectiles = new ArrayList<>();

    // frames since alien was constructed
    // used for calculating trajectory
    protected int elapsedFrames = 1;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    protected double startingY;

    public ArrayList<Sprite> getProjectiles() {
        return projectiles;
    }

    public Alien(String imageName, Board board) {
        super(imageName, board);
    }

    public Alien(String imageName, double x, double y, Board board) {
        super(imageName, x, y, board);
    }

    private void initLevel3Alien() {
        startingY = y;
        hp = 60 + (int) board.getDifficulty() / 5;
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

    // update's speedX and speedY of spaceship
    @Override
    abstract void updateSpeeds();

    // handles collision with specified sprite
    @Override
    public void handleCollision(Sprite s) {
        if (!(s instanceof AlienBullet)) {
            if (s instanceof Bullet || s instanceof Rocket) {
                board.incrementScore(s.damage);
            }
            hp -= s.damage;
            if (hp < 0) { // todo: death animation
                vis = false;
                // collides = false;
                hp = 0;
            }
        }
    }

    // renders sprite to Graphics object
    @Override
    public void render(Graphics2D g, ImageObserver o) {
        g.drawImage(defaultImage, (int) x, (int) y, o); // todo: layered animations?
    }

    // fires bullet/projectile at specified sprite
    abstract void fireBullet(Sprite s);
}
