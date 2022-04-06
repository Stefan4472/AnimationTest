package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.engine.map.Map;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.FontCache;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.AlienBullet;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Bullet;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;

import java.util.Random;

/**
 * Context.
 */

public class GameContext {
    public final Context appContext;
    public final boolean inDebugMode;
    public final BitmapCache bitmapCache;
    public final FontCache fontCache;
    public final AnimFactory animFactory;  // TODO: remove (unnecessary)
    public final Random rand;
    public final int gameWidthPx;
    public final int gameHeightPx;
    public final int screenWidthPx;
    public final int screenHeightPx;
    public final int fullHealth;
    // Width of a (square) tile in the game
    public final int tileWidthPx;

    public GameContext(
            Context appContext,
            boolean inDebugMode,
            BitmapCache bitmapCache,
            FontCache fontCache,
            AnimFactory animCache,
            Random rand,
            int gameWidthPx,
            int gameHeightPx,
            int screenWidthPx,
            int screenHeightPx,
            int fullHealth) {
        this.appContext = appContext;
        this.inDebugMode = inDebugMode;
        this.bitmapCache = bitmapCache;
        this.fontCache = fontCache;
        this.animFactory = animCache;
        this.rand = rand;
        this.gameWidthPx = gameWidthPx;
        this.gameHeightPx = gameHeightPx;
        this.screenWidthPx = screenWidthPx;
        this.screenHeightPx = screenHeightPx;
        this.fullHealth = fullHealth;
        tileWidthPx = gameHeightPx / Map.NUM_ROWS;
    }
}
