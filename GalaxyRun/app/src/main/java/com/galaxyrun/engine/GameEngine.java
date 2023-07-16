package com.galaxyrun.engine;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.MotionEvent;

import com.galaxyrun.engine.controller.ControlState;
import com.galaxyrun.engine.external.ExternalInput;
import com.galaxyrun.engine.external.GameUpdateMessage;
import com.galaxyrun.engine.external.MotionInput;
import com.galaxyrun.engine.external.PauseGameInput;
import com.galaxyrun.engine.external.SensorInput;
import com.galaxyrun.engine.external.StartGameInput;
import com.galaxyrun.engine.controller.TiltController;
import com.galaxyrun.engine.ui.GameUI;
import com.galaxyrun.engine.ui.UIInputId;
import com.galaxyrun.helper.BitmapCache;
import com.galaxyrun.helper.BitmapData;
import com.galaxyrun.helper.BitmapID;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.helper.FontCache;
import com.galaxyrun.helper.FpsCalculator;
import com.galaxyrun.engine.map.Map;
import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.sprite.Spaceship;
import com.galaxyrun.sprite.Sprite;
import com.galaxyrun.sprite.SpriteState;
import com.galaxyrun.stats.GameTimer;
import com.galaxyrun.util.FastQueue;
import com.galaxyrun.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Core game logic.
 */
public class GameEngine implements IExternalGameController {

    private final GameContext gameContext;
    private final BitmapCache bitmapCache;
    private final FontCache fontCache;
    private final AnimFactory animFactory;

    private HitDetector hitDetector;
    private DrawLayers drawLayers;
    private Map map;
    private Background background;
    private GameUI ui;

    private GameState currState = null;
    private double score;
    private boolean isPaused;
    private boolean isMuted;

    // Tracks game duration (non-paused)  TODO: handle pause() and resume()
    // Note: game difficulty is purely time-based.
    private GameTimer gameTimer;
    private FpsCalculator fpsCalculator;
    // The player's spaceship
    private Spaceship spaceship;
    // All other sprites
    private List<Sprite> sprites;

    // Queue for game input events coming from outside
    private ConcurrentLinkedQueue<ExternalInput> externalInputQueue =
            new ConcurrentLinkedQueue<>();
    // Used to process gyroscope input in order to control the spaceship.
    private TiltController tiltController = new TiltController();

    // Number of points that a coin is worth
    public static final int COIN_VALUE = 100;
    public static final int STARTING_PLAYER_HEALTH = 100;

