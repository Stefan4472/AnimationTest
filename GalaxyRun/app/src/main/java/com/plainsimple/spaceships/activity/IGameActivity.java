package com.plainsimple.spaceships.activity;

/**
 * Functions provided by GameActivity.
 */

public interface IGameActivity {
    int calcPlayableHeight(int surfaceHeight);
    int calcPlayableWidth(int surfaceWidth);
//    void playSound();
    void onSizeSet(int widthPx, int heightPx);
}
