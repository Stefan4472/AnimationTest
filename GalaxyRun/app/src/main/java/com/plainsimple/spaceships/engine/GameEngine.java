package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.external.ExternalInput;
import com.plainsimple.spaceships.engine.external.ExternalInputId;
import com.plainsimple.spaceships.engine.external.GameUpdateMessage;
import com.plainsimple.spaceships.engine.external.MotionExternalInput;
import com.plainsimple.spaceships.engine.external.SimpleExternalInput;
import com.plainsimple.spaceships.engine.ui.Background;
import com.plainsimple.spaceships.engine.ui.GameUI;
import com.plainsimple.spaceships.engine.ui.UIInputId;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.helper.FpsCalculator;
import com.plainsimple.spaceships.helper.Map;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.FastQueue;
import com.plainsimple.spaceships.util.GameEngineUtil;
import com.plainsimple.spaceships.util.Pair;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 * TODO: NEEDS PRETTY SERIOUS STRUCTURAL IMPROVEMENTS
 */

public class GameEngine implements IExternalGameController {

    private GameContext gameContext;
    private BitmapCache bitmapCache;
    private AnimFactory animFactory;
    private HitDetector hitDetector;
    private DrawLayers drawLayers;
    private Map map;
    private Background background;
    private GameUI ui;

    private GameState currState;
    private boolean isPaused;
    private boolean isMuted;
    private int score;

    // TODO: NOTE: IN PROGRESS OF REWRITING OLD LOGIC
    // Tracks game (non-paused)  TODO: handle pause() and resume()
    // Note: game difficulty is purely time-based.
    private GameTimer gameTimer;
    private FpsCalculator fpsCalculator;
    // The player's spaceship
    private Spaceship spaceship;
    // All other sprites
    private List<Sprite> sprites = new LinkedList<>();

    // Queue for game input events coming from outside
    private ConcurrentLinkedQueue<ExternalInput> externalInputQueue;

    // Represents the possible states that the game can be in
    private enum GameState {
        STARTING,
        IN_PROGRESS,
        PLAYER_KILLED,
        FINISHED;
    }

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;

    /* Start GameEngine logic */
    public GameEngine(
            Context appContext,
            int screenWidthPx,
            int screenHeightPx
    ) {
        // Calculate game dimensions based on screen dimensions.
        Pair<Integer, Integer> gameDimensions =
                GameUI.calcGameDimensions(screenWidthPx, screenHeightPx);
        int gameWidthPx = gameDimensions.first;
        int gameHeightPx = gameDimensions.second;

        bitmapCache = new BitmapCache(appContext, gameWidthPx, gameHeightPx);
        animFactory = new AnimFactory(bitmapCache);
        externalInputQueue = new ConcurrentLinkedQueue<>();
        gameTimer = new GameTimer();
        fpsCalculator = new FpsCalculator(100);

        // Create GameContext TODO: add "debug" flag to GameContext?
        gameContext = new GameContext(
                appContext,
                bitmapCache,
                animFactory,
                gameWidthPx,
                gameHeightPx,
                screenWidthPx,
                screenHeightPx,
                GameEngine.STARTING_PLAYER_HEALTH
        );

        // Create Spaceship. Will be set just off the screen, centered vertically
        spaceship = new Spaceship(gameContext.getNextSpriteId(), 0, 0, gameContext);

        // GUI elements
        // TODO: need to adjust playable width/height to accommodate healthbar
        background = new Background(gameContext);
        ui = new GameUI(gameContext);

        map = new Map(gameContext);

        hitDetector = HitDetector.MakeDefaultHitDetector();
        drawLayers = new DrawLayers(7);

        // Set state for new, un-started game
        currState = GameState.FINISHED;

        // TODO: allow external trigger?
        startGame(new FastQueue<>());
    }

    private void resetGame(ProtectedQueue<EventID> createdEvents) {
//        gameInputQueue.clear();
        startGame(createdEvents);
    }

