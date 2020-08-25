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

    // TODO: NOTE: IN PROGRESS OF REWRITING OLD LOGIC
    // defines this game's GameMode  TODO: SIMPLIFY
    private static final String ENDLESS_0_STR = "ENDLESS_0" + ":" + "Endless:" + "EASY"
            + ":" + 0 + ":" + 2000 + ":" + 4000 + ":" + 7000 + ":" + 12000 + ":" + 25000 + ":" +
            "Survive! The farther you go the harder it gets and the more coins and points you'll earn!"
            + ":" + "loop(INFINITE,genRandom)";
    private GameMode gameMode = GameMode.fromString(ENDLESS_0_STR);
    // tracks duration of this game (non-paused)
    private GameTimer gameTimer = new GameTimer();
    // runs sprite generation and updating
    private GameDriver gameDriver;
    // speed of sprites scrolling across the screen (must be negative!)
    private double scrollSpeed = -0.0025f;
    // The player's paceship
    private Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    public static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    public static final float SCORING_CONST = 800;


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

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                bitmapCache,
                animCache,
                gameWidthPx,
                gameHeightPx
        );

        // Create Spaceship and init just off the screen, centered vertically
        spaceship = new Spaceship(0, 0, gameContext);  // TODO: START OFF INVISIBLE
        // Set this class to receive Spaceship events
        spaceship.setListener(this);

        // TODO: ANY WAY WE CAN PUT THE SPACESHIP INTO THE CONTEXT CONSTRUCTOR?
        gameContext.setPlayerSprite(spaceship);

        gameDriver = new GameDriver(
                gameContext,
                gameContext.getGameWidthPx(),
                gameContext.getGameHeightPx(),
                gameMode.getLevelData()
        );

        // Set state for new, un-started game
        currState = GameState.FINISHED;
    }

//    private void initializeGame() {
//
//
//    }

    private void resetGame() {
        startGame();
    }

    public void startGame() {
        enterStartingState();
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
        return spaceship.getHP();
    }

    // updates all game logic
    // adds any new sprites and generates a new set of sprites if needed
    public void update() {
        // Do nothing if paused
        if (isPaused) {
            return;
        }

        // TODO: USE TIME, NOT NUMBER OF FRAMES, FOR EVERYTHING!
        // TODO: DETERMINE MS SINCE LAST UPDATE

        long run_time = gameTimer.getMsTracked();
        currDifficulty = calcDifficulty(run_time);
        scrollSpeed = calcScrollSpeed();

        // Debug prints, every 100 frames
        if (numUpdates != 0 && numUpdates % 100 == 0) {
            Log.d("GameEngine", String.format(
                    "Spaceship at %f, %f", spaceship.getX(), spaceship.getY()
            ));
            Log.d("GameEngine", String.format(
                    "Game runtime = %d ms", run_time
            ));
        }

        // TODO: THIS IS WHERE WE RUN OUR STATE-CHANGE-DETECTION LOGIC
        switch (currState) {
            case STARTING: {
                // Starting position reached
                if (spaceship.getX() > gameContext.getGameWidthPx() / 4) {
                    enterInProgressState();
                }
            }
            case IN_PROGRESS: {
                // Increment score
                score += 1 + currDifficulty / SCORING_CONST;
            }
            case PLAYER_KILLED: {
                // TODO: DON'T WE NEED TO CHECK FOR PLAYER_INVISIBLE FIRST, THEN SLOW DOWN, THEN MARK FINISHED?
                // TODO: I THINK WE NEED A 'SLOWING_DOWN` STATE
                // As soon as the Player is killed, the scrollSpeed
                // slows down to zero.
                // Go to `FINISHED` once scrollSpeed hits near-zero.
                if (scrollSpeed > -0.0001f) {
                    setState(GameState.FINISHED);
                }
            }
            case FINISHED: {

            }

        }

        updateSpaceship();
        gameDriver.update((int) currDifficulty, scrollSpeed, spaceship);
        spaceship.updateAnimations();
        numUpdates++;

    }

    private void enterStartingState() {
        currDifficulty = 0;
        score = 0;
        scorePerSecond = 0.0;

        spaceship.reset();
        gameDriver.reset();
        gameTimer.reset();

        // Move spaceship just off the left of the screen, centered vertically
        BitmapData ship_data = gameContext.getBitmapCache().getData(BitmapID.SPACESHIP);
        spaceship.setX(-ship_data.getWidth());
        spaceship.setY(gameContext.getGameHeightPx() / 2 - ship_data.getHeight() / 2);

        // Make non-controllable
        spaceship.setControllable(false);
        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(0.01f);

        gameTimer.start();
        setState(GameState.STARTING);
    }

    private void enterInProgressState() {
        spaceship.setControllable(true);
        spaceship.setSpeedX(0);
        spaceship.setX(gameContext.getGameWidthPx() / 4);
        setState(GameState.IN_PROGRESS);
        // TODO: START OBSTACLE GENERATION?
    }

    private void enterKilledState() {
        setState(GameState.PLAYER_KILLED);
    }

    private void enterFinishedState() {
        setState(GameState.FINISHED);
    }

    private void setState(GameState newState) {
        Log.d("GameEngine", String.format("Entering state %s", newState.toString()));
        // TODO: ENQUEUE EVENT via SETSTATE() METHOD
//        if (gameEventsListener != null) {
//            gameEventsListener.onGameStarted();
//        }
    }

    private void updateSpaceship() {
        spaceship.updateSpeeds();
        spaceship.move();
        spaceship.updateActions();
        spaceship.updateAnimations();
        GameEngineUtil.updateSprites(spaceship.getProjectiles());
    }

    /*
    Calculate difficulty "magic number" based on how long the game
    has run.
     */
    private static double calcDifficulty(long gameRuntimeMs) {
        // TODO: THIS IS JUST A PLACEHOLDER
        return gameRuntimeMs / 1000;
//        difficulty += 0.6;
    }

    /*
    Calculates "scrollspeed" based on current scroll speed, game state,
    and difficulty.
    TODO: AGAIN, NEED TO BASE ON TIME PASSED, NOT NUMBER OF FRAMES
     */
    private double calcScrollSpeed() {
        // Spaceship destroyed: slow down scrolling to a halt.
        if (currState == GameState.PLAYER_KILLED) {
            return scrollSpeed / 1.03f;
        } else { // Normal scrolling progression
            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
//            scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
            return currDifficulty / 10;
        }
    }

    // TODO: REMOVE
    @Override
    public void onHealthChanged(int change) {
        Log.d("GameView.java", "Received onHealthChanged for " + change);
    }

    @Override // handle spaceship becoming invisible
    public void onInvisible() {
        Log.d("GameView.java", "Received onInvisible");
        enterFinishedState();
    }
}
