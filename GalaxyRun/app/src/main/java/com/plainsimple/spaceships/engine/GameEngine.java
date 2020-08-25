package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.util.Log;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.GameDriver;
import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.GameEngineUtil;

import java.util.concurrent.TimeUnit;

import plainsimple.spaceships.R;

/**
 * Core game logic.
 */

public class GameEngine implements IGameController, Spaceship.SpaceshipListener {

    private GameContext gameContext;
    private BitmapCache bitmapCache;
    private AnimCache animCache;

    // Data for calculating FPS (TODO)
    private long startTime;
    private long numUpdates;

    // Represents the level of difficulty. Increases non-linearly over time
    private double currDifficulty;

    private boolean isPaused;
    private GameState currState;
    public int score;
    // Score gained per second of survival. Calculated as a function
    // of `currDifficulty`
    private double scorePerSecond;
    // Stores the amount of time the current run has lasted
//    private GameTimer gameTimer;


    // The player's spaceship
//    private Spaceship spaceship;

    // Represents the possible states that the game can be in
    private enum GameState {
        STARTING,
        IN_PROGRESS,
        PLAYER_KILLED,
        FINISHED
    }

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;

    public GameEngine(Context appContext, int gameWidthPx, int gameHeightPx) {
        // Create BitmapCache
        bitmapCache = new BitmapCache(
                appContext, gameWidthPx, gameHeightPx);
        animCache = new AnimCache(appContext, bitmapCache);

        // Create Spaceship and init just off the screen, centered vertically
        BitmapData ship_data = gameContext.getBitmapCache().getData(BitmapID.SPACESHIP);
        spaceship = new Spaceship(
                -ship_data.getWidth(),
                gameContext.getGameHeightPx() / 2 - ship_data.getHeight() / 2,
                gameContext
        );
        // Set this class to receive Spaceship events
        spaceship.setListener(this);

        gameDriver = new GameDriver(
                gameContext,
                gameContext.getGameWidthPx(),
                gameContext.getGameHeightPx(),
                gameMode.getLevelData()
        );

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                bitmapCache,
                animCache,
                spaceship,
                gameWidthPx,
                gameHeightPx
        );
    }

    /* Start implementation of IGameController interface. */
    // TODO: CONCURRENT QUEUE INPUT
    // COLLECT DRAWCALLS, EVENTS ON CONCURRENT QUEUE
    @Override
    public void inputStartShooting() {

    }

    @Override
    public void inputEndShooting() {

    }

    @Override
    public void inputStartMoveUp() {

    }

    @Override
    public void inputStartMoveDown() {

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

    public int getPlayerHealth() {
        return 100; // TODO
    }

    /* Start old logic, which will be gradually refactored/rewritten */
    private float difficulty;
    // whether game components have been initialized
    private boolean initialized;
    // whether game has started (spaceship has reached starting position)
    private boolean gameStarted;
    // whether the spaceship has been destroyed
    private boolean spaceshipDestroyed;
    // whether game is completely finished and screen has come to a halt
    private boolean gameFinished;
    // defines this game's GameMode
    private GameMode gameMode;
    // tracks duration of this game (non-paused)
    private GameTimer gameTimer = new GameTimer();
    // runs sprite generation and updating
    public GameDriver gameDriver;
    // speed of sprites scrolling across the screen (must be negative!)
    public static float scrollSpeed = -0.0025f;
    // spaceship
    public Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    public static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    public static final float SCORING_CONST = 800;

    // this game's level of difficulty (default to EASY)
    private Difficulty difficultyLevel = Difficulty.EASY;

    // available difficulty levels with their difficulty increments per frame
    public enum Difficulty {
        EASY(0.6f), MEDIUM(1.0f), HARD(1.4f);

        private float perFrameIncrease;
        public float getPerFrameIncrease() {
            return perFrameIncrease;
        }
        Difficulty(float perFrameIncrease) {
            this.perFrameIncrease = perFrameIncrease;
        }
    }

    // listener passed in by GameActivity
    private IGameEventListener gameEventsListener;

    // updates all game logic
    // adds any new sprites and generates a new set of sprites if needed
    public void update() {
        try {
            TimeUnit.MILLISECONDS.sleep(15);
        } catch (InterruptedException e) {

        }
//        if (gameStarted) {
//            if (!spaceshipDestroyed) {
//                // increment difficulty by amount specified in difficultyLevel
//                difficulty += difficultyLevel.getPerFrameIncrease();
//                score += 1 + difficulty / SCORING_CONST;
//            }
//            updateScrollSpeed();
//        }
//        updateSpaceship();
//        gameDriver.update((int) difficulty, scrollSpeed, spaceship);
//        spaceship.updateAnimations();
    }

    private void updateSpaceship() {
        // move spaceship to initial position
        if (!gameStarted && spaceship.getX() > gameContext.getGameWidthPx() / 4) {
            gameTimer.start();
            gameStarted = true;
            spaceship.setX(gameContext.getGameWidthPx() / 4);
            spaceship.setSpeedX(0);
            spaceship.setControllable(true);
            if (gameEventsListener != null) {
                gameEventsListener.onGameStarted();
            }
        }
        spaceship.updateSpeeds();
        spaceship.move();
        spaceship.updateActions();
        GameEngineUtil.updateSprites(spaceship.getProjectiles());
    }

    // calculates scrollspeed based on difficulty
    public void updateScrollSpeed() {
        // spaceship destroyed: slow down scrolling to a halt and fire onGameFinished when scrollspeed = 0
        if (spaceshipDestroyed) {
            scrollSpeed /= 1.03f;
            if (scrollSpeed > -0.0001f) {
                gameFinished = true;
                Log.d("GameView.java", "OnGameFinished()");
                gameEventsListener.onGameFinished();
            }
        } else { // normal scrolling progression
            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
            scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
        }
    }

    @Override // handle spaceship changing health (send back to GameEventsListener)
    public void onHealthChanged(int change) {
        Log.d("GameView.java", "Received onHealthChanged for " + change);
        gameEventsListener.onHealthChanged(change);
    }

    @Override // handle spaceship becoming invisible
    public void onInvisible() {
        Log.d("GameView.java", "Received onInvisible");
        spaceshipDestroyed = true;
    }

    // resets all elements and fields so that a new game can begin
    public void restartGame() {
        spaceship.reset();
        spaceship.setX(-spaceship.getWidth());
        spaceship.setY(gameContext.getGameHeightPx() / 2 - spaceship.getHeight() / 2);
        gameDriver.reset();
        gameTimer.reset();
        spaceshipDestroyed = false;
        gameStarted = false;
        gameFinished = false;
        difficulty = 0;
        score = 0;
    }

    // sets difficultyLevel of the game
    public void setDifficultyLevel(Difficulty difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        Log.d("GameView", "Difficulty Level set to " + difficultyLevel);
    }

    public void setGameEventsListener(IGameEventListener gameEventsListener) {
        this.gameEventsListener = gameEventsListener;
    }

    public void incrementScore(int toAdd) {
        score += toAdd;
    }

    public static float getScrollSpeed() {
        return scrollSpeed;
    }
}
