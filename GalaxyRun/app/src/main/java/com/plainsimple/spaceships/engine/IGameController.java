package com.plainsimple.spaceships.engine;

/**
 * Interface for controlling the game. Meant to be very simple to use.
 */

public interface IGameController {
    void inputStartGame();
    void inputPauseGame();
    void inputResumeGame();
    void inputRestartGame();
    void inputStartShooting();
    void inputStopShooting();
    void inputStartMoveUp();
    void inputStartMoveDown();
    void inputStopMoving();
}
