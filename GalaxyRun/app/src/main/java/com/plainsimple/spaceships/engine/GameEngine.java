package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.util.Log;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.Map;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.FastQueue;
import com.plainsimple.spaceships.util.GameEngineUtil;
import com.plainsimple.spaceships.util.ProtectedQueue;
import com.plainsimple.spaceships.engine.ui.HealthBar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 * TODO: NEEDS PRETTY SERIOUS STRUCTURAL IMPROVEMENTS
 */

public class GameEngine implements IGameController {

    private GameContext gameContext;
    private BitmapCache bitmapCache;
    private AnimFactory animFactory;
    private HitDetector hitDetector;
    private DrawLayers drawLayers;
    private Map map;

    // UI element for the Health Bar, drawn at the bottom of the screen
    private HealthBar healthBar;

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
    private double scrollSpeed;
    // Distance (num pixels) scrolled so far this game
    private double scrollDistance;
    // The player's spaceship
    private Spaceship spaceship;
    // All other sprites
    private List<Sprite> sprites = new LinkedList<>();

    // Queue for game input events
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
    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;


    /* Start GameEngine logic */
    public GameEngine(
            Context appContext,
            int gameWidthPx,
            int gameHeightPx
    ) {
        // Create BitmapCache
        bitmapCache = new BitmapCache(appContext, gameWidthPx, gameHeightPx);
        animFactory = new AnimFactory(bitmapCache);
        gameInputQueue = new ConcurrentLinkedQueue<>();

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                bitmapCache,
                animFactory,
                gameWidthPx,
                gameHeightPx
        );

        // Create Spaceship and init just off the screen, centered vertically
        spaceship = gameContext.createSpaceship(0, 0);  // TODO: START OFF INVISIBLE?
        // TODO: ANY WAY WE CAN PUT THE SPACESHIP INTO THE CONTEXT CONSTRUCTOR?
        gameContext.setPlayerSprite(spaceship);

        // TODO: need to adjust playable width/height to accommodate healthbar
        healthBar = new HealthBar(appContext, gameWidthPx, gameHeightPx, spaceship.getHealth(), GameEngine.STARTING_PLAYER_HEALTH);

        map = new Map(gameContext);

