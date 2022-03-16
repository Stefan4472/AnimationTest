package com.plainsimple.spaceships.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameEngine;
import com.plainsimple.spaceships.engine.GameRunner;
import com.plainsimple.spaceships.engine.GameUpdateMessage;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.view.ArrowButtonView;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.IGameViewListener;

import androidx.fragment.app.FragmentActivity;
import plainsimple.spaceships.R;

/**
 * Activity containing the game.
 *
 * The Game logic lives in `GameEngine`, which is executed in a parallel
 * thread by `GameRunner`. GameActivity receives `DrawParams` for the
 * current game and forwards them to `GameView`, which draws them.
 *
 * See https://developer.android.com/guide/components/activities/activity-lifecycle
 * for information about the Activity Lifecycle
 */
public class GameActivity extends FragmentActivity implements
        GameRunner.Callback, // Receive game state updates
        IGameViewListener, // Receive touch events from GameView
        IGameActivity, // Implement methods for calculating screen size
        PauseDialogFragment.PauseDialogListener, // Receive events from the Pause dialog
        GameOverDialogFragment.GameOverDialogListener, // Receive events from the GameOver dialog
        ArrowButtonView.OnDirectionChangedListener // Receive input events from ArrowButtonView
{
    private GameEngine gameEngine;
    private GameRunner mGameRunner;

    private long startTime;
    private long numUpdates;

    // View elements
    private GameView gameView;
//    private ImageButton pauseButton;
//    private ImageButton muteButton;
//    private ArrowButtonView arrowButtons;

    // Whether to keep running the game in a background thread
    //private boolean isRunning; // TODO: improve and work with ACTIVITY LIFECYCLE! -> stop the background thread when activity is not being shown
//    private boolean isGamePaused;
//    private boolean isGameMuted;

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

        // Get handles to the view elements
        gameView = findViewById(R.id.spaceships);
//        pauseButton = findViewById(R.id.pausebutton);
//        muteButton = findViewById(R.id.mutebutton);
//        arrowButtons = findViewById(R.id.arrow_buttons);

        // Provide GameView with a reference to our IGameActivity interface.
        gameView.setGameActivityInterface(this);
        // Register ourselves for needed listeners
        gameView.setGameViewListener(this);
//        arrowButtons.setOnDirectionChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
        gameView.startThread();
//        if (isGamePaused) {
//            // Display pause dialog
//            DialogFragment d = PauseDialogFragment.newInstance(1.0f, 1.0f);
//            d.show(getFragmentManager(), "Pause");
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
//        setGamePaused(true);
        gameView.stopThread();
    }

    /*
    IGameViewListener--handle dimensions determined.
     */
    @Override
    public void onSizeSet(int widthPx, int heightPx) {
        initialize(widthPx, heightPx);
    }

    /*
    Initialize the game. This is called once the GameView has been sized.
    We have to wait for this because we need to know how big our screen is.
     */
    private void initialize(int playableWidthPx, int playableHeightPx) {
        Log.d("GameActivity", String.format(
                "initialize() called w/width %d, height %d", playableWidthPx, playableHeightPx
        ));

        // Create GameEngine
        // TODO: can we move the GameEngine fully into GameRunner?
        gameEngine = new GameEngine(
                getApplicationContext(),
                playableWidthPx,
                playableHeightPx
        );
//        gameContext.setSpaceship(gameEngine.getSpaceship());

        // Create GameRunner background thread
        mGameRunner = new GameRunner(new Handler(), this, gameEngine);
        mGameRunner.start();
        mGameRunner.prepareHandler();
        startTime = System.currentTimeMillis();
        gameEngine.inputExternalStartGame();
        // Call the first game update
        mGameRunner.queueUpdate();
    }

    /*
    GameRunner.Callback. Called when the next game state is ready.
     */
    @Override
    public void onGameStateUpdated(GameUpdateMessage updateMessage) {
        // Log debugging info every 150 frames
        if (numUpdates != 0 && numUpdates % 150 == 0) {
            long curr_time = System.currentTimeMillis();
            double fps = numUpdates / ((curr_time - startTime) / 1000.0);
            Log.d("GameActivity", String.format("fps: %f (%d updates)", fps, numUpdates));
            Log.d("GameActivity", String.format(
                    "Got %d drawParams", updateMessage.getDrawParams().getSize()
            ));
        }

//        for (EventID event : updateMessage.getEvents()) {
//            Log.d("GameActivity", event.toString());
//            switch (event) {
//                case GAME_STARTED: {
//                    onGameStarted();
//                    break;
//                }
//                case GAME_FINISHED: {
//                    onGameFinished();
//                    break;
//                }
//            }
//        }

        // Update views
        gameView.queueDrawFrame(updateMessage.getDrawParams());

        numUpdates++;

        // Call the next update
//        if (isRunning) {
            // Sleep--for testing
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {

            }
            mGameRunner.queueUpdate();
//        }
    }

    /*
    IGameViewListener--handle touch event.
     */
    @Override
    public void handleScreenTouch(MotionEvent motionEvent) {
        gameEngine.inputExternalMotionEvent(motionEvent);
    }

