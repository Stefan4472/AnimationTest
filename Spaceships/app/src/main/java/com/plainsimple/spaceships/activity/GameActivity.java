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
public class GameActivity extends FragmentActivity implements PauseDialogFragment.PauseDialogListener,
    GameOverDialogFragment.GameOverDialogListener, GameView.GameEventsListener,
        ArrowButtonView.OnDirectionChangedListener {

    // view elements
    private GameView gameView;
    private HealthBarView healthBarView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;
    private ArrowButtonView arrowButtons;

    // GameMode object containing all data required to administer the selected GameMode
    private GameMode gameMode;
    // difficulty game is being played at
    private GameView.Difficulty difficulty;

    private static boolean paused = false;
    private static boolean muted;

    private static SoundPool soundPool;
    private static Hashtable<SoundID, Integer> soundIDs;

    private static CannonType equippedCannon;
    private static RocketType equippedRocket;
    private static ArmorType equippedArmor;

    private SharedPreferences preferences;
    private float musicVolume;
    private static float gameVolume;

    // keys for retrieving data relevant to GUI from SharedPreferences
    private static final String GAME_VOLUME_KEY = "gameVolume";
    private static final String MUSIC_VOLUME_KEY = "musicVolume";
    private static final String MUTED_KEY = "MUTED";
    public static final String DIFFICULTY_KEY = "SELECTED_GAME_DIFFICULTY";
    public static final String GAMEMODE_KEY = "SELECTED_GAMEMODE";

    @Override // loads everything required and starts the game. Requires a bundle with valid
    // DIFFICULTY_KEY and GAMEMODE_KEY params. Throws IllegalArgumentException if this is not the
    // case.
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);

        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view/layout to gameview layout
        setContentView(R.layout.game_layout);

        // get handle to SharedPreferences
        preferences = getPreferences(Context.MODE_PRIVATE);
        muted = preferences.getBoolean(MUTED_KEY, false);
        paused = false;

        // set up view elements
        gameView = (GameView) findViewById(R.id.spaceships); // todo: what should go in onResume()?
        healthBarView = (HealthBarView) findViewById(R.id.healthbar);
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(muted ? R.drawable.sound_off : R.drawable.sound_on);
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton); // todo: establish whether rockets have been unlocked and which fire mode to start with
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);

        arrowButtons = (ArrowButtonView) findViewById(R.id.arrow_buttons);
        arrowButtons.setOnDirectionChangedListener(this);

        // todo: fade in arrowButtons
        Bundle args = getIntent().getExtras();

        try { // todo: any way to get this data to gameView when it's first initialized?
            gameMode = GameModeManager.retrieve(args.getString(GAMEMODE_KEY));
            difficulty = GameView.Difficulty.valueOf(args.getString(DIFFICULTY_KEY));
            gameView.setDifficultyLevel(difficulty);
            gameView.setGameMode(gameMode);
            Log.d("GameActivity", "Playing " + gameMode.getName() + " on " + args.getString(DIFFICULTY_KEY));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("GameActivity requires a Bundle with valid " +
                    "GAMEMODE_KEY and DIFFICULTY_KEY params");
        }

        // set up GameEventsListener
        gameView.setGameEventsListener(this);

        // set volume control to proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // retrieve game and music volume from SharedPreferences
        gameVolume = preferences.getFloat(GAME_VOLUME_KEY, 1.0f);
        musicVolume = preferences.getFloat(MUSIC_VOLUME_KEY, 1.0f);

        // retrieve equipped cannon and rocket
        equippedCannon = EquipmentManager.getEquippedCannon();
        equippedRocket = EquipmentManager.getEquippedRocket();
        equippedArmor = EquipmentManager.getEquippedArmor();

        // initialize healthBarView with correct hp values
        healthBarView.setFullHealth(equippedArmor.getHP());
        healthBarView.setCurrentHealth(equippedArmor.getHP());
    }

    private void initMedia() {
        Log.d("Activity Class", "Creating SoundPool");
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new Hashtable<>();
        Log.d("Activity Class", "Loading Sounds");
        soundIDs.put(SoundID.LASER, soundPool.load(this, SoundID.LASER.getId(), 1));
        soundIDs.put(SoundID.ROCKET, soundPool.load(this, SoundID.ROCKET.getId(), 1));
        soundIDs.put(SoundID.EXPLOSION, soundPool.load(this, SoundID.EXPLOSION.getId(), 1));
        soundIDs.put(SoundID.BUTTON_CLICKED, soundPool.load(this, SoundID.BUTTON_CLICKED.getId(), 1));
        Log.d("Activity Class", soundIDs.size() + " sounds loaded");
    }

    // plays a sound using the SoundPool at the correct volume
    public static void playSound(SoundID soundID) {
        if (!muted) {
            soundPool.play(soundIDs.get(soundID), gameVolume, gameVolume, 1, 0, 1.0f);
        }
    }

    // handle user pressing pause button
    public void onPausePressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        paused = !paused;
        if (paused) {
            gameView.getGameTimer().pause();
            pauseButton.setBackgroundResource(R.drawable.play);
            soundPool.autoPause();
            // display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(gameVolume, musicVolume);
            d.show(getFragmentManager(), "Pause");
        } else {
            gameView.getGameTimer().start();
            pauseButton.setBackgroundResource(R.drawable.pause);
            soundPool.autoResume();
        }
    }

    public void onMutePressed(View view) {
        muted = !muted;
        if(muted) {
            muteButton.setBackgroundResource(R.drawable.sound_off);
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_on);
            playSound(SoundID.BUTTON_CLICKED);
        }
        AudioManager a_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, muted);
    }

    public void onToggleBulletPressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        gameView.setFiringMode(Spaceship.FireMode.BULLET);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
    }

    public void onToggleRocketPressed(View view) {
        playSound(SoundID.BUTTON_CLICKED);
        gameView.setFiringMode(Spaceship.FireMode.ROCKET);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button_pressed);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button);
    }

    @Override // handles the user resuming the game from the PauseDialog
    // Overrided from PauseDialogListener
    public void onResumePressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        Log.d("GameActivity", "Resuming game");
        dialog.dismiss();
        onPausePressed(gameView);
    }

    @Override // handles the user quitting from the PauseDialog or
    // GameOverDialog. Overrided from PauseDialogListener and GameOverDialogListener
    public void onQuitPressed(DialogFragment dialog) { // todo: wouldn't save stats if prematurely exited
        playSound(SoundID.BUTTON_CLICKED);
        paused = true;
        Log.d("GameActivity", "Quitting game");
        finish();
    }

    @Override // handles the user restarting the game from the PauseDialog
    // or GameOverDialog. Overrided from PauseDialogListener and GameOverDialogListener
    public void onRestartPressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        dialog.dismiss();
        gameView.restartGame();
        healthBarView.setCurrentHealth(equippedArmor.getHP());
        paused = false;
        pauseButton.setBackgroundResource(R.drawable.pause);
    }

    @Override // handles the GameView's dimensions being set or changed
    // returns given screenHeight minus height of HealthBarView (to avoid
    // HealthBarView being drawn over part of the GameView).
    // Overriden from GameEventsListener.
    public int onGameViewSurfaced(int screenHeight) {
        return screenHeight - healthBarView.getHeight();
    }

    @Override // fired when the ArrowButtonView has a change of input. Sends new direction to GameView
    public void onDirectionChanged(Spaceship.Direction newDirection) {
        gameView.updateDirection(newDirection);
    }

    @Override // handles the game officially starting (i.e. the spaceship has reached
    // the correct horizontal position for obstacles to start. Override from GameEventsListener
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

    @Override // pop-up end game dialog when game is over
    public void onGameFinished() {
        // hide arrowButtons
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrowButtons.setAlpha(0);
            }
        });
        // update all stats and display GameOverDialogFragment
        gameView.forceUpdateStats();
        boolean high_score = updateStats();

        // query GameMode for number of stars earned, based on currentStats
        int stars_earned = gameMode.calculateStars(GameView.currentStats.getScore().intValue());
        DialogFragment d = GameOverDialogFragment.newInstance(GameView.currentStats, "GameOver",
                high_score, stars_earned);
        d.show(getFragmentManager(), "GameOver");
    }

    @Override // health changed triggers update of healthBarView
    public void onHealthChanged(final int newHealth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                healthBarView.setMovingToHealth(newHealth);
            }
        });
    }

    // updates all necessary statistics using data from GameView's current run. This includes
    // StatsManager, Coins, and GameMode-specific stats. Returns whether this run was a highscore
    // for the GameMode
    private boolean updateStats() { // todo: improve
        // ensure gameView's stats are up to date
        gameView.forceUpdateStats();
        // update lifetime stats with this game's collected stats
        StatsManager.update(GameView.currentStats);
        // add coins collected in game to current available coins (stored in EquipmentManager)
        EquipmentManager.addCoins((int) GameView.currentStats.get(GameStats.COINS_COLLECTED));
        // update GameMode specific data and commit to GameModeManager
        boolean high_score = GameView.currentStats.getScore().intValue() > gameMode.getHighscore();
        if (high_score) {
            gameMode.setHighscore(GameView.currentStats.getScore().intValue());
            Log.d("GameActivity", "Highscore!");
        }
        // ensure GameMode is set to correct difficulty
        gameMode.setLastDifficulty(difficulty);
        GameModeManager.put(gameMode.getKey(), gameMode);
        return high_score;
    }

    @Override // we do not restore game state here--only in onCreate
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
        initMedia();
        Log.d("Activity Class", "Media Initialized");
        if (paused) {
            // display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(gameVolume, musicVolume);
            d.show(getFragmentManager(), "Pause");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
        // pause the game to stop rendering
        paused = true;
        // save volume preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(GAME_VOLUME_KEY, gameVolume);
        editor.putFloat(MUSIC_VOLUME_KEY, musicVolume);
        editor.putBoolean(MUTED_KEY, muted);
        editor.commit();
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void onGameVolumeChanged(DialogFragment dialog, float gameVolume) {
        GameActivity.gameVolume = gameVolume;
        Log.d("GameActivity.java", "New Game Volume set to " + gameVolume);
        playSound(SoundID.BUTTON_CLICKED);
    }

    @Override
    public void onMusicVolumeChanged(DialogFragment dialog, float musicVolume) {
        this.musicVolume = musicVolume;
        Log.d("GameActivity.java", "New Music Volume set to " + musicVolume);
    }

    public static boolean getPaused() {
        return paused;
    }

    public static boolean isMuted() {
        return muted;
    }

    public static CannonType getEquippedCannon() {
        return equippedCannon;
    }

    public static RocketType getEquippedRocket() {
        return equippedRocket;
    }

    public static ArmorType getEquippedArmor() {
        return equippedArmor;
    }
}
