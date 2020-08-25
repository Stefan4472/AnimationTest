package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;

/**
 * Created by Stefan on 8/24/2020.
 */

public class GameContext {
    private Context appContext;
    private BitmapCache bitmapCache;
    private AnimCache animCache;
    private int gameWidthPx;
    private int gameHeightPx;

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;

    public GameContext(Context appContext, BitmapCache bitmapCache,
            AnimCache animCache, int gameWidthPx, int gameHeightPx) {
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

    public int getGameWidthPx() {
        return gameWidthPx;
    }

    public int getGameHeightPx() {
        return gameHeightPx;
    }
}
