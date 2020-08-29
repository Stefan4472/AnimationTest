package com.plainsimple.spaceships.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameEngine;
import com.plainsimple.spaceships.engine.GameRunner;
import com.plainsimple.spaceships.engine.GameUpdateMessage;
import com.plainsimple.spaceships.helper.AnimCache;
import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.view.ArrowButtonView;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.HealthBarView;
import com.plainsimple.spaceships.view.IGameViewListener;

import java.util.List;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends FragmentActivity implements
        GameRunner.Callback,
        IGameViewListener,
        IGameActivity,
        PauseDialogFragment.PauseDialogListener,
        GameOverDialogFragment.GameOverDialogListener,
        ArrowButtonView.OnDirectionChangedListener {

    // TODO: LOOKUP JAVA CODING GUIDELINES
    private boolean initialized;
    private GameEngine gameEngine;
    private GameRunner mGameRunner;
    private BitmapCache bitmapCache;

    private long startTime;
    private long numUpdates;

    // View elements
    private GameView gameView;
    private HealthBarView healthbarView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ArrowButtonView arrowButtons;

    // TODO: MANAGE THE ACTIVITY LIFECYCLE PROPERLY!!!!!
    private boolean isRunning = true;

    private boolean isPaused;
    private boolean isMuted;

//    private static SoundPool soundPool;
//    private static Hashtable<SoundID, Integer> soundIDs;

//    private SharedPreferences preferences;

    /*
    Callback fired when the activity is created. Loads everything required and
    starts the game.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);

        // Go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
//        getSupportActionBar().hide();

        // Set content view/layout
        setContentView(R.layout.game_layout);

        // get handle to SharedPreferences
//        preferences = getPreferences(Context.MODE_PRIVATE);

        setGameMuted(false);
        setGamePaused(false);

        // Get handles to the view elements
        gameView = (GameView) findViewById(R.id.spaceships);
        healthbarView = (HealthBarView) findViewById(R.id.healthbar);
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        arrowButtons = (ArrowButtonView) findViewById(R.id.arrow_buttons);

        // Provide GameView with a reference to our IGameActivity interface.
        gameView.setGameActivityInterface(this);
        // Register ourselves for needed listeners
        gameView.setGameViewListener(this);
        arrowButtons.setOnDirectionChangedListener(this);

        // set volume control to proper stream
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /*
    GameRunner.Callback. Called when the next game update has been run.
     */
    @Override
    public void onGameStateUpdated(GameUpdateMessage message) {
//        Log.d("GameActivity", "Got latest game update");
        if (numUpdates != 0 && numUpdates % 100 == 0) {
            long curr_time = System.currentTimeMillis();
            Log.d("GameActivity", "fps: " + numUpdates / ((curr_time - startTime) / 1000));
            Log.d("GameActivity", String.format(
                    "Got %d drawParams", message.getDrawParams().getSize()
            ));
        }
        for (EventID event : message.getEvents()) {
            Log.d("GameActivity", event.toString());
        }
        numUpdates++;

        // Update views
        gameView.queueDrawFrame(message.getDrawParams());
//        healthbarView.setMovingToHealth((int) (numUpdates % 100));

        // Call the next update
        if (isRunning) {
            // Sleep--for testing
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }
            mGameRunner.queueUpdate();
        }
    }

    /*
    IGameViewListener--handle touch event.
     */
    @Override
    public void handleScreenTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            // Start of touch
            case MotionEvent.ACTION_DOWN:
                gameEngine.inputStartShooting();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            // End of touch
            case MotionEvent.ACTION_UP:
                gameEngine.inputEndShooting();
                break;
        }
    }

    /*
    IGameViewListener--handle dimensions determined.
     */
    @Override
    public void onSizeSet(int widthPx, int heightPx) {
        initialize(widthPx, heightPx);
    }

    private void initialize(int playableWidthPx, int playableHeightPx) {
        Log.d("GameActivity", String.format(
                "initialize() called w/width %d, height %d", playableWidthPx, playableHeightPx
        ));

        bitmapCache = new BitmapCache(
                getApplicationContext(),
                playableWidthPx,
                playableHeightPx
        );
        gameView.setBitmapCache(bitmapCache);

        // Create GameEngine
        gameEngine = new GameEngine(
                getApplicationContext(),
                bitmapCache,
                playableWidthPx,
                playableHeightPx
        );
//        gameContext.setSpaceship(gameEngine.getSpaceship());

        // Setup UI
        resetUI();
        // Create GameRunner background thread
        mGameRunner = new GameRunner(new Handler(), this, gameEngine);
        mGameRunner.start();
        mGameRunner.prepareHandler();
        startTime = System.currentTimeMillis();
        gameEngine.startGame();
        // Call the first game update
        mGameRunner.queueUpdate();
        initialized = true;
//        Log.d("Activity Class", "Creating SoundPool");
//        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//        soundIDs = new Hashtable<>();
//        Log.d("Activity Class", "Loading Sounds");
//        soundIDs.put(SoundID.LASER, soundPool.load(this, SoundID.LASER.getId(), 1));
//        soundIDs.put(SoundID.ROCKET, soundPool.load(this, SoundID.ROCKET.getId(), 1));
//        soundIDs.put(SoundID.EXPLOSION, soundPool.load(this, SoundID.EXPLOSION.getId(), 1));
//        soundIDs.put(SoundID.BUTTON_CLICKED, soundPool.load(this, SoundID.BUTTON_CLICKED.getId(), 1));
//        Log.d("Activity Class", soundIDs.size() + " sounds loaded");
    }

    private void resetUI() {
        assert(gameEngine != null);
        // Initialize healthbarView with the correct hp values
        healthbarView.setFullHealth(GameEngine.STARTING_PLAYER_HEALTH);
        healthbarView.setCurrentHealth(gameEngine.getPlayerHealth());
    }

    private void setGamePaused(boolean isPaused) {
        assert(gameEngine != null);

        // Do nothing if this does not change the state
        if (this.isPaused == isPaused) {
            return;
        }

        this.isPaused = isPaused;
        if (this.isPaused) {
            Log.d("GameActivity", "Pausing game");
            gameEngine.inputPause();
            pauseButton.setBackgroundResource(R.drawable.play);
//            soundPool.autoPause();
            // Display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(1.0f, 1.0f);
            d.show(getFragmentManager(), "Pause");
        } else {
            Log.d("GameActivity", "Resuming game");
            gameEngine.inputResume();
            pauseButton.setBackgroundResource(R.drawable.pause);
//            soundPool.autoResume();
        }
    }

    private void setGameMuted(boolean isMuted) {
        assert(gameEngine != null);

        // Do nothing if this does not change the state
        if (this.isMuted == isMuted) {
            return;
        }

        this.isMuted = isMuted;
        if(this.isMuted) {
            muteButton.setBackgroundResource(R.drawable.sound_off);
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_on);
        }
