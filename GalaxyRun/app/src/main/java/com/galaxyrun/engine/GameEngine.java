package com.galaxyrun.engine;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.MotionEvent;

import com.galaxyrun.engine.background.Background;
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
public class GameEngine implements IExternalGameController, IGameStateReceiver {

    private final GameContext gameContext;
    private final BitmapCache bitmapCache;
    private final FontCache fontCache;
    private final AnimFactory animFactory;

    private HitDetector hitDetector;
    private DrawLayers drawLayers;
    private Map map;
    private Background background;
    private GameUI ui;

    // Calculates the state of the game and triggers state transitions.
    private GameStateMachine stateMachine;
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
    private final ConcurrentLinkedQueue<ExternalInput> externalInputQueue =
            new ConcurrentLinkedQueue<>();
    // Used to process gyroscope input in order to control the spaceship.
    private final TiltController tiltController = new TiltController();

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
        // TODO: export to static factory method. Have a struct for game width/height and screen
        //  width/height
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
                screenHeightPx
        );

        initGameObjects();
    }

    private void initGameObjects() {
        Log.d("GameEngine", "Initializing game objects");
        // Init GameStateMachine and set ourselves to receive callbacks.
        stateMachine = new GameStateMachine(gameContext, this);

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

        // TODO: rename `GameGenerator`?
        map = new Map(gameContext);
        // TODO: rename `GameBackground`?
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
        // TODO: this is fishy
        stateMachine.startGame();
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
        // Calculate the current game state. This will call the appropriate callbacks.
        stateMachine.updateState(spaceship);

        // TODO: spawned_sprites = map.update() ? Rename `Map` to `Spawner`? Or `GameGenerator`?
        map.update(gameTime, createdSprites);
        // TODO: add createdSprites to be processed in this update.

        UpdateContext updateContext = new UpdateContext(
                gameTime,
                stateMachine.getCurrState(),
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
        // TODO: break these into separate for-loops
        Iterator<Sprite> it_sprites = sprites.iterator();
        while (it_sprites.hasNext()) {
            Sprite sprite = it_sprites.next();
            // sprite.update(updateContext);
            // TODO: this should be done after updating actions.
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
        if (stateMachine.getCurrState() == GameState.PLAYING) {
//            Log.d("GameEngine", "scorepersec = " + calcScorePerSecond(updateContext.difficulty));
            score += gameTime.msSincePrevUpdate / 1000.0 * calcScorePerSecond(updateContext.difficulty);
        }
        // Give points for any collected coins
        // TODO: more sophisticated event handling
        for (EventID event : createdEvents) {
            if (event == EventID.COIN_COLLECTED) {
                score += GameConstants.COIN_VALUE;
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

    @Override
    public void enterWaitingState() {
    }

    @Override
    public void enterStartingState() {
        gameTimer.start();
        // Make non-controllable
        // TODO: this can be done at the GameEngine level with a flag and suppressing input
        spaceship.setControllable(false);
        // Set speed to slowly fly onto screen
        spaceship.setSpeedX(gameContext.gameWidthPx * 0.12);
    }

    @Override
    public void enterPlayingState() {
        spaceship.setControllable(true);
        spaceship.setX(gameContext.gameWidthPx / 4.0);
        spaceship.setSpeedX(0);
    }

    @Override
    public void enterPlayerDeadState() {
        spaceship.setControllable(false);
    }

    @Override
    public void enterGameOverState() {
        gameTimer.pause();
    }

    private void setPaused(boolean shouldPause) {
        // Note: have to be careful in WAITING and FINISHED states,
        // because the timer should not be running
        if (isPaused != shouldPause && stateMachine.getCurrState() != GameState.GAME_OVER
                && stateMachine.getCurrState() != GameState.WAITING_FOR_START) {
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
                        stateMachine.startGame();
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
        // TODO: use the gameContext time. The fact that we don't have it here indicates
        //  something strange with the overarching logic.
        spaceship.setControls(new ControlState(
                tiltController.calculateState(System.currentTimeMillis()), isShooting));
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