        // TODO: NEEDS A LOT OF CLEANUP
        hitDetector = new HitDetector(new HitDetector.CollisionLayer[] {
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.ALIEN.ordinal(),
                        new int[]{}
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.ALIEN_BULLET.ordinal(),
                        new int[]{}
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.ASTEROID.ordinal(),
                        new int[]{}
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.BULLET.ordinal(),
                        new int[] {
                                Sprite.SpriteType.OBSTACLE.ordinal(),
                                Sprite.SpriteType.ALIEN.ordinal(),
                                Sprite.SpriteType.ASTEROID.ordinal()
                        }
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.COIN.ordinal(),
                        new int[]{}
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.OBSTACLE.ordinal(),
                        new int[]{}
                ),
                new HitDetector.CollisionLayer(
                        Sprite.SpriteType.SPACESHIP.ordinal(),
                        new int[] {
                                Sprite.SpriteType.OBSTACLE.ordinal(),
                                Sprite.SpriteType.COIN.ordinal(),
                                Sprite.SpriteType.ALIEN.ordinal(),
                                Sprite.SpriteType.ALIEN_BULLET.ordinal(),
                                Sprite.SpriteType.ASTEROID.ordinal()
                        }
                )
        });

        drawLayers = new DrawLayers(7);

        // Set state for new, un-started game
        currState = GameState.FINISHED;
    }

    private void resetGame(ProtectedQueue<EventID> createdEvents) {
//        gameInputQueue.clear();
        startGame(createdEvents);
    }

    // TODO: MAKE THIS GO THROUGH THE INPUT QUEUE? (THE ANSWER IS YES I THINK)
    private void startGame(ProtectedQueue<EventID> createdEvents) {
        currDifficulty = 0;
        score = 0;
        scorePerSecond = 0.0;
        scrollDistance = 0.0;

        spaceship.reset();
        sprites.clear();
        sprites.add(spaceship);
        map.restart();

        // Move spaceship just off the left of the screen, centered vertically
        BitmapData ship_data = gameContext.getBitmapCache().getData(BitmapID.SPACESHIP);
        spaceship.setX(-ship_data.getWidth());
        spaceship.setY(gameContext.getGameHeightPx() / 2 - ship_data.getHeight() / 2);
        // Make non-controllable
        spaceship.setControllable(false);
        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(gameContext.getGameWidthPx() * 0.12);

        healthBar.setCurrentHealth(spaceship.getHealth());

        gameTimer = new GameTimer();
        gameTimer.start();

        setState(GameState.STARTING, createdEvents);
    }

    public int getPlayerHealth() {
        return spaceship.getHealth();
    }

    // updates all game logic
    // adds any new sprites and generates a new set of sprites if needed
    public GameUpdateMessage update() {
        // Create queues for this update
        FastQueue<Sprite> created_sprites = new FastQueue<>();
        FastQueue<EventID> created_events = new FastQueue<>();
        FastQueue<SoundID> created_sounds = new FastQueue<>();
        FastQueue<DrawParams> draw_params = new FastQueue<>();

        // Process any input events on queue
        while (!gameInputQueue.isEmpty()) {
            processInput(gameInputQueue.poll(), created_events);
        }

        // Do nothing if paused
        if (isPaused) {
            return new GameUpdateMessage(score, spaceship.getHealth(), currDifficulty);
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
            enterInProgressState(created_events);
        }

        // Increment score
        if (currState == GameState.IN_PROGRESS) {
            score += game_time.getMsSincePrevUpdate() / 1000.0 * scorePerSecond;
        }

        // Debug prints, every 100 frames
//        if (numUpdates != 0 && numUpdates % 100 == 0) {
//            Log.d("GameEngine", String.format(
//                    "Spaceship at %f, %f", spaceship.getX(), spaceship.getY()
//            ));
//            Log.d("GameEngine", String.format(
//                    "Game runtime = %f sec", game_time.getRunTimeMs() / 1000.0
//            ));
//        }

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

        hitDetector.clear();
        drawLayers.clear();

        Iterator<Sprite> it_sprites = sprites.iterator();
        while(it_sprites.hasNext()) {
            Sprite sprite = it_sprites.next();
            if(sprite.shouldTerminate()) {
                it_sprites.remove();
                continue;
            }

            GameEngineUtil.updateSprite(sprite, update_context);
            hitDetector.addSprite(sprite);
            drawLayers.addSprite(sprite);
        }

        // Collect DrawParams
        drawLayers.getDrawParams(draw_params, true);

        // Handle collisions, passing the health of each as the damage
        // applied to the other.
        List<HitDetector.CollisionTuple> collisions = hitDetector.determineCollisions();
        for (HitDetector.CollisionTuple collision : collisions) {
            int sprite_health = collision.sprite1.getHealth();
            int other_health = collision.sprite2.getHealth();
            collision.sprite1.handleCollision(collision.sprite2, other_health, update_context);
            collision.sprite2.handleCollision(collision.sprite1, sprite_health, update_context);
        }

        // Add all created sprites
        for (Sprite sprite : created_sprites) {
            Log.d("GameEngine", String.format("Adding sprite of type %s", sprite.getSpriteType().toString()));
            sprites.add(sprite);
        }

        // Update and draw HealthBar
        healthBar.setMovingToHealth(spaceship.getHealth());
        healthBar.getDrawParams(draw_params);

        numUpdates++;
        return new GameUpdateMessage(
                draw_params,
                created_events,
                created_sounds,
                score,
                spaceship.getHealth(),
                scrollSpeed
        );
    }

    private void processInput(GameInput.InputID inputID, ProtectedQueue<EventID> createdEvents) {
        Log.d("GameEngine", String.format("Processing input event %s", inputID.toString()));
        switch (inputID) {
            case START_GAME: {
                startGame(createdEvents);
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
                resetGame(createdEvents);
                break;
            }
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

    private void enterStartingState(ProtectedQueue<EventID> createdEvents) {

    }

    private void enterInProgressState(ProtectedQueue<EventID> createdEvents) {
        spaceship.setControllable(true);
        spaceship.setSpeedX(0);
        spaceship.setX(gameContext.getGameWidthPx() / 4);
        setState(GameState.IN_PROGRESS, createdEvents);
        // TODO: START OBSTACLE GENERATION?
    }

    private void enterKilledState(ProtectedQueue<EventID> createdEvents) {
        // TODO: DON'T WE NEED TO CHECK FOR PLAYER_INVISIBLE FIRST, THEN SLOW DOWN, THEN MARK FINISHED?
        // TODO: I THINK WE NEED A 'SLOWING_DOWN` STATE
        // As soon as the Player is killed, the scrollSpeed
        // slows down to zero.
        // Go to `FINISHED` once scrollSpeed hits near-zero.
        //                if (scrollSpeed > -0.0001f) {
        //                    setState(GameState.FINISHED);
        //                }
        setState(GameState.PLAYER_KILLED, createdEvents);
    }

    private void enterFinishedState(ProtectedQueue<EventID> createdEvents) {
        setState(GameState.FINISHED, createdEvents);
    }

    private void setState(GameState newState, ProtectedQueue<EventID> createdEvents) {
        Log.d("GameEngine", String.format("Entering state %s", newState.toString()));
        currState = newState;
        if (currState == GameState.IN_PROGRESS) {
            createdEvents.push(EventID.GAME_STARTED);
        } else if (currState == GameState.FINISHED) {
            createdEvents.push(EventID.GAME_FINISHED);
        }
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

    @Override
    public void inputStartGame() {
        gameInputQueue.add(GameInput.InputID.START_GAME);
    }

    @Override
    public void inputPauseGame() {
        gameInputQueue.add(GameInput.InputID.PAUSE_GAME);
    }

    @Override
    public void inputResumeGame() {
        gameInputQueue.add(GameInput.InputID.RESUME_GAME);
    }

    @Override
    public void inputRestartGame() {
        gameInputQueue.add(GameInput.InputID.RESTART_GAME);
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
}
