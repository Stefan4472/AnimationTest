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
import com.plainsimple.spaceships.helper.DrawParams;
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
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 */

public class GameEngine implements IGameController, Spaceship.SpaceshipListener {

    private GameContext gameContext;
    private BitmapCache bitmapCache;
    private AnimCache animCache;

    private Map map;

    // Data for calculating FPS (TODO)
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

    // Queue for game input
    private ConcurrentLinkedQueue<GameInput.InputID> gameInputQueue;

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
        gameInputQueue = new ConcurrentLinkedQueue<>();

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                this.bitmapCache,
                animCache,
                gameWidthPx,
                gameHeightPx
        );

        // Create Spaceship and init just off the screen, centered vertically
        spaceship = gameContext.createSpaceship(0, 0);  // TODO: START OFF INVISIBLE?
        // Set this class to receive Spaceship events
        spaceship.setListener(this);

        // TODO: ANY WAY WE CAN PUT THE SPACESHIP INTO THE CONTEXT CONSTRUCTOR?
        gameContext.setPlayerSprite(spaceship);

        map = new Map(gameContext);

        // Set state for new, un-started game
        currState = GameState.FINISHED;
    }

    private void resetGame() {
//        gameInputQueue.clear();
        startGame();
    }

    // TODO: MAKE THIS GO THROUGH THE INPUT QUEUE? (THE ANSWER IS YES I THINK)
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

    public int getPlayerHealth() {
        return spaceship.getHealth();
    }

    // updates all game logic
    // adds any new sprites and generates a new set of sprites if needed
    public GameUpdateMessage update() {
        // Process any input events on queue
        while (!gameInputQueue.isEmpty()) {
            processInput(gameInputQueue.poll());
        }

        // Do nothing if paused
        if (isPaused) {
            return new GameUpdateMessage();
        }

        // TODO: BETTER ORGANIZATION OF LOGIC WHILE IN DIFFERENT STATES
        GameTime game_time = gameTimer.recordUpdate();
        currDifficulty = calcDifficulty(game_time.getRunTimeMs());
        scrollSpeed = calcScrollSpeed(currDifficulty);
        scrollDistance += game_time.getMsSincePrevUpdate() / 1000.0 * scrollSpeed;
        scorePerSecond = calcScorePerSecond(currDifficulty);

//        Log.d("GameEngine", String.format("%d, %d, %f, %f, %f, %f", game_time.getRunTimeMs(), game_time.getMsSincePrevUpdate(), currDifficulty, scrollSpeed, scrollDistance, scorePerSecond));
        // If we haven't started yet, check if it's time to start
        if (currState == GameState.STARTING && checkShouldBeginRun()) {
            enterInProgressState();
        }

        // Increment score
        if (currState == GameState.IN_PROGRESS) {
            score += game_time.getMsSincePrevUpdate() / 1000.0 * scorePerSecond;
        }

        // Debug prints, every 100 frames
        if (numUpdates != 0 && numUpdates % 100 == 0) {
            Log.d("GameEngine", String.format(
                    "Spaceship at %f, %f", spaceship.getX(), spaceship.getY()
            ));
            Log.d("GameEngine", String.format(
                    "Game runtime = %f sec", game_time.getRunTimeMs() / 1000.0
            ));
        }

        // Create queues for this update
        FastQueue<Sprite> created_sprites = new FastQueue<>();
        FastQueue<EventID> created_events = new FastQueue<>();
        FastQueue<SoundID> created_sounds = new FastQueue<>();
        FastQueue<DrawParams> draw_params = new FastQueue<>();

        // TODO: PROVIDE IGAMEENGINE INTERFACE REFERENCE?
        UpdateContext update_context = new UpdateContext(
                game_time,
                currDifficulty,
                scrollSpeed,
                created_sprites,
                created_events,
                created_sounds
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
        // TODO: SIMPLE WAY OF GETTING ALL HITBOXES DRAWN?
        for (Sprite obstacle : obstacles) {
            obstacle.getDrawParams(draw_params);
            // Draw hitbox
            DrawRect hitbox_rect = new DrawRect(Color.RED, Paint.Style.STROKE, 2);
            hitbox_rect.setBounds(obstacle.getHitbox());
            draw_params.push(hitbox_rect);
        }

        for (Sprite coin : coins) {
            coin.getDrawParams(draw_params);
        }
        for (Sprite alien : aliens) {
            alien.getDrawParams(draw_params);
        }
        for (Sprite alien_projectile : alienProjectiles) {
            alien_projectile.getDrawParams(draw_params);
        }
        spaceship.getDrawParams(draw_params);

        // Add all created sprites
        // TODO: REFACTOR THIS OUT
        for (Sprite sprite : created_sprites) {
            Log.d("GameEngine", String.format("Adding sprite of type %s", sprite.getSpriteType().toString()));
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

        numUpdates++;
        return new GameUpdateMessage(
                draw_params,
                created_events,
                created_sounds
        );
    }

    private void processInput(GameInput.InputID inputID) {
        Log.d("GameEngine", String.format("Processing input event %s", inputID.toString()));
        switch (inputID) {
            case START_SHOOTING: {
                spaceship.setShooting(true);
                break;
            }
            case STOP_SHOOTING: {
                spaceship.setShooting(false);
                break;
            }
            case START_MOVING_UP: {
                spaceship.setDirection(Spaceship.Direction.UP);
                break;
            }
            case START_MOVING_DOWN: {
                spaceship.setDirection(Spaceship.Direction.DOWN);
                break;
            }
            case STOP_MOVING: {
                spaceship.setDirection(Spaceship.Direction.NONE);
                break;
            }
            case PAUSE_GAME: {
                isPaused = true;
                break;
            }
            case RESUME_GAME: {
                isPaused = false;
                break;
            }
            case RESTART_GAME: {
                resetGame();
                break;
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("Unsupported GameInputID %s", inputID.toString())
                );
            }
        }
    }

    // TODO: CLEAN UP THE `UPDATE()` LOGIC
    private void runStartingState() {

    }

    private void runInProgressState() {

    }

    private void runKilledState() {

    }

    /*
    Return whether it's time to start the current run (and move from
    `STARTING` to `IN_PROGRESS`.
     */
    private boolean checkShouldBeginRun() {
        // Starting position reached
        return currState == GameState.STARTING &&
                spaceship.getX() >= gameContext.getGameWidthPx() / 4;
    }

    private void enterInProgressState() {
        spaceship.setControllable(true);
        spaceship.setSpeedX(0);
        spaceship.setX(gameContext.getGameWidthPx() / 4);
        setState(GameState.IN_PROGRESS);
        // TODO: START OBSTACLE GENERATION?
    }

    private void enterKilledState() {
        // TODO: DON'T WE NEED TO CHECK FOR PLAYER_INVISIBLE FIRST, THEN SLOW DOWN, THEN MARK FINISHED?
        // TODO: I THINK WE NEED A 'SLOWING_DOWN` STATE
        // As soon as the Player is killed, the scrollSpeed
        // slows down to zero.
        // Go to `FINISHED` once scrollSpeed hits near-zero.
        //                if (scrollSpeed > -0.0001f) {
        //                    setState(GameState.FINISHED);
        //                }
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
    private double calcDifficulty(long gameRuntimeMs) {
        double gametime_sec = gameRuntimeMs / 1000.0;
//        Log.d("GameEngine", String.format("difficulty calculation off runtime %d, %f", gameRuntimeMs, gametime_sec));
        // TODO: MAKE SURE IT NEVER EXCEEDS 1.0
        double difficulty = (gametime_sec / 45.0) / (1.0 + gametime_sec / 45.0) + 0.1;
        if (difficulty > 1.0) {
            difficulty = 1.0;
            Log.w("GameEngine", String.format(
                    "WARN: Difficulty exceeded 1.0 (at time %d ms)", gameRuntimeMs)
            );
        }
        return difficulty;
    }

    /*
    Calculates "scrollspeed" based on current scroll speed, game state,
    and difficulty. Return as pixels per second.
     */
    private double calcScrollSpeed(double difficulty) {
        return (0.43 * difficulty + 0.12) * gameContext.getGameWidthPx();
        // Spaceship destroyed: slow down scrolling to a halt.
//        if (currState == GameState.PLAYER_KILLED) {
//            return scrollSpeed / 1.03f;
//        } else { // Normal scrolling progression
//            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
////            scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
//            return currDifficulty / 10;
//        }
    }

    private double calcScorePerSecond(double difficulty) {
        return difficulty * 100;
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

    /* Start implementation of IGameController interface. */
    @Override
    public void inputStartShooting() {
        gameInputQueue.add(GameInput.InputID.START_SHOOTING);
    }

    @Override
    public void inputStopShooting() {
        gameInputQueue.add(GameInput.InputID.STOP_SHOOTING);
    }

    @Override
    public void inputStartMoveUp() {
        gameInputQueue.add(GameInput.InputID.START_MOVING_UP);
    }

    @Override
    public void inputStartMoveDown() {
        gameInputQueue.add(GameInput.InputID.START_MOVING_DOWN);
    }

    @Override
    public void inputStopMoving() {
        gameInputQueue.add(GameInput.InputID.STOP_MOVING);
    }

    @Override
    public void inputPause() {
        gameInputQueue.add(GameInput.InputID.PAUSE_GAME);
    }

    @Override
    public void inputResume() {
        gameInputQueue.add(GameInput.InputID.RESUME_GAME);
    }

    @Override
    public void inputRestart() {
        gameInputQueue.add(GameInput.InputID.RESTART_GAME);
    }
}
