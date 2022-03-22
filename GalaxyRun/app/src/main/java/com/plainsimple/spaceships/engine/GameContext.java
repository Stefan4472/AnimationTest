package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.AlienBullet;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Bullet;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;

/**
 * Context.
 */

public class GameContext {
    public final Context appContext;
    public final BitmapCache bitmapCache;
    public final AnimFactory animFactory;
    public final int gameWidthPx;
    public final int gameHeightPx;
    public final int screenWidthPx;
    public final int screenHeightPx;
    public final int fullHealth;
    private int nextSpriteId = 1;
    // TODO: add a Random instance

    public GameContext(
            Context appContext,
            BitmapCache bitmapCache,
            AnimFactory animCache,
            int gameWidthPx,
            int gameHeightPx,
            int screenWidthPx,
            int screenHeightPx,
            int fullHealth) {
        this.appContext = appContext;
        this.bitmapCache = bitmapCache;
        this.animFactory = animCache;
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
        this.screenWidthPx = screenWidthPx;
        this.screenHeightPx = screenHeightPx;
        this.fullHealth = fullHealth;
    }

    //
    public int getNextSpriteId() {
        return nextSpriteId++;
    }

    /* Begin Sprite creation factory methods */
    // TODO: probably don't need these methods
    public Alien createAlien(double x, double y, double difficulty) {
        return new Alien(getNextSpriteId(), x, y, difficulty, this);
    }

    public AlienBullet createAlienBullet(double x, double y, double targetX, double targetY) {
        return new AlienBullet(getNextSpriteId(), x, y, targetX, targetY, this);
    }

//    public Asteroid createAsteroid(double x, double y, double difficulty) {
//        return new Asteroid(getNextSpriteId(), x, y, difficulty, this);
//    }

    public Bullet createBullet(double x, double y) {
        return new Bullet(getNextSpriteId(), x, y, this);
    }

    public Coin createCoin(double x, double y) {
        return new Coin(getNextSpriteId(), x, y, this);
    }

    public Obstacle createObstacle(double x, double y, int width, int height) {
        return new Obstacle(getNextSpriteId(), x, y, width, height, this);
    }
}