    // TODO: MAKE THIS GO THROUGH THE INPUT QUEUE? (THE ANSWER IS YES I THINK)
    // TODO: how to reset/restart the game?
    private void startGame(ProtectedQueue<EventID> createdEvents) {
        score = 0;
        gameTimer.reset();  // TODO
        gameTimer.start();

        spaceship.reset();
        sprites.clear();
        sprites.add(spaceship);
        map.restart();

        // Move spaceship just off the left of the screen, centered vertically
        BitmapData ship_data = gameContext.bitmapCache.getData(BitmapID.SPACESHIP);
        spaceship.setX(-ship_data.getWidth());
        spaceship.setY(gameContext.gameHeightPx / 2 - ship_data.getHeight() / 2);
        // Make non-controllable
        spaceship.setControllable(false);
        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(gameContext.gameWidthPx * 0.12);

        setState(GameState.STARTING, createdEvents);
    }

    /*
    Update all game logic.
     */
    public GameUpdateMessage update() {
        hitDetector.clear();
        drawLayers.clear();

        // Create queues for this update
        FastQueue<Sprite> createdSprites = new FastQueue<>();
        FastQueue<EventID> createdEvents = new FastQueue<>();
        FastQueue<SoundID> createdSounds = new FastQueue<>();
        FastQueue<DrawParams> drawParams = new FastQueue<>();

        // Process any input events on queue
        while (!externalInputQueue.isEmpty()) {
            processExternalInput(externalInputQueue.poll(), createdEvents);
        }

        // Do nothing if paused
        // TODO: probably don't need to handle this case if GameTimer is paused
        if (isPaused) {
            Log.d("GameEngine", "Game is paused!");
            return new GameUpdateMessage(drawParams, createdEvents, createdSounds, 0.0);
        }

        // TODO: BETTER ORGANIZATION OF LOGIC WHILE IN DIFFERENT STATES
        GameTime gameTime = gameTimer.recordUpdate();
        map.update(gameTime, createdSprites);
        double difficulty = map.getDifficulty();
        double scrollSpeed = map.getScrollSpeed();

        // If we haven't started yet, check if it's time to start
        if (currState == GameState.STARTING && checkShouldBeginRun()) {
            enterInProgressState(createdEvents);
        }

        // Increment score
        if (currState == GameState.IN_PROGRESS) {
            double scorePerSecond = calcScorePerSecond(difficulty);
            score += gameTime.msSincePrevUpdate / 1000.0 * scorePerSecond;
        }

        // TODO: PROVIDE IGAMEENGINE INTERFACE REFERENCE?
        UpdateContext updateContext = new UpdateContext(
                gameTime,
                difficulty,
                scrollSpeed,
                score,
                spaceship.getHealth(),
                isPaused,
                isMuted,
                spaceship.getDirection(),
                spaceship,
                createdSprites,
                createdEvents,
                createdSounds
        );

//        if (currState == GameState.IN_PROGRESS) {
//            map.update(updateContext, scrollDistance);
//        }

        // Update and draw background
        background.update(updateContext);
        background.getDrawParams(drawParams);

        // Update sprites, removing any that should be "terminated"
        Iterator<Sprite> it_sprites = sprites.iterator();
        while (it_sprites.hasNext()) {
            Sprite sprite = it_sprites.next();
            if (sprite.shouldTerminate()) {
                it_sprites.remove();
                continue;
            }
            GameEngineUtil.updateSprite(sprite, updateContext);
            hitDetector.addSprite(sprite);
            drawLayers.addSprite(sprite);
        }

        // Collect DrawParams
        drawLayers.getDrawParams(drawParams, true);

        // Handle collisions, passing the health of each as the damage
        // applied to the other.
        List<HitDetector.CollisionTuple> collisions = hitDetector.determineCollisions();
        for (HitDetector.CollisionTuple collision : collisions) {
            int sprite_health = collision.sprite1.getHealth();
            int other_health = collision.sprite2.getHealth();
            collision.sprite1.handleCollision(collision.sprite2, other_health, updateContext);
            collision.sprite2.handleCollision(collision.sprite1, sprite_health, updateContext);
        }

        // Add all created sprites
        for (Sprite sprite : createdSprites) {
            Log.d("GameEngine", String.format("Adding sprite of type %s", sprite.getSpriteType().toString()));
            sprites.add(sprite);
        }

        ui.update(updateContext);
        ui.getDrawParams(drawParams);

        for (UIInputId input : ui.pollAllInput()) {
            processUIInput(input);
        }

        fpsCalculator.recordFrame();
        return new GameUpdateMessage(
                drawParams,
                createdEvents,
                createdSounds,
                fpsCalculator.getAverage()
        );
    }

