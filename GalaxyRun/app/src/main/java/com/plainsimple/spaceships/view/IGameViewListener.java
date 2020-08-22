package com.plainsimple.spaceships.view;

/**
 * Created by Stefan on 8/22/2020.
 */

public interface IGameViewListener {
    void onGameStarted();
    void onGameFinished();
    void onHealthChanged(int healthChange);
}
