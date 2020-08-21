package com.plainsimple.spaceships.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.GameModeManager;
import com.plainsimple.spaceships.store.ArmorType;
import com.plainsimple.spaceships.store.CannonType;
import com.plainsimple.spaceships.store.EquipmentManager;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.stats.StatsManager;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.view.ArrowButtonView;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.HealthBarView;

import java.util.Hashtable;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends FragmentActivity
        implements PauseDialogFragment.PauseDialogListener,
            GameOverDialogFragment.GameOverDialogListener,
            GameView.GameEventsListener,
            ArrowButtonView.OnDirectionChangedListener {

    // view elements
    private GameView gameView;
    private HealthBarView healthBarView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ArrowButtonView arrowButtons;

    // GameMode object containing all data required to administer the selected GameMode
    private GameMode gameMode;
    // difficulty game is being played at
    private GameView.Difficulty difficulty;

    // TODO: MAKE NON-STATIC
    private static boolean paused = false;
    private static boolean muted;

//    private static SoundPool soundPool;
//    private static Hashtable<SoundID, Integer> soundIDs;

//    private SharedPreferences preferences;

    // loads everything required and starts the game. Requires a bundle with valid
    // DIFFICULTY_KEY and GAMEMODE_KEY params. Throws IllegalArgumentException if this is not the
    // case.
    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);

        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // set content view/layout to gameview layout
        setContentView(R.layout.game_layout);

        // get handle to SharedPreferences
//        preferences = getPreferences(Context.MODE_PRIVATE);
        muted = false;
        paused = false;

        // Set up view elements
        gameView = (GameView) findViewById(R.id.spaceships); // todo: what should go in onResume()?
        healthBarView = (HealthBarView) findViewById(R.id.healthbar);
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(muted ? R.drawable.sound_off : R.drawable.sound_on);

        // todo: fade in arrowButtons
        arrowButtons = (ArrowButtonView) findViewById(R.id.arrow_buttons);
        arrowButtons.setOnDirectionChangedListener(this);

        gameMode = GameModeManager.retrieve(GameModeManager.ENDLESS_0);
        difficulty = GameView.Difficulty.EASY;

        gameView.setDifficultyLevel(difficulty);
        gameView.setGameMode(gameMode);
        Log.d("GameActivity", "Playing " + gameMode.getName() + " on " + difficulty.toString());

        // set up GameEventsListener
        gameView.setGameEventsListener(this);

        // set volume control to proper stream
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // initialize healthBarView with correct hp values
        healthBarView.setFullHealth(100);
        healthBarView.setCurrentHealth(100);
    }

    private void initMedia() {
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

    /*
    Plays the sound specified by the provided `SoundID`. If the game is muted,
     the sound will be suppressed.
     */
    public static void playSound(SoundID soundID) {
        if (!muted) {
            // TODO: LOGIC FOR PLAYING SOUNDS IS CURRENTLY BEING COMMENTED OUT
//            soundPool.play(soundIDs.get(soundID), gameVolume, gameVolume, 1, 0, 1.0f);
        }
    }

    /*
    Callback fired when the user clicks the "pause" button.
     */
    public void onPausePressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        paused = !paused;
        if (paused) {
//            gameView.getGameTimer().pause();
            pauseButton.setBackgroundResource(R.drawable.play);
//            soundPool.autoPause();
            // Display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(1.0f, 1.0f);
            d.show(getFragmentManager(), "Pause");
        } else {
//            gameView.getGameTimer().start();
            pauseButton.setBackgroundResource(R.drawable.pause);
//            soundPool.autoResume();
        }
    }

    /*
    Callback fired when the user clicks the "mute" button. Either mutes
    the game, or unmutes the game, depending on the current state of `muted`.
    Change the mute button graphic to reflect the new state.
     */
    public void onMutePressed(View view) {
        muted = !muted;
        if(muted) {
            muteButton.setBackgroundResource(R.drawable.sound_off);
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_on);
            playSound(SoundID.BUTTON_CLICKED);
        }
//        AudioManager a_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, muted);
    }

    /*
    Callback fired when the user wants to resume the current run (which is paused).
     */
    @Override
    public void onResumePressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        Log.d("GameActivity", "Resuming game");
        dialog.dismiss();
        onPausePressed(gameView);
    }

    /*
    Callback fired when the user wants to quit the current run.
     */
    @Override
    public void onQuitPressed(DialogFragment dialog) { // todo: wouldn't save stats if prematurely exited
        playSound(SoundID.BUTTON_CLICKED);
        paused = true;
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
        gameView.restartGame();
        healthBarView.setCurrentHealth(100);
        paused = false;
        pauseButton.setBackgroundResource(R.drawable.pause);
    }

    // handles the GameView's dimensions being set or changed
    // returns given screenHeight minus height of HealthBarView (to avoid
    // HealthBarView being drawn over part of the GameView).
    // Overriden from GameEventsListener.
    // TODO: THIS SHOULDN'T RETURN ANYTHING
    @Override
    public int onGameViewSurfaced(int screenHeight) {
        return screenHeight - healthBarView.getHeight();
    }

    /*
    Callback fired when the user changes the input direction via ArrowButtonView.
    Send the new direction to the GameView.
     */
    @Override
    public void onDirectionChanged(Spaceship.Direction newDirection) {
        gameView.updateDirection(newDirection);
    }

    /*
    Callback fired when the current run has officially started (i.e. the
    spaceship has reached the correct horizontal position for obstacles
    to start spawning.
     */
    @Override
    public void onGameStarted() {
        // fade in direction arrows once spaceship reaches initial position
        final AnimatorSet fade_in = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrowbuttons_fadein);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fade_in.setTarget(arrowButtons);
                fade_in.start();
            }
        });
        // start the game timer
        gameView.getGameTimer().start();
    }

    /*
    Callback fired when the current run is completely over.
    Check for high-score (TODO) and show the game-over dialog.
     */
    @Override
    public void onGameFinished() {
        // Hide arrowButtons
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrowButtons.setAlpha(0);
            }
        });

        boolean is_highscore = false;
        int stars_earned = 0;
        // Show the GameOver dialog
        DialogFragment d = GameOverDialogFragment.newInstance(
                GameView.currentStats,
                "GameOver",
                is_highscore,
                stars_earned
        );
        d.show(getFragmentManager(), "GameOver");
    }

    /*
    Callback fired when the player's health changes.
    Trigger an update of the healthBarView.
     */
    @Override
    public void onHealthChanged(final int newHealth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                healthBarView.setMovingToHealth(newHealth);
            }
        });
    }

    /*
    Callback fired when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
        // pause the game to stop rendering
        paused = true;

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
        initMedia();
        if (paused) {
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

    public static boolean getPaused() {
        return paused;
    }

    public static boolean isMuted() {
        return muted;
    }
}
