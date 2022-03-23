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
}