    public GameEngine(
            Context appContext,
            int screenWidthPx,
            int screenHeightPx,
            boolean inDebugMode
    ) {
        // Calculate game dimensions based on screen dimensions.
        Pair<Integer, Integer> gameDimensions =
                GameUI.calcGameDimensions(screenWidthPx, screenHeightPx);
        int gameWidthPx = gameDimensions.first;
        int gameHeightPx = gameDimensions.second;

        bitmapCache = new BitmapCache(appContext, gameWidthPx, gameHeightPx);
        fontCache = new FontCache(appContext, Typeface.MONOSPACE);
        animFactory = new AnimFactory(bitmapCache);

        // Create GameContext
        gameContext = new GameContext(
                appContext,
                inDebugMode,
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

        initGameObjects();

        // Start in WAITING state
        enterState(GameState.WAITING);
    }

    private void initGameObjects() {
        Log.d("GameEngine", "Initializing game objects");
        gameTimer = new GameTimer();
        fpsCalculator = new FpsCalculator(100);
        score = 0;

        // Move spaceship just off the left of the screen, centered vertically
        BitmapData shipData = gameContext.bitmapCache.getData(BitmapID.SPACESHIP);
        spaceship = new Spaceship(
                gameContext,
                -shipData.getWidth(),
                gameContext.gameHeightPx / 2.0 - shipData.getHeight() / 2.0
        );

        sprites = new LinkedList<>();
        sprites.add(spaceship);

        map = new Map(gameContext);
        background = new Background(gameContext);
        ui = new GameUI(gameContext);
        hitDetector = HitDetector.MakeDefaultHitDetector();
        drawLayers = new DrawLayers(gameContext, 7);
        Log.d("GameEngine", "Finished initializing game objects");
    }

    /*
    Destroy current game state and start a new game.
     */
    private void restart() {
        initGameObjects();
        enterState(GameState.STARTING);
    }

    /*
    Update all game logic.
     */
    public GameUpdateMessage update() {
        // Create queues for this update
        FastQueue<Sprite> createdSprites = new FastQueue<>();
        FastQueue<EventID> createdEvents = new FastQueue<>();
        FastQueue<SoundID> createdSounds = new FastQueue<>();
        FastQueue<DrawInstruction> drawInstructions = new FastQueue<>();

        processExternalInput();
        processUIInput();
        for (SoundID sound : ui.pollAllSounds()) {
            createdSounds.push(sound);
        }

        // Note: we take GameTime *after* processing input because the input
        // may restart the game
        GameTime gameTime = gameTimer.recordUpdate();
        fpsCalculator.recordFrame();
        hitDetector.clear();
        drawLayers.clear();

        GameState shouldState = GameStateMachine.calcState(gameContext, spaceship, currState);
        if (shouldState != currState) {
            enterState(shouldState);
        }

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
//            Log.d("GameEngine", "scorepersec = " + calcScorePerSecond(updateContext.difficulty));
            score += gameTime.msSincePrevUpdate / 1000.0 * calcScorePerSecond(updateContext.difficulty);
        }
        // Give points for any collected coins
        // TODO: more sophisticated event handling
        for (EventID event : createdEvents) {
            if (event == EventID.COIN_COLLECTED) {
                score += COIN_VALUE;
            }
        }

        background.update(updateContext);
        ui.update(updateContext);

        // Collect DrawInstructions.
        // Draw Background first, then sprites, then UI
        background.getDrawInstructions(drawInstructions);
        drawLayers.getDrawInstructions(drawInstructions);
        ui.getDrawInstructions(drawInstructions);

        return new GameUpdateMessage(
                drawInstructions,
                createdEvents,
                createdSounds,
                fpsCalculator.getNumFrames(),
                fpsCalculator.calcFps(),
                isMuted
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
    }

    private void enterStartingState() {
        currState = GameState.STARTING;
        gameTimer.start();
        // Make non-controllable
        // TODO: this can be done at the GameEngine level with a flag and suppressing input
        spaceship.setControllable(false);
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

    private void processExternalInput() {
        while (!externalInputQueue.isEmpty()) {
            ExternalInput input = externalInputQueue.poll();
            if (input != null) {
                switch (input.inputId) {
                    case START_GAME: {
                        enterStartingState();
                        break;
                    }
                    case PAUSE_GAME: {
                        setPaused(true);
                        break;
                    }
                    case MOTION: {
                        MotionEvent e = ((MotionInput) input).motion;
                        ui.inputMotionEvent(e);
                        break;
                    }
                    case SENSOR: {
                        SensorEvent e = ((SensorInput) input).event;
                        if (e.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                            tiltController.inputGyroscopeEvent(e);
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException(
                                String.format("Unsupported ExternalInputId %s", input.inputId)
                        );
                    }
                }
            }
        }
    }

    private void processUIInput() {
        boolean isShooting = false;
        for (UIInputId input : ui.pollAllInput()) {
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
                case MUTE: {
                    setMuted(true);
                    break;
                }
                case UNMUTE: {
                    setMuted(false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        spaceship.setControls(new ControlState(
                tiltController.getDirection(), tiltController.getMagnitude(), isShooting));
    }

    /* IExternalGameController interface. */
    @Override
    public void inputExternalStartGame() {
        externalInputQueue.add(new StartGameInput());
    }

    @Override
    public void inputExternalPauseGame() {
        externalInputQueue.add(new PauseGameInput());
    }

    @Override
    public void inputExternalMotionEvent(MotionEvent e) {
        externalInputQueue.add(new MotionInput(e));
    }

    @Override
    public void inputExternalSensorEvent(SensorEvent e) {
        externalInputQueue.add(new SensorInput(e));
    }
}
