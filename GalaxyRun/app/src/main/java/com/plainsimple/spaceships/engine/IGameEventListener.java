package com.plainsimple.spaceships.engine;

/**
 * Interface implemented by objects that want to be notified
 * of in-game events, e.g. player health change, etc.
 */

public interface IGameEventListener {
    void onGameStarted();
    void onGameFinished();
    void onHealthChanged(int healthChange);

    // TODO: REFACTOR THIS OUT!!!
    // fired when the GameView's dimensions have been determined (setSurfaceSize)
    // returns an int, which is the screenHeight the game should be set
    // this allows the height of the screen used to be different that the full
    // height of the GameView
    int onGameViewSurfaced(int screenHeight);
}
