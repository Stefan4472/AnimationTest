package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
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

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;

    public GameContext(
            Context appContext,
            BitmapCache bitmapCache,
            AnimCache animCache,
            Sprite playerSprite,
            int gameWidthPx,
            int gameHeightPx) {
        this.appContext = appContext;
        this.bitmapCache = bitmapCache;
        this.animCache = animCache;
        this.playerSprite = playerSprite;
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

    public int getGameWidthPx() {
        return gameWidthPx;
    }

    public int getGameHeightPx() {
        return gameHeightPx;
    }
}
