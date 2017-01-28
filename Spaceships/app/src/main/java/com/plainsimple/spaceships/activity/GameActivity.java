package com.plainsimple.spaceships.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.plainsimple.spaceships.helper.ArmorType;
import com.plainsimple.spaceships.helper.CannonType;
import com.plainsimple.spaceships.helper.EquipmentManager;
import com.plainsimple.spaceships.helper.GameStats;
import com.plainsimple.spaceships.helper.RocketType;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.EnumUtil;
import com.plainsimple.spaceships.view.GameView;

import java.util.Hashtable;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends FragmentActivity implements SensorEventListener, PauseDialogFragment.PauseDialogListener,
    GameOverDialogFragment.GameOverDialogListener {

    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;
    private ImageButton upArrow;
    private ImageButton downArrow;
    // whether the user selected to quit the game
    private boolean quit = false;
    private static SoundPool soundPool;
    private static Hashtable<SoundID, Integer> soundIDs;
    private static boolean paused = false;
    private static boolean muted;
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
    private static final String FIRE_MODE_KEY = "SELECTED_FIRE_MODE";
    private static final String RESTORE_STATE_KEY = "RESTORE_GAME_STATE";

    // sensor manager for gyroscope
    private SensorManager sensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameActivity", "onCreate called");
        Log.d("GameActivity", "Screen Density is " + getResources().getDisplayMetrics().densityDpi);
        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set content view/layout to gameview layout
        setContentView(R.layout.activity_game);
        // set up view elements
        gameView = (GameView) findViewById(R.id.spaceships); // todo: what should go in onResume()?

        // get handle to SharedPreferences
        preferences = getPreferences(Context.MODE_PRIVATE);
        muted = preferences.getBoolean(MUTED_KEY, false);
        paused = false;

        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(muted ? R.drawable.sound_off : R.drawable.sound_on);
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton); // todo: establish whether rockets have been unlocked and which fire mode to start with
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
        // initialize listeners
        upArrow = (ImageButton) findViewById(R.id.up_arrow);
        upArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   downArrow.setVisibility(View.INVISIBLE); // todo: why did this stop working?
                   gameView.updateInput(Spaceship.DIRECTION_UP);
               } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   gameView.updateInput(Spaceship.DIRECTION_NONE);
                   downArrow.setVisibility(View.VISIBLE);
               }
                return false; // todo: does return matter?
            }
        });
        downArrow = (ImageButton) findViewById(R.id.down_arrow);
        downArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    upArrow.setVisibility(View.INVISIBLE);
                    gameView.updateInput(Spaceship.DIRECTION_DOWN);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameView.updateInput(Spaceship.DIRECTION_NONE);
                    upArrow.setVisibility(View.VISIBLE);
                }
                return false; // todo: does return matter?
            }
        });
        // set up GameEventsListener
        final Animation arrow_fade_in = AnimationUtils.loadAnimation(this, R.anim.arrowbutton_fadein);
        gameView.setGameEventsListener(new GameView.GameEventsListener() { // todo: methods in class body
            @Override // fade in direction arrows once spaceship reaches initial position
            public void onGameStarted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upArrow.startAnimation(arrow_fade_in);
                        downArrow.startAnimation(arrow_fade_in);
                    }
                });
            }
            @Override // pop-up end game dialog when game is over
            public void onGameFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upArrow.setVisibility(View.INVISIBLE);
                        downArrow.setVisibility(View.INVISIBLE);
                    }
                });
                // display GameOverDialogFragment todo: fade-in animation
                Log.d("GameActivity.java", "Displaying GameOverDialog");
                DialogFragment d = new GameOverDialogFragment();
                d.show(getFragmentManager(), "GameOver");
            }
        });
        // set volume control to proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // retrieve game and music volume from SharedPreferences
        gameVolume = preferences.getFloat(GAME_VOLUME_KEY, 1.0f);
        musicVolume = preferences.getFloat(MUSIC_VOLUME_KEY, 1.0f);

        // get sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // retrieve game state if instructions in preferences say so
        if (preferences.getBoolean(RESTORE_STATE_KEY, false)) {
            Log.d("GameActivity", "Found a saved game to restore");
            gameView.flagRestoreGameState();
        }

        // retrieve equipped cannon and rocket
        EquipmentManager equipmentManager = new EquipmentManager(this);
        equippedCannon = equipmentManager.getEquippedCannon();
        equippedRocket = equipmentManager.getEquippedRocket();
        equippedArmor = equipmentManager.getEquippedArmor();
    }

    private void initMedia() {
        Log.d("Activity Class", "Creating SoundPool");
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new Hashtable<>();
        Log.d("Activity Class", "Loading Sounds");
        soundIDs.put(SoundID.LASER, soundPool.load(this, EnumUtil.getID(SoundID.LASER), 1));
        soundIDs.put(SoundID.ROCKET, soundPool.load(this, EnumUtil.getID(SoundID.ROCKET), 1));
        soundIDs.put(SoundID.EXPLOSION, soundPool.load(this, EnumUtil.getID(SoundID.EXPLOSION), 1));
        soundIDs.put(SoundID.BUTTON_CLICKED, soundPool.load(this, EnumUtil.getID(SoundID.BUTTON_CLICKED), 1));
        soundIDs.put(SoundID.TITLE_THEME, soundPool.load(this, EnumUtil.getID(SoundID.TITLE_THEME), 1));
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
            //gameView.getGameTimer().stop();
            pauseButton.setBackgroundResource(R.drawable.play);
            soundPool.autoPause();
            // display pause dialog
            DialogFragment d = PauseDialogFragment.newInstance(gameVolume, musicVolume);
            d.show(getFragmentManager(), "Pause");
        } else {
            //gameView.getGameTimer().start();
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
        AudioManager a_manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
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

    @Override
    public void onResumePressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        Log.d("GameActivity", "Resuming game");
        dialog.dismiss();
        onPausePressed(gameView);
    }

    @Override
    public void onQuitPressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        quit = true;
        updateStats();
        Log.d("GameActivity", "Quitting game");
        finish();
    }

    @Override
    public void onRestartPressed(DialogFragment dialog) {
        playSound(SoundID.BUTTON_CLICKED);
        dialog.dismiss();
        updateStats();
        gameView.restartGame();
        paused = false;
    }

    // updates lifetime stats from GameView's current run
    private void updateStats() {
        // ensure gameView's stats are up to date
        gameView.forceUpdateStats(); // todo: clean up, fix issues
        // update lifetime stats with this game's collected stats
        SharedPreferences l_stats = getSharedPreferences(GameStats.PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor l_stats_e = l_stats.edit();
        //l_stats_e.clear();
        for (String key : GameView.currentStats.getKeySet()) {
            l_stats_e.putLong(key, Double.doubleToLongBits(Double.longBitsToDouble(l_stats.getLong(key, 0)) + GameView.currentStats.get(key)));
            Log.d("GameActivity.java", "Updated field " + key + " to " + Double.longBitsToDouble(l_stats.getLong(key, 0)));
        }
        if (GameView.currentStats.get(GameStats.GAME_SCORE) > Double.longBitsToDouble(l_stats.getLong(GameStats.HIGH_SCORE, 0))) {
            l_stats_e.putLong(GameStats.GAME_SCORE, Double.doubleToLongBits(GameView.currentStats.get(GameStats.GAME_SCORE)));
        }
        l_stats_e.commit();
        // add coins collected in game to current available coins (stored in EquipmentManager)
        EquipmentManager coin_manager = new EquipmentManager(this);
        coin_manager.addCoins((int) GameView.currentStats.get(GameStats.COINS_COLLECTED));
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
        /*if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) { // todo: only register if inputMode = Gyro
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_NORMAL); // todo: test sample rates. Manually restrict rate? // todo: works with Level 9 API +
            Log.d("Activity Class", "Gyroscope Registered");
        } else {
            Log.d("GameView Class", "No Gyroscope");
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
        // pause if game is still playing
        if (!paused && !quit) {
            Log.d("GameActivity", "Pausing the game");
            paused = true;
            Log.d("GameActivity", "Finished pausing the game");
        }
        if (!quit) { // save game state if user did not quit on purpose
            /*Log.d("GameActivity.java", "Saving game state");
            long start_time = System.currentTimeMillis();
            gameView.saveGameState();
            Log.d("GameActivity", "Finished Save: Took " + (System.currentTimeMillis() - start_time) + "ms");*/
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(GAME_VOLUME_KEY, gameVolume);
        editor.putFloat(MUSIC_VOLUME_KEY, musicVolume);
        editor.putBoolean(MUTED_KEY, muted);
        // flag game restore if user did not explicitly press quit
        editor.putBoolean(RESTORE_STATE_KEY, !quit);
        editor.commit();
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("GameActivity", "onStop called");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gameView.updateInput(event.values[1]); // update gameView with current screen pitch // todo: this should be registered in GameActivity
        }
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
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
