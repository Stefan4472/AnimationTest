package com.plainsimple.spaceships.view;

/**
 * Event callbacks triggered by GameView.
 */

public interface IGameViewListener {
    void onGameStarted();
    void onGameFinished();
    void onHealthChanged(int healthChange);
}
