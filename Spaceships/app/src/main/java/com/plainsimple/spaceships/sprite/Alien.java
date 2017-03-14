package com.plainsimple.spaceships.sprite;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawParams;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 8/28/2015.
 */
public abstract class Alien extends Sprite {

    // frames to wait between firing bullets
    protected int bulletDelay;
    protected int framesSinceLastBullet = 0;

    protected float bulletSpeed;
    protected List<Sprite> projectiles = new LinkedList<>();

    // frames since alien was constructed
    // used for calculating trajectory
    protected int elapsedFrames = 1;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    protected float startingY;

    public List<Sprite> getProjectiles() {
        return projectiles;
    }
    public List<Sprite> getAndClearProjectiles() {
        List<Sprite> copy = new LinkedList<>();
        for(Sprite p : projectiles) {
            copy.add(p);
        }
        projectiles.clear();
        return copy;
    }

    public Alien(BitmapData bitmapData, float x, float y) {
        super(bitmapData, x, y);
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
    public abstract void handleCollision(Sprite s, int damage);

    @Override
    public abstract List<DrawParams> getDrawParams();

    // fires bullet/projectile at specified sprite
    public abstract void fireBullet(Sprite s); // todo: unnecessary
}
