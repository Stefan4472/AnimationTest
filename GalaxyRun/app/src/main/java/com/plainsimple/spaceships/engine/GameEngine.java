package com.plainsimple.spaceships.engine;

import android.content.Context;

import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.stats.GameTimer;

/**
 * Core game logic.
 */

public class GameEngine implements IGameController {

    // Reference to app context
    private Context context;

    // Playable screen dimensions
    private int screenWidth;
    private int screenHeight;

    // Represents the level of difficulty. Increases non-linearly over time
    private double currDifficulty;

    private boolean isPaused;
    private GameState currState;
    private int score;
    // Score gained per second of survival. Calculated as a function
    // of `currDifficulty`
    private double scorePerSecond;
    // Stores the amount of time the current run has lasted
    private GameTimer gameTimer;

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    // The player's spaceship
    private Spaceship spaceship;

    // Represents the possible states that the game can be in
    private enum GameState {
        STARTING,
        IN_PROGRESS,
        PLAYER_KILLED,
        FINISHED
    }

    public GameEngine(
            Context context,
            int screenWidth,
            int screenHeight
    ) {
        this.context = context;
        setScreenSize(screenWidth, screenHeight);
    }

    /* Start implementation of IGameController interface. */
    @Override
    public void inputStartShooting() {

    }

    @Override
    public void inputEndShooting() {

    }

    @Override
    public void inputMoveUp() {

    }

    @Override
    public void inputMoveDown() {

    }

    @Override
    public void inputStopMoving() {

    }

    @Override
    public void inputPause() {

    }

    @Override
    public void inputResume() {

    }

    @Override
    public void inputRestart() {

    }

    @Override
    public void setScreenSize(int width, int height) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        // TODO: SCALING LOGIC
    }

}
