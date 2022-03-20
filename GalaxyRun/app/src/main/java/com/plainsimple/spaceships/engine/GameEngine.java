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
import com.plainsimple.spaceships.engine.map.Map;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.FastQueue;
import com.plainsimple.spaceships.util.GameEngineUtil;
import com.plainsimple.spaceships.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 *
 * TODO: think more about how to handle restarts.
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

    private GameState currState = null;
    private boolean isPaused;
    private boolean isMuted;
    private int score;

    // Tracks game duration (non-paused)  TODO: handle pause() and resume()
    // Note: game difficulty is purely time-based.
    private GameTimer gameTimer = new GameTimer();
    private FpsCalculator fpsCalculator = new FpsCalculator(100);
    // The player's spaceship
    private Spaceship spaceship;
    // All other sprites
    private List<Sprite> sprites = new LinkedList<>();

    // Queue for game input events coming from outside
    private ConcurrentLinkedQueue<ExternalInput> externalInputQueue =
            new ConcurrentLinkedQueue<>();

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;

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

        // Init everything that requires GameContext
        spaceship = new Spaceship(gameContext.getNextSpriteId(), 0, 0, gameContext);
        background = new Background(gameContext);
        ui = new GameUI(gameContext);
        map = new Map(gameContext, System.currentTimeMillis());
        hitDetector = HitDetector.MakeDefaultHitDetector();
        drawLayers = new DrawLayers(7);

        // Start in WAITING state
        enterState(GameState.WAITING);
    }

    /*
    Update all game logic.
     */
    public GameUpdateMessage update() {
        GameTime gameTime = gameTimer.recordUpdate();
        hitDetector.clear();
        drawLayers.clear();

        // Process any external inputs on queue
        while (!externalInputQueue.isEmpty()) {
            ExternalInput input = externalInputQueue.poll();
            if (input != null) {
                processExternalInput(input);
            }
        }
        // Process UI inputs (which can be created by handling
        // external inputs, e.g. screen touches)
        for (UIInputId input : ui.pollAllInput()) {
            processUIInput(input);
        }

        GameState shouldState = GameStateMachine.calcState(gameContext, spaceship, currState);
        if (shouldState != currState) {
            enterState(shouldState);
        }

        // Create queues for this update
        FastQueue<Sprite> createdSprites = new FastQueue<>();
        FastQueue<EventID> createdEvents = new FastQueue<>();
        FastQueue<SoundID> createdSounds = new FastQueue<>();
        FastQueue<DrawParams> drawParams = new FastQueue<>();

        map.update(gameTime, createdSprites);
        double difficulty = map.getDifficulty();
        double scrollSpeed = map.getScrollSpeed();

        UpdateContext updateContext = new UpdateContext(
                gameTime,
                currState,
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

        // Give points for being alive
        if (currState == GameState.PLAYING) {
            double scorePerSecond = calcScorePerSecond(difficulty);
            score += gameTime.msSincePrevUpdate / 1000.0 * scorePerSecond;
        }

        background.update(updateContext);
        ui.update(updateContext);

        // Collect DrawParams.
        // Draw Background first, then sprites, then UI
        background.getDrawParams(drawParams);
        drawLayers.getDrawParams(drawParams, true);
        ui.getDrawParams(drawParams);

        // A little hack to easily support muting:
        // simply delete all sounds
        if (isMuted) {
            createdSounds.clear();
        }

        fpsCalculator.recordFrame();
        return new GameUpdateMessage(
                drawParams,
                createdEvents,
                createdSounds,
                fpsCalculator.getNumFrames(),
                fpsCalculator.calcFps()
        );
    }

    private double calcScorePerSecond(double difficulty) {
        return difficulty * 100;
    }

    /*
    Enters the new GameState, applying the transition function.
     */
    private void enterState(GameState newState) {
        if (newState != currState) {
            Log.d("GameEngine", "Setting state to " + newState.name());
            switch (newState) {
                case WAITING:
                    enterWaitingState();
                    break;
                case STARTING:
                    enterStartingState();
                    break;
                case PLAYING:
                    enterPlayingState();
                    break;
                case DEAD:
                    enterDeadState();
                    break;
                case FINISHED:
                    enterFinishedState();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported GameState");
            }
        }
    }

    private void enterWaitingState() {
        currState = GameState.WAITING;
        map = new Map(gameContext, System.currentTimeMillis());
        // Move spaceship just off the left of the screen, centered vertically
        BitmapData ship_data = gameContext.bitmapCache.getData(BitmapID.SPACESHIP);
        spaceship.setX(-ship_data.getWidth());
        spaceship.setY(gameContext.gameHeightPx / 2.0 - ship_data.getHeight() / 2.0);
        // Make non-controllable
        // TODO: this can be done at the GameEngine level with a flag and suppressing input
        spaceship.setControllable(false);
    }

    private void enterStartingState() {
        currState = GameState.STARTING;
        score = 0;
        gameTimer = new GameTimer();
        gameTimer.start();

        // Create Spaceship and position off the screen to the left,
        // centered vertically
        spaceship.reset();
        sprites.clear();
        sprites.add(spaceship);
        // TODO: how to avoid re-creating the Map multiple times?
        //   -> may in fact need a reset() function for certain objects
        map = new Map(gameContext, System.currentTimeMillis());

        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(gameContext.gameWidthPx * 0.12);
    }

    private void enterPlayingState() {
        currState = GameState.PLAYING;
        spaceship.setControllable(true);
        spaceship.setX(gameContext.gameWidthPx / 4.0);
        spaceship.setSpeedX(0);
    }

    private void enterDeadState() {
        currState = GameState.DEAD;
        spaceship.setControllable(false);
    }

    private void enterFinishedState() {
        currState = GameState.FINISHED;
        gameTimer.pause();
    }

    private void setPaused(boolean shouldPause) {
        // Note: have to be careful in WAITING and FINISHED states,
        // because the timer should not be running
        if (isPaused != shouldPause && currState != GameState.FINISHED && currState != GameState.WAITING) {
            Log.d("GameEngine", "Setting paused = " + shouldPause);
            isPaused = shouldPause;
            if (shouldPause) {
                gameTimer.pause();
            } else {
                gameTimer.resume();
            }

        }
    }

    private void setMuted(boolean shouldMute) {
        Log.d("GameEngine", "Setting muted = " + shouldMute);
        this.isMuted = shouldMute;
    }

    private void processExternalInput(ExternalInput input) {
//        Log.d("GameEngine", String.format("Processing external input event %s", input.toString()));
        if (input instanceof SimpleExternalInput) {
            switch (((SimpleExternalInput) input).inputId) {
                case START_GAME: {
                    enterStartingState();
                    break;
                }
                default: {
                    throw new IllegalArgumentException(
                            String.format("Unsupported GameInputID %s", ((SimpleExternalInput) input).inputId)
                    );
                }
            }
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
                setPaused(true);
                break;
            }
            case RESUME_GAME: {
                setPaused(false);
                break;
            }
            case RESTART_GAME: {
                // TODO: also need to reset the UI
                enterStartingState();
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
            case MUTE_GAME: {
                setMuted(true);
                break;
            }
            case UNMUTE_GAME: {
                setMuted(false);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
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