    // TODO: handle external lifecycle events (PAUSE, RESUME, STOP)
    private void processExternalInput(ExternalInput input, ProtectedQueue<EventID> createdEvents) {
        Log.d("GameEngine", String.format("Processing external input event %s", input.toString()));
        if (input instanceof SimpleExternalInput) {
//            switch (((SimpleExternalInput) input).inputId) {
//                case START_GAME: {
//                    startGame(createdEvents);
//                    break;
//                }
//                case PAUSE_GAME: {
//                    isPaused = true;
//                    // TODO: pauseGame()
//                    break;
//                }
//                default: {
//                    throw new IllegalArgumentException(
//                            String.format("Unsupported GameInputID %s", ((SimpleExternalInput) input).inputId)
//                    );
//                }
//            }
        } else if (input instanceof MotionExternalInput) {
            MotionEvent e = ((MotionExternalInput) input).motion;
            ui.handleMotionEvent(e);
        } else {
            throw new IllegalArgumentException("Unsupported input type");
        }
    }

    private void processUIInput(UIInputId input) {
        switch (input) {
            case PAUSE_GAME: {
                Log.d("GameEngine", "Pausing game");
                isPaused = true;
                fpsCalculator.reset();
                break;
            }
            case RESUME_GAME: {
                Log.d("GameEngine", "Resuming game");
                isPaused = false;
                break;
            }
//            case RESTART_GAME: {
//                resetGame(createdEvents);
//                break;
//            }
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
            case MUTE_GAME: {
                isMuted = true;
                break;
            }
            case UNMUTE_GAME: {
                isMuted = false;
                break;
            }
            default: {
                throw new IllegalArgumentException();
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
                spaceship.getX() >= gameContext.gameWidthPx / 4;
    }

    private void enterStartingState(ProtectedQueue<EventID> createdEvents) {

    }

    private void enterInProgressState(ProtectedQueue<EventID> createdEvents) {
        spaceship.setControllable(true);
        spaceship.setSpeedX(0);
        spaceship.setX(gameContext.gameWidthPx / 4);
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

    private double calcScorePerSecond(double difficulty) {
        return difficulty * 100;
    }

    /* IExternalGameController interface. */
    @Override
    public void inputExternalStartGame() {
        externalInputQueue.add(new SimpleExternalInput(ExternalInputId.START_GAME));
    }

    @Override
    public void inputExternalPauseGame() {
        externalInputQueue.add(new SimpleExternalInput(ExternalInputId.PAUSE_GAME));
    }

    @Override
    public void inputExternalMotionEvent(MotionEvent e) {
        externalInputQueue.add(new MotionExternalInput(e));
    }

    public void inputStartGame() {
        externalInputQueue.add(new SimpleExternalInput(ExternalInputId.START_GAME));
    }

    public void inputPauseGame() {
        externalInputQueue.add(new SimpleExternalInput(ExternalInputId.PAUSE_GAME));
    }

    public void inputMotionEvent(MotionEvent e) {
        externalInputQueue.add(new MotionExternalInput(e));
    }

//    public void inputResumeGame() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.RESUME_GAME));
//    }
//
//    public void inputRestartGame() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.RESTART_GAME));
//    }
//
//    public void inputStartShooting() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.START_SHOOTING));
//    }
//
//    public void inputStopShooting() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.STOP_SHOOTING));
//    }
//
//    public void inputStartMoveUp() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.START_MOVING_UP));
//    }
//
//    public void inputStartMoveDown() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.START_MOVING_DOWN));
//    }
//
//    public void inputStopMoving() {
//        gameInputQueue.add(new SimpleGameInput(GameInputId.STOP_MOVING));
//    }
}
