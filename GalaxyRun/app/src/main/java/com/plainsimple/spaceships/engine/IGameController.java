package com.plainsimple.spaceships.engine;

/**
 * Interface for controlling the game. Meant to be very simple to use.
 */

public interface IGameController {
    void inputStartShooting();
    void inputEndShooting();
    void inputStartMoveUp();
    void inputStartMoveDown();
    void inputStopMoving();
    void inputPause();
    void inputResume();
    void inputRestart();
}