//    private void setGamePaused(boolean isPaused) {
//        // Do nothing if this does not change the state
//        if (this.isGamePaused == isPaused) {
//            return;
//        }
//
//        this.isGamePaused = isPaused;
//        if (this.isGamePaused) {
//            Log.d("GameActivity", "Pausing game");
//            gameEngine.inputExternalPauseGame();
//            pauseButton.setBackgroundResource(R.drawable.play);
////            soundPool.autoPause();
//            // Display pause dialog
//            DialogFragment d = PauseDialogFragment.newInstance(1.0f, 1.0f);
//            d.show(getFragmentManager(), "Pause");
//        } else {
//            Log.d("GameActivity", "Resuming game");
//            gameEngine.inputResumeGame();
//            pauseButton.setBackgroundResource(R.drawable.pause);
////            soundPool.autoResume();
//        }
//    }

//    private void setGameMuted(boolean isMuted) {
//        // Do nothing if this does not change the state
//        if (this.isGameMuted == isMuted) {
//            return;
//        }
//
//        this.isGameMuted = isMuted;
//        if(this.isGameMuted) {
//            muteButton.setBackgroundResource(R.drawable.sound_off);
//        } else {
//            muteButton.setBackgroundResource(R.drawable.sound_on);
//        }
////        AudioManager a_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
////        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, isMuted);
//    }

    /*
    Plays the sound specified by the provided `SoundID`. If the game is isMuted,
     the sound will be suppressed.
     */
//    public void playSound(SoundID soundID) {
//        if (!isGameMuted) {
//            // TODO: LOGIC FOR PLAYING SOUNDS IS CURRENTLY BEING COMMENTED OUT
////            soundPool.play(soundIDs.get(soundID), gameVolume, gameVolume, 1, 0, 1.0f);
//        }
//    }

//    /*
//    Callback fired when the user clicks the "pause" button.
//     */
//    public void onPausePressed(View view) {
//        playSound(SoundID.BUTTON_CLICKED);
//        setGamePaused(!isGamePaused);
//    }

//    /*
//    Callback fired when the user clicks the "mute" button.
//     */
//    public void onMutePressed(View view) {
//        playSound(SoundID.BUTTON_CLICKED);
//        setGameMuted(!isGameMuted);
//    }

    /*
    Callback fired when the user wants to resume the current run (which is paused).
    Resume the game and close the dialog.
     */
    @Override
    public void onResumePressed(DialogFragment dialog) {
//        playSound(SoundID.BUTTON_CLICKED);
//        dialog.dismiss();
//        setGamePaused(false);
    }

    /*
    Callback fired when the user wants to quit the current run.
     */
    @Override
    public void onQuitPressed(DialogFragment dialog) {
//        playSound(SoundID.BUTTON_CLICKED);
////        setGamePaused(true);
//        Log.d("GameActivity", "Quitting game");
//        finish();
    }

    /*
    Callback fired when the user wants to restart the current run.
     */
    @Override
    public void onRestartPressed(DialogFragment dialog) {
//        playSound(SoundID.BUTTON_CLICKED);
//        dialog.dismiss();
//
//        gameEngine.inputRestartGame();
//        setGamePaused(false);
    }

    /*
    Callback fired when the user changes the input direction via ArrowButtonView.
    Send the new direction to the GameView.
     */
    @Override
    public void onDirectionChanged(Spaceship.Direction newDirection) {
//        switch (newDirection) {
//            case DOWN: {
//                gameEngine.inputStartMoveDown();
//                break;
//            }
//            case UP: {
//                gameEngine.inputStartMoveUp();
//                break;
//            }
//            case NONE: {
//                gameEngine.inputStopMoving();
//                break;
//            }
//        }
    }

    /*
    Called when the current run has officially started (i.e. the
    spaceship has reached the correct horizontal position for obstacles
    to start spawning.
     */
//    public void onGameStarted() {
//        // Fade in direction arrows
//        final AnimatorSet fade_in = (AnimatorSet) AnimatorInflater.loadAnimator(
//                this,
//                R.animator.arrowbuttons_fadein
//        );
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                fade_in.setTarget(arrowButtons);
//                fade_in.start();
//            }
//        });
//    }

    /*
    Called when the current run is completely over.
    Check for high-score (TODO) and show the game-over dialog.
     */
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
////        DialogFragment d = GameOverDialogFragment.newInstance(
////                new GameStats(),
////                "GameOver",
////                is_highscore,
////                stars_earned
////        );
////        d.show(getFragmentManager(), "GameOver");
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

//    // TODO: REMOVE
    @Override
    public void onGameVolumeChanged(DialogFragment dialog, float gameVolume) {
////        GameActivity.gameVolume = gameVolume;
//        Log.d("GameActivity.java", "New Game Volume set to " + gameVolume);
//        playSound(SoundID.BUTTON_CLICKED);
    }
//
//    // TODO: REMOVE
    @Override
    public void onMusicVolumeChanged(DialogFragment dialog, float musicVolume) {
////        this.musicVolume = musicVolume;
//        Log.d("GameActivity.java", "New Music Volume set to " + musicVolume);
    }
}
