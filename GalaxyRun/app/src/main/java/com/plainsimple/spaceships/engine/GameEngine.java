package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.external.ExternalInput;
import com.plainsimple.spaceships.engine.external.ExternalInputId;
import com.plainsimple.spaceships.engine.external.GameUpdateMessage;
import com.plainsimple.spaceships.engine.external.MotionExternalInput;
import com.plainsimple.spaceships.engine.external.SimpleExternalInput;
import com.plainsimple.spaceships.engine.ui.GameUI;
import com.plainsimple.spaceships.engine.ui.UIInputId;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.helper.FontCache;
import com.plainsimple.spaceships.helper.FpsCalculator;
import com.plainsimple.spaceships.engine.map.Map;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.sprite.SpriteState;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.util.FastQueue;
import com.plainsimple.spaceships.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 *
 * TODO: think more about how to handle restarts.
 */
public class GameEngine implements IExternalGameController {

    private GameContext gameContext;
    private BitmapCache bitmapCache;
    private FontCache fontCache;
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
        fontCache = new FontCache(appContext, Typeface.MONOSPACE);
        animFactory = new AnimFactory(bitmapCache);

        // Create GameContext TODO: add "debug" flag to GameContext?
        gameContext = new GameContext(
                appContext,
                bitmapCache,
                fontCache,
                animFactory,
                new Random(System.currentTimeMillis()),
                gameWidthPx,
                gameHeightPx,
                screenWidthPx,
                screenHeightPx,
                GameEngine.STARTING_PLAYER_HEALTH
        );

        // Init everything that requires GameContext
        spaceship = new Spaceship(gameContext, 0, 0);
        background = new Background(gameContext);
        ui = new GameUI(gameContext);
        map = new Map(gameContext);
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
        processUIInput(ui.pollAllInput());

        GameState shouldState = GameStateMachine.calcState(gameContext, spaceship, currState);
        if (shouldState != currState) {
            enterState(shouldState);
        }

        // Create queues for this update
        FastQueue<Sprite> createdSprites = new FastQueue<>();
        FastQueue<EventID> createdEvents = new FastQueue<>();
        FastQueue<SoundID> createdSounds = new FastQueue<>();
        FastQueue<DrawInstruction> drawInstructions = new FastQueue<>();

        map.update(gameTime, createdSprites);

        UpdateContext updateContext = new UpdateContext(
                gameTime,
                currState,
                map.getDifficulty(),
                map.getScrollSpeed(),
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

//        map.spawnNewSprites(updateContext);

        // Update sprites, removing any that should be "terminated"
        Iterator<Sprite> it_sprites = sprites.iterator();
        while (it_sprites.hasNext()) {
            Sprite sprite = it_sprites.next();
            // sprite.update(updateContext);
            if (sprite.getState() == SpriteState.TERMINATED) {
                it_sprites.remove();
                continue;
            }

            sprite.updateSpeeds(updateContext);
            sprite.move(updateContext);
            sprite.updateActions(updateContext);
            sprite.updateAnimations(updateContext);

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
            Log.d("GameEngine", String.format("Adding sprite of type %s", sprite.getClass().getSimpleName()));
            sprites.add(sprite);
        }

        // Give points for being alive
        if (currState == GameState.PLAYING) {
            score += gameTime.msSincePrevUpdate / 1000.0 * calcScorePerSecond(updateContext.difficulty);
        }

        background.update(updateContext);
        ui.update(updateContext);

        // Collect DrawInstructions.
        // Draw Background first, then sprites, then UI
        background.getDrawInstructions(drawInstructions);
        drawLayers.getDrawInstructions(drawInstructions, true);
        ui.getDrawInstructions(drawInstructions);

        // A little hack to easily support muting:
        // simply delete all sounds
        if (isMuted) {
            createdSounds.clear();
        }

        fpsCalculator.recordFrame();
        return new GameUpdateMessage(
                drawInstructions,
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
        map = new Map(gameContext);
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
        map = new Map(gameContext);

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

    private void restart() {
        // TODO: also need to reset the UI
        enterStartingState();
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

    private void processUIInput(Queue<UIInputId> inputs) {
        // Set defaults in the absence of input
        boolean isShooting = false;
        Spaceship.Direction moveInput = Spaceship.Direction.NONE;

        for (UIInputId input : inputs) {
            switch (input) {
                case PAUSE: {
                    setPaused(true);
                    break;
                }
                case RESUME: {
                    setPaused(false);
                    break;
                }
                case RESTART: {
                    restart();
                    break;
                }
                case SHOOT: {
                    isShooting = true;
                    break;
                }
                case MOVE_UP: {
                    moveInput = Spaceship.Direction.UP;
                    break;
                }
                case MOVE_DOWN: {
                    moveInput = Spaceship.Direction.DOWN;
                    break;
                }
                case MUTE: {
                    setMuted(true);
                    break;
                }
                case UN_MUTE: {
                    setMuted(false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        // TODO: check if isControllable()
        spaceship.setShooting(isShooting);
        spaceship.setDirection(moveInput);
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

    // TODO: refactor out
    public void inputStartGame() {
        externalInputQueue.add(new SimpleExternalInput(ExternalInputId.START_GAME));
    }
}