//        AudioManager a_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, isMuted);
    }

    /*
    Plays the sound specified by the provided `SoundID`. If the game is isMuted,
     the sound will be suppressed.
     */
    public void playSound(SoundID soundID) {
        if (!isMuted) {
            // TODO: LOGIC FOR PLAYING SOUNDS IS CURRENTLY BEING COMMENTED OUT
//            soundPool.play(soundIDs.get(soundID), gameVolume, gameVolume, 1, 0, 1.0f);
        }
    }

    /*
    Callback fired when the user clicks the "pause" button.
     */
    public void onPausePressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        setGamePaused(!isPaused);
    }

    /*
    Callback fired when the user clicks the "mute" button. Either mutes
    the game, or unmutes the game, depending on the current state of `isMuted`.
    Change the mute button graphic to reflect the new state.
     */
    public void onMutePressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        setGameMuted(!isMuted);
    }

    /*
    Callback fired when the user wants to resume the current run (which is paused).
    Resume the game and close the dialog.
     */
    @Override
    public void onResumePressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        dialog.dismiss();
        setGamePaused(false);
    }

    /*
    Callback fired when the user wants to quit the current run.
     */
    @Override
    public void onQuitPressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
//        setGamePaused(true);
        Log.d("GameActivity", "Quitting game");
        finish();
    }

    /*
    Callback fired when the user wants to restart the current run.
     */
    @Override
    public void onRestartPressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        dialog.dismiss();

        gameEngine.inputRestart();
        resetUI();
        setGamePaused(false);
    }

    /*
    Callbacks fired when the GameView's surface dimensions are set or changed.
    Returns given surfaceHeight minus height of HealthBarView. This way,
    we make sure that the GameView does not draw itself under the HealthBarView.
    TODO: COULD WE MAKE IT SO THAT THIS IS NOT NECESSARY?
     */
    @Override
    public int calcPlayableHeight(int surfaceHeight) {
        return surfaceHeight - healthbarView.getHeight();
    }
    @Override
    public int calcPlayableWidth(int surfaceWidth) {
        return surfaceWidth;
    }

    /*
    Callback fired when the user changes the input direction via ArrowButtonView.
    Send the new direction to the GameView.
     */
    @Override
    public void onDirectionChanged(Spaceship.Direction newDirection) {
        switch (newDirection) {
            case DOWN: {
                gameEngine.inputStartMoveDown();
                break;
            }
            case UP: {
                gameEngine.inputStartMoveUp();
                break;
            }
            case NONE: {
                gameEngine.inputStopMoving();
                break;
            }
        }
    }

    /*
    Callback fired when the current run has officially started (i.e. the
    spaceship has reached the correct horizontal position for obstacles
    to start spawning.
     */
