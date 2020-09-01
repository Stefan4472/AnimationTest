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
 * Created by Stefan on 8/24/2020.
 */

public class GameContext {
    private Context appContext;
    private BitmapCache bitmapCache;
    private AnimFactory animFactory;
    private Sprite playerSprite;
    private int gameWidthPx;
    private int gameHeightPx;
    private int nextSpriteId = 1;

    // TODO: PROVIDE `ISINBOUNDS()` METHOD?
    public GameContext(
            Context appContext,
            BitmapCache bitmapCache,
            AnimFactory animCache,
            int gameWidthPx,
            int gameHeightPx) {
        this.appContext = appContext;
        this.bitmapCache = bitmapCache;
        this.animFactory = animCache;
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
    }

    public Context getAppContext() {
        return appContext;
    }

    public BitmapCache getBitmapCache() {
        return bitmapCache;
    }

    public AnimFactory getAnimFactory() {
        return animFactory;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public int getGameWidthPx() {
        return gameWidthPx;
    }

    public int getGameHeightPx() {
        return gameHeightPx;
    }

    /* Begin Sprite creation factory methods */
    // NOTE: UNFORTUNATELY WE CAN'T JUST PASS IN A `VARARGS`, LIKE IN PYTHON :(
//    Sprite createSprite()
    public Alien createAlien(double x, double y, double difficulty) {
        return new Alien(nextSpriteId++, x, y, difficulty, this);
    }

    public AlienBullet createAlienBullet(double x, double y, double targetX, double targetY) {
        return new AlienBullet(nextSpriteId++, x, y, targetX, targetY, this);
    }

    public Asteroid createAsteroid(double x, double y, double difficulty) {
        return new Asteroid(nextSpriteId++, x, y, difficulty, this);
    }

    public Bullet createBullet(double x, double y) {
        return new Bullet(nextSpriteId++, x, y, this);
    }

    public Coin createCoin(double x, double y) {
        return new Coin(nextSpriteId++, x, y, this);
    }

    public Obstacle createObstacle(double x, double y, int width, int height) {
        return new Obstacle(nextSpriteId++, x, y, width, height, this);
    }

    public Spaceship createSpaceship(double x, double y) {
        return new Spaceship(nextSpriteId++, x, y, this);
    }
}
