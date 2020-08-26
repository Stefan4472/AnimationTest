package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.Map;
import com.plainsimple.spaceships.helper.TileGenerator;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.GameEngineUtil;

import java.util.LinkedList;
import java.util.List;

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
    // speed of sprites scrolling across the screen (must be negative!)
    private double scrollSpeed = -0.0025f;
    // The player's spaceship
    private Spaceship spaceship;


    // Represents the possible states that the game can be in
    private enum GameState {
        STARTING,
        IN_PROGRESS,
        PLAYER_KILLED,
        FINISHED;
    }

    // relative speed of background scrolling to foreground scrolling
    public static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    public static final float SCORING_CONST = 800;

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;

    /* GameDriver logic TODO: REFACTOR */
    // grid of tile ID's instructing which sprites to initialize on screen
    private byte[][] tiles;
    // used to generate tiles based on pre-defined settings
    private Map map;
    // number of rows of sprites that fit on screen
    private static final int ROWS = 6;
    // number of tiles elapsed since last tiles was generated
    private int mapTileCounter = 0;
    // keeps track of tile spaceship was on last time tiles was updated
    private long lastTile = 0;
    // coordinates of upper-left of "window" being shown
    private float x = 0;  // TODO: MAKE DOUBLE
    // active generated obstacles
    private List<Sprite> obstacles = new LinkedList<>();
    // active generated coins
    private List<Sprite> coins = new LinkedList<>();
    // active generated aliens
    private List<Sprite> aliens = new LinkedList<>();
    // active projectiles on screen fired by aliens
    private List<Sprite> alienProjectiles = new LinkedList<>();
    // width (px) of the side of a tile
    private int tileWidth;

    /* Start GameEngine logic */
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
        spaceship = gameContext.createSpaceship(0, 0);  // TODO: START OFF INVISIBLE
        // Set this class to receive Spaceship events
        spaceship.setListener(this);

        // TODO: ANY WAY WE CAN PUT THE SPACESHIP INTO THE CONTEXT CONSTRUCTOR?
        gameContext.setPlayerSprite(spaceship);

        tileWidth = gameHeightPx / ROWS;
        tiles = new byte[ROWS][gameWidthPx / tileWidth];
        // todo: take String defining map as a parameter
        map = Map.parse(gameMode.getLevelData());

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
        obstacles.clear();
        coins.clear();
        aliens.clear();
        alienProjectiles.clear();
        tiles = new byte[ROWS][gameContext.getGameWidthPx() / tileWidth];
        map.restart();
        mapTileCounter = 0;
        x = 0;
        lastTile = 0;

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
    public GameUpdateMessage update() {
        GameUpdateMessage update_msg = new GameUpdateMessage();

        // Do nothing if paused
        if (isPaused) {
            return update_msg;
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

        /* Start GameDriver logic */
        // update x
        x += gameContext.getGameWidthPx() * scrollSpeed;

        // check if screen has progressed to render a new tile
        if (getWTile() != lastTile) {
            // add any non-empty tiles in the current column to the edge of the screen
            for (int i = 0; i < tiles.length; i++) {
                // process for adding Obstacles: count the number of adjacent obstacles in the row.
                // Set them all to EMPTY in the array. Construct the obstacle using this data
                if (tiles[i][mapTileCounter] == TileGenerator.OBSTACLE) {
                    int num_cols = 0;
                    for (int col = mapTileCounter; col < tiles[0].length && tiles[i][col] == TileGenerator.OBSTACLE; col++) {
                        num_cols++;
                        tiles[i][col] = TileGenerator.EMPTY;
                    }
                    obstacles.add(gameContext.createObstacle(
                            gameContext.getGameWidthPx() + getWOffset(),
                            i * tileWidth,
                            num_cols * tileWidth,
                            tileWidth
                    ));

                } else if (tiles[i][mapTileCounter] != TileGenerator.EMPTY) {
                    addMapTile(tiles[i][mapTileCounter], gameContext.getGameWidthPx() + getWOffset(), i * tileWidth, (float) scrollSpeed, spaceship);
                }
            }
            mapTileCounter++;

            // generate more sprites todo: only generate if all aliens have been killed, or all bosses, or etc. preventGeneration flag?
            if (mapTileCounter == tiles[0].length) {
                tiles = map.genNext((int) currDifficulty);
//                tiles = map.generateDebugTiles();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }

        // todo: improve
        GameEngineUtil.getAlienBullets(alienProjectiles, aliens);

        // check collisions between user-fired projectiles and relevant sprites
        for(Sprite projectile : spaceship.getProjectiles()) {
            GameEngineUtil.checkCollisions(projectile, aliens);
            GameEngineUtil.checkCollisions(projectile, obstacles);
            //GameEngineUtil.checkCollisions(projectile, alienProjectiles);
        }
        // check collisions with spaceship only if terminate = false
        if (!spaceship.terminate()) {
            GameEngineUtil.checkCollisions(spaceship, aliens);
            GameEngineUtil.checkCollisions(spaceship, obstacles);
            GameEngineUtil.checkCollisions(spaceship, coins);
            GameEngineUtil.checkCollisions(spaceship, alienProjectiles);
        }
        // update all other sprites
        GameEngineUtil.updateSprites(obstacles);
        GameEngineUtil.updateSprites(aliens);
        GameEngineUtil.updateSprites(coins);
        GameEngineUtil.updateSprites(alienProjectiles);

        // TODO: COLLECT DRAWPARAMS DURING THE SAME PASS AS THE UPDATES
        for (Sprite obstacle : obstacles) {
            obstacle.getDrawParams(update_msg.drawParams);
        }
        for (Sprite coin : coins) {
            coin.getDrawParams(update_msg.drawParams);
        }
        for (Sprite alien : aliens) {
            alien.getDrawParams(update_msg.drawParams);
        }
        for (Sprite alien_projectile : alienProjectiles) {
            alien_projectile.getDrawParams(update_msg.drawParams);
        }
        spaceship.getDrawParams(update_msg.drawParams);

        numUpdates++;
        return update_msg;
    }

    private void enterStartingState() {
        currDifficulty = 0;
        score = 0;
        scorePerSecond = 0.0;

        spaceship.reset();
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

    // current horizontal tile
    private long getWTile() {
        return (int) x / tileWidth;
    }

    // number of pixels from start of current tile
    private int getWOffset() {
        return (int) x % tileWidth;
    }

    // initializes sprite and adds to the proper list, given parameters
    private void addMapTile(int tileID, float x, float y, float scrollSpeed, Spaceship spaceship) throws IndexOutOfBoundsException {
        switch (tileID) {
            case TileGenerator.COIN:
                coins.add(gameContext.createCoin(
                        x,
                        y
                ));
                break;
            case TileGenerator.ALIEN:
                aliens.add(gameContext.createAlien(
                        x,
                        y,
                        scrollSpeed,
                        spaceship,
                        (int) currDifficulty
                ));
                break;
            case TileGenerator.ASTEROID: // todo: separate list for asteroids? could bounce off one another
                obstacles.add(gameContext.createAsteroid(
                        x,
                        y,
                        scrollSpeed,
                        (int) currDifficulty
                ));
                break;
            case TileGenerator.END_GAME:

                break;
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
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
