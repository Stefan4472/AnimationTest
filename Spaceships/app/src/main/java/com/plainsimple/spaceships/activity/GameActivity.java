package com.plainsimple.spaceships.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.plainsimple.spaceships.helper.Equipped;
import com.plainsimple.spaceships.helper.GameSave;
import com.plainsimple.spaceships.helper.RawResource;
import com.plainsimple.spaceships.helper.SoundParams;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.EnumUtil;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;
import com.plainsimple.spaceships.view.GameView;

import java.util.Hashtable;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends FragmentActivity implements SensorEventListener {

    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;
    private FontTextView pausedText;
    private FontButton resumeButton;
    private FontButton quitButton;
    private ImageButton upArrow;
    private ImageButton downArrow;
    // whether the user selected to quit the game
    private boolean quit = false;
    private static SoundPool soundPool;
    private static Hashtable<RawResource, Integer> soundIDs;
    private static boolean paused = false;
    private static boolean muted = false;
    private static int score = 0;
    private static float difficulty = 0;
    // points a coin is worth
    public static final int COIN_VALUE = 100;

    // sensor manager for gyroscope
    private SensorManager sensorManager;
    /* Called when activity first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameActivity", "onCreate called");
        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set content view/layout to gameview layout
        setContentView(R.layout.activity_game);
        gameView = (GameView) findViewById(R.id.spaceships); // todo: what should go in onResume()?
        gameView.setKeepScreenOn(true);

        // set up view elements
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(R.drawable.sound_on);
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton); // todo: establish whether rockets have been unlocked, establish firemode
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
        pausedText = (FontTextView) findViewById(R.id.pausedNotification);
        resumeButton = (FontButton) findViewById(R.id.resumeButton);
        quitButton = (FontButton) findViewById(R.id.quitButton);

        upArrow = (ImageButton) findViewById(R.id.up_arrow);
        upArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   downArrow.setVisibility(View.INVISIBLE);
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

        final Animation arrow_fade_in = AnimationUtils.loadAnimation(this, R.anim.arrowbutton_fadein);
        gameView.setGameEventsListener(new GameView.GameEventsListener() {
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

            }
        });
        // set volume control to proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // get sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // retrieve game state if one is found in memory
        if (GameSave.exists(this, GameSave.DEFAULT_SAVE_NAME)) {
            Log.d("GameActivity", "Found a saved game to restore");
            gameView.flagRestoreGameState(GameSave.DEFAULT_SAVE_NAME);
        }
    }

    private void initMedia() {
        Log.d("Activity Class", "Creating SoundPool");
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new Hashtable<>();
        Log.d("Activity Class", "Loading Sounds");
        soundIDs.put(RawResource.LASER, soundPool.load(this, EnumUtil.getID(RawResource.LASER), 1));
        soundIDs.put(RawResource.ROCKET, soundPool.load(this, EnumUtil.getID(RawResource.ROCKET), 1));
        soundIDs.put(RawResource.EXPLOSION, soundPool.load(this, EnumUtil.getID(RawResource.EXPLOSION), 1));
        soundIDs.put(RawResource.BUTTON_CLICKED, soundPool.load(this, EnumUtil.getID(RawResource.BUTTON_CLICKED), 1));
        soundIDs.put(RawResource.TITLE_THEME, soundPool.load(this, EnumUtil.getID(RawResource.TITLE_THEME), 1));
        Log.d("Activity Class", soundIDs.size() + " sounds loaded");
    }

    // plays a sound using the SoundPool given SoundParams
    public static void playSound(SoundParams parameters) {
        soundPool.play(soundIDs.get(parameters.getResourceID()), parameters.getLeftVolume(),
                parameters.getRightVolume(), parameters.getPriority(), parameters.getLoop(),
                parameters.getRate());
    }

    // handle user pressing pause button
    public void onPausePressed(View view) {
        if(paused) { // unpause
            pauseButton.setBackgroundResource(R.drawable.pause);
            paused = false;
            soundPool.autoResume();
            pausedText.setVisibility(View.GONE);
            resumeButton.setVisibility(View.GONE);
            quitButton.setVisibility(View.GONE);
        } else { // pause
            pauseButton.setBackgroundResource(R.drawable.play);
            paused = true;
            soundPool.autoPause();
            pausedText.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.VISIBLE);
            quitButton.setVisibility(View.VISIBLE);
            // display pause dialog
            //PauseDialogFragment pauseDialog = PauseDialogFragment.newInstance(1.0f, 1.0f, false); // todo: put in real values
            //pauseDialog.show(getFragmentManager(), "fragment_pause_dialog");
            DialogFragment d = new PauseDialog2();
            d.show(getFragmentManager(), "Pause");
        }
    }

    public void onMutePressed(View view) {
        if(muted) {
            muteButton.setBackgroundResource(R.drawable.sound_on);
            muted = false;
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_off);
            muted = true;
        }
        AudioManager a_manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, muted);
    }

    public void onToggleBulletPressed(View view) {
        gameView.setFiringMode(Spaceship.FireMode.BULLET);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
    }

    public void onToggleRocketPressed(View view) {
        gameView.setFiringMode(Spaceship.FireMode.ROCKET);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button_pressed);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button);
    }

    public void onQuitPressed(View view) {
        Log.d("GameActivity", "Quitting game");
        quit = true;
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
        initMedia();
        Log.d("Activity Class", "Media Initialized");
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
        if (!paused) {
            Log.d("GameActivity", "Pausing the game");
            onPausePressed(gameView);
            Log.d("GameActivity", "Finished pausing the game");
        }
        soundPool.release();
        soundPool = null;
        //BitmapCache.destroyBitmaps();
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("GameActivity", "onStop called");
        if (!quit) { // save game state only if user did not quit on purpose // todo: this in onStop??
            Log.d("GameActivity.java", "Saving game state " + System.currentTimeMillis());
            gameView.saveGameState();
            Log.d("GameActivity", "Finished Save " + System.currentTimeMillis());
        } else { // ensure there is no saved game
            gameView.clearGameState();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gameView.updateInput(event.values[1]); // update gameView with current screen pitch // todo: this should be registered in GameActivity
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static boolean getPaused() {
        return paused;
    }

    public static int getScore() {
        return score;
    }

    public static void incrementScore(int toAdd) {
        score += toAdd;
        Log.d("GameActivity Class", "Incrementing Score by " + toAdd + " to " + score);
    }

    public static boolean isMuted() {
        return muted;
    }

    public static float getDifficulty() {
        return difficulty;
    }

    public static void incrementDifficulty(float toAdd) {
        difficulty += toAdd;
    }

}
