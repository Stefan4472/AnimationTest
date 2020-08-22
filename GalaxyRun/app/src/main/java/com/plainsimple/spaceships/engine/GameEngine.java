package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.plainsimple.spaceships.helper.Background;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.GameDriver;
import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.store.ArmorType;
import com.plainsimple.spaceships.store.CannonType;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.util.GameEngineUtil;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.ScoreDisplay;

import plainsimple.spaceships.R;

/**
 * Core game logic.
 */

public class GameEngine implements IGameController, Spaceship.SpaceshipListener {

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
//    private GameTimer gameTimer;

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    // The player's spaceship
//    private Spaceship spaceship;

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
    // selected fire mode (bullet or rocket)
    private Spaceship.FireMode selectedFireMode = Spaceship.FireMode.BULLET;
    // runs sprite generation and updating
    private GameDriver gameDriver;
    // speed of sprites scrolling across the screen (must be negative!)
    public static float scrollSpeed = -0.0025f;
    // spaceship
    private Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    private static final float SCORING_CONST = 800;

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
        if (gameStarted) {
            if (!spaceshipDestroyed) {
                // increment difficulty by amount specified in difficultyLevel
                difficulty += difficultyLevel.getPerFrameIncrease();
                score += 1 + difficulty / SCORING_CONST;
            }
            updateScrollSpeed();
        }
        updateSpaceship();
        gameDriver.update((int) difficulty, scrollSpeed, spaceship);
        spaceship.updateAnimations();
    }

    private void updateSpaceship() {
        // move spaceship to initial position
        if (!gameStarted && spaceship.getX() > screenWidth / 4) {
            gameTimer.start();
            gameStarted = true;
            spaceship.setX(screenWidth / 4);
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

    // initializes all objects required to start a new game
    private void initNewGame() {
        // calculate scaling factor using spaceship_sprite height as a baseline
        Bitmap spaceship_bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship);
        float scalingFactor = (screenHeight / 6.0f) / (float) spaceship_bmp.getHeight();
        BitmapCache.setScalingFactor(scalingFactor);
        // get spaceship image data from cache
        BitmapData ship_data = BitmapCache.getData(BitmapID.SPACESHIP, context);

        // initialize spaceship just off the screen centered vertically
        spaceship = new Spaceship(-ship_data.getWidth(), screenHeight / 2 - ship_data.getHeight() / 2, context);
        // set spaceship equipment based on settings in GameActivity
        spaceship.setCannonType(CannonType.CANNON_0);
        spaceship.setRocketType(RocketType.ROCKET_0);
        spaceship.setArmorType(ArmorType.ARMOR_0);
        // set this class to receive Spaceship events
        spaceship.setListener(this);

        gameDriver = new GameDriver(context, screenWidth, screenHeight, gameMode.getLevelData());
        gameFinished = false;
        score = 0;
    }

    // resets all elements and fields so that a new game can begin
    public void restartGame() {
        spaceship.reset();
        spaceship.setX(-spaceship.getWidth());
        spaceship.setY(screenHeight / 2 - spaceship.getHeight() / 2);
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
