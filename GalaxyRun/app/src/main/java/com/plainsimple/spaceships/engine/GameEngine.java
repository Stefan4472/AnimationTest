package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawRect;
import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.Map;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.helper.TileGenerator;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.FastQueue;
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

    private Map map;

    // Data for calculating FPS (TODO)
    private long startTime;
    private long numUpdates;

    // Represents the level of difficulty. Increases non-linearly over time
    private double currDifficulty;

    private boolean isPaused;
    private GameState currState;
    private int score;

    // Score gained per second of survival. Calculated as a function
    // of `currDifficulty`
    private double scorePerSecond;

    // TODO: NOTE: IN PROGRESS OF REWRITING OLD LOGIC
    // tracks duration of this game (non-paused)
    private GameTimer gameTimer;
    // speed of sprites scrolling across the screen (must be negative!)
    private double scrollSpeed = -0.0025f;
    // Distance (num pixels) scrolled so far this game
    private double scrollDistance;
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

    /* Store sprites according to type. TODO: GENERALIZE */
    private List<Sprite> coins = new LinkedList<>();
    private List<Sprite> obstacles = new LinkedList<>();
    private List<Sprite> aliens = new LinkedList<>();
    private List<Sprite> alienProjectiles = new LinkedList<>();
    private List<Sprite> playerProjectiles = new LinkedList<>();


    /* Start GameEngine logic */
    public GameEngine(
            Context appContext,
            BitmapCache bitmapCache,
            int gameWidthPx,
            int gameHeightPx
    ) {
        // Create BitmapCache
        this.bitmapCache = bitmapCache;
        animCache = new AnimCache(appContext, this.bitmapCache);

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                this.bitmapCache,
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

        map = new Map(gameContext);

        // Set state for new, un-started game
        currState = GameState.FINISHED;
    }

    private void resetGame() {
        startGame();
    }

    public void startGame() {
        currDifficulty = 0;
        score = 0;
        scorePerSecond = 0.0;
        scrollDistance = 0.0;

        spaceship.reset();
        obstacles.clear();
        coins.clear();
        aliens.clear();
        alienProjectiles.clear();
        playerProjectiles.clear();

        map.restart();

        // Move spaceship just off the left of the screen, centered vertically
        BitmapData ship_data = gameContext.getBitmapCache().getData(BitmapID.SPACESHIP);
        spaceship.setX(-ship_data.getWidth());
        spaceship.setY(gameContext.getGameHeightPx() / 2 - ship_data.getHeight() / 2);
        // Make non-controllable
        spaceship.setControllable(false);
        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(gameContext.getGameWidthPx() * 0.08);

        gameTimer = new GameTimer();
        gameTimer.start();

        setState(GameState.STARTING);
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
        return spaceship.getHealth();
    }

    // updates all game logic
    // adds any new sprites and generates a new set of sprites if needed
    public GameUpdateMessage update() {
        GameUpdateMessage update_msg = new GameUpdateMessage();

        // Do nothing if paused
        if (isPaused) {
            return update_msg;
        }
        // REVISE MAP GENERATION
        // GET DIFFICULTY AND SCROLLSPEED WORKING AS DESIRED
        // Set up events + GameActivity input
        GameTime game_time = gameTimer.recordUpdate();
//        Log.d("GameEngine", String.format("%d %d %d",
//                game_time.getCurrTimeMs(), game_time.getRunTimeMs(), game_time.getMsSincePrevUpdate()));
        currDifficulty = calcDifficulty(game_time.getRunTimeMs());
        scrollSpeed = calcScrollSpeed();
        scrollDistance += game_time.getMsSincePrevUpdate() / 1000.0 * scrollSpeed;

        // Debug prints, every 100 frames
        if (numUpdates != 0 && numUpdates % 100 == 0) {
            Log.d("GameEngine", String.format(
                    "Spaceship at %f, %f", spaceship.getX(), spaceship.getY()
            ));
            Log.d("GameEngine", String.format(
                    "Game runtime = %d ms", game_time.getRunTimeMs() / 1000
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
//                if (scrollSpeed > -0.0001f) {
//                    setState(GameState.FINISHED);
//                }
            }
            case FINISHED: {

            }
        }

        FastQueue<Sprite> created_sprites = new FastQueue<>();

        // TODO: WAY OF CALCULATING TIME SINCE PREVIOUS UPDATE (WHILE ACCOUNTING FOR PAUSE/RESUME)
        // TODO: PROVIDE IGAMEENGINE INTERFACE REFERENCE?
        UpdateContext update_context = new UpdateContext(
                game_time,
                currDifficulty,
                scrollSpeed,
                created_sprites,
                update_msg.events,
                update_msg.sounds
        );

        if (currState == GameState.IN_PROGRESS) {
            map.update(update_context, scrollDistance);
        }

        GameEngineUtil.updateSprite(spaceship, update_context);

        // check collisions between user-fired projectiles and relevant sprites
        for(Sprite projectile : playerProjectiles) {
            GameEngineUtil.checkCollisions(projectile, aliens, update_context);
            GameEngineUtil.checkCollisions(projectile, obstacles, update_context);
            //GameEngineUtil.checkCollisions(projectile, alienProjectiles);
        }
        // check collisions with spaceship
        if (!spaceship.shouldTerminate()) {
            GameEngineUtil.checkCollisions(spaceship, aliens, update_context);
            GameEngineUtil.checkCollisions(spaceship, obstacles, update_context);
            GameEngineUtil.checkCollisions(spaceship, coins, update_context);
            GameEngineUtil.checkCollisions(spaceship, alienProjectiles, update_context);
        }
        // update all other sprites
        GameEngineUtil.updateSprites(obstacles, update_context);
        GameEngineUtil.updateSprites(aliens, update_context);
        GameEngineUtil.updateSprites(coins, update_context);
        GameEngineUtil.updateSprites(alienProjectiles, update_context);
        GameEngineUtil.updateSprites(playerProjectiles, update_context);

        // TODO: COLLECT DRAWPARAMS DURING THE SAME PASS AS THE UPDATES
        for (Sprite obstacle : obstacles) {
            obstacle.getDrawParams(update_msg.drawParams);
            // Draw hitbox
            DrawRect hitbox_rect = new DrawRect(Color.RED, Paint.Style.STROKE, 2);
            hitbox_rect.setBounds(obstacle.getHitbox());
            update_msg.drawParams.push(hitbox_rect);
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

        // Add all created sprites
        // TODO: REFACTOR THIS OUT
        for (Sprite sprite : created_sprites) {
            switch (sprite.getSpriteType()) {
                case ALIEN: {
                    aliens.add(sprite);
                    break;
                }
                case ALIEN_BULLET: {
                    alienProjectiles.add(sprite);
                    break;
                }
                case ASTEROID: {
                    obstacles.add(sprite);
                    break;
                }
                case BULLET: {
                    playerProjectiles.add(sprite);
                    break;
                }
                case COIN: {
                    coins.add(sprite);
                    break;
                }
                case OBSTACLE: {
                    obstacles.add(sprite);
                    break;
                }
                default: {
                    throw new IllegalArgumentException(
                            String.format("Unsupported SpriteType %s", sprite.getSpriteType().toString())
                    );
                }
            }
        }
        Log.d("GameEngine", String.format(
                "There were %d sprites created", created_sprites.getSize())
        );
        numUpdates++;
        return update_msg;
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
        currState = newState;
        // TODO: ENQUEUE EVENT via SETSTATE() METHOD
//        if (gameEventsListener != null) {
//            gameEventsListener.onGameStarted();
//        }
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
        return gameContext.getGameWidthPx() * 0.05;
        // Spaceship destroyed: slow down scrolling to a halt.
//        if (currState == GameState.PLAYER_KILLED) {
//            return scrollSpeed / 1.03f;
//        } else { // Normal scrolling progression
//            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
////            scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
//            return currDifficulty / 10;
//        }
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