//    @Override
//    public void onGameStarted() {
//        // Fade in direction arrows
//        final AnimatorSet fade_in =
//                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrowbuttons_fadein);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fade_in.setTarget(arrowButtons);
//                fade_in.start();
//            }
//        });
//        // start the game timer
////        gameView.getGameTimer().start();
//    }
//
//    /*
//    Callback fired when the current run is completely over.
//    Check for high-score (TODO) and show the game-over dialog.
//     */
//    @Override
//    public void onGameFinished() {
//        // Hide arrowButtons
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                arrowButtons.setAlpha(0);
//            }
//        });
//
//        boolean is_highscore = false;
//        int stars_earned = 0;
//        // Show the GameOver dialog
//        DialogFragment d = GameOverDialogFragment.newInstance(
//                new GameStats(),
//                "GameOver",
//                is_highscore,
//                stars_earned
//        );
//        d.show(getFragmentManager(), "GameOver");
//    }

    /*
    Callback fired when the player's health changes.
    Trigger an update of the healthbarView.
     */
//    @Override
//    public void onHealthChanged(final int newHealth) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                healthbarView.setMovingToHealth(newHealth);
//            }
//        });
//    }

    /*
    Callback fired when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
        isPaused = true;
        gameEngine.inputPause();
        gameView.stopThread();
        isRunning = false;

//        soundPool.release();
//        soundPool = null;
    }

    /*
    Callback fired when the activity is resumed.
    TODO: HANDLE ACTIVITY LIFECYCLE CORRECTLY
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
        gameView.startThread();
//        initMedia();
        if (isPaused) {
            // display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(1.0f, 1.0f);
            d.show(getFragmentManager(), "Pause");
        }
    }

    // TODO: REMOVE
    @Override
    public void onGameVolumeChanged(DialogFragment dialog, float gameVolume) {
//        GameActivity.gameVolume = gameVolume;
        Log.d("GameActivity.java", "New Game Volume set to " + gameVolume);
        playSound(SoundID.BUTTON_CLICKED);
    }

    // TODO: REMOVE
    @Override
    public void onMusicVolumeChanged(DialogFragment dialog, float musicVolume) {
//        this.musicVolume = musicVolume;
        Log.d("GameActivity.java", "New Music Volume set to " + musicVolume);
    }
}
