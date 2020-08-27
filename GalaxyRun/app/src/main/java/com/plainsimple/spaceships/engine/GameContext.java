package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
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
    private AnimCache animCache;
    private Sprite playerSprite;
    private int gameWidthPx;
    private int gameHeightPx;
    private int nextSpriteId = 1;

    // TODO: PROVIDE `ISINBOUNDS()` METHOD?
    public GameContext(
            Context appContext,
            BitmapCache bitmapCache,
            AnimCache animCache,
            int gameWidthPx,
            int gameHeightPx) {
        this.appContext = appContext;
        this.bitmapCache = bitmapCache;
        this.animCache = animCache;
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
    }

    public Context getAppContext() {
        return appContext;
    }

    public BitmapCache getBitmapCache() {
        return bitmapCache;
    }

    public AnimCache getAnimCache() {
        return animCache;
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
    public Alien createAlien(double x, double y, int difficulty) {
        return new Alien(nextSpriteId++, x, y, difficulty, this);
    }

    public AlienBullet createAlienBullet(BitmapData bitmapData, double x, double y, float targetX, float targetY) {
        return new AlienBullet(nextSpriteId++, bitmapData, x, y, targetX, targetY, this);
    }

    public Asteroid createAsteroid(double x, double y, float scrollSpeed, int difficulty) {
        return new Asteroid(nextSpriteId++, x, y, scrollSpeed, difficulty, this);
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
