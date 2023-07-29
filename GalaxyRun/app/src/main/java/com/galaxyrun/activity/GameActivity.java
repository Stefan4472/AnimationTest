package com.galaxyrun.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.galaxyrun.engine.GameRunner;
import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.engine.external.GameUpdateMessage;
import com.galaxyrun.engine.external.SoundPlayer;
import com.galaxyrun.view.GameView;

import androidx.fragment.app.FragmentActivity;
//import galaxyrun.BuildConfig;
import galaxyrun.R;

/**
 * Activity containing the game.
 *
 * The Game logic is run in a parallel thread (`GameRunner`).
 * The GameActivity receives `DrawInstructions` for the
 * current game and forwards them to `GameView`, which draws them.
 */
public class GameActivity extends FragmentActivity implements
        GameRunner.Callback, // Receive game state updates
        GameView.IGameViewListener, // Receive events from GameView
        SensorEventListener
{
    // Runs the game in a separate thread
    private GameRunner gameRunner;
    // View element that draws the game
    private GameView gameView;
    // Whether game is currently muted.
    private boolean isMuted;
    // Plays game audio
    private SoundPlayer soundPlayer;
    // Plays background song.
    // TODO: this should really be controlled by commands from GameEngine.
    //   I am taking a shortcut by directly playing the song from `GameActivity`
    private MediaPlayer songPlayer;
    private SensorManager sensorManager;
    private static final float MUSIC_VOLUME = 0.25f;

    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        // Hide title and action bar. Make navbar transparent.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );
        setContentView(R.layout.game_layout);
        gameView = findViewById(R.id.spaceships);
        gameView.setListener(this);
        soundPlayer = new SoundPlayer(getApplicationContext());
        songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.game_song);
        songPlayer.setVolume(MUSIC_VOLUME, MUSIC_VOLUME);
        songPlayer.setLooping(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameRunner != null) {
            gameRunner.resumeThread();
        }
        gameView.startThread();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        songPlayer.start();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            // TODO: how to handle this?
            Log.d("GameView Class", "No Gyroscope");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gameRunner != null) {
            gameRunner.pauseThread();
        }
        gameView.stopThread();
        songPlayer.pause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPlayer.release();
        songPlayer.release();
        songPlayer = null;
    }

    /*
    IGameViewListener--handle dimensions determined.
     */
    @Override
    public void onSizeSet(int widthPx, int heightPx) {
        initialize(widthPx, heightPx);
    }

    /*
    IGameViewListener--handle touch event.
     */
    @Override
    public void handleScreenTouch(MotionEvent motionEvent) {
        gameRunner.inputMotionEvent(motionEvent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        gameRunner.inputSensorEvent(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /*
    Initialize the game. This is called once the GameView has been sized.
    We have to wait for this because we need to know how big our screen is.
     */
    private void initialize(int screenWidthPx, int screenHeightPx) {
        Log.d("GameActivity", String.format(
                "initialize() called w/width %d, height %d", screenWidthPx, screenHeightPx
        ));

        // Create GameRunner background thread
        gameRunner = new GameRunner(
                new Handler(),
                this,
                getApplicationContext(),
                screenWidthPx,
                screenHeightPx,
                false
//                BuildConfig.DEBUG
        );
        gameRunner.start();
        gameRunner.prepareHandler();
        gameRunner.startGame();
    }

    /*
    GameRunner.Callback. Called when the next game state is ready.
     */
    @Override
    public void onGameStateUpdated(GameUpdateMessage updateMessage) {
        if (updateMessage.fps != 0 && updateMessage.fps < 30) {
            Log.w("GameActivity", "FPS below 30! FPS = " + updateMessage.fps);
        }

        // Handle change of "muted" state.
        if (isMuted != updateMessage.isMuted) {
            if (songPlayer != null) {
                float newVolume = updateMessage.isMuted ? 0 : MUSIC_VOLUME;
                songPlayer.setVolume(newVolume, newVolume);
            }
            isMuted = updateMessage.isMuted;
        }

        // Play sounds if not muted
        if (soundPlayer != null && !isMuted) {
            for (SoundID sound : updateMessage.getSounds()) {
                soundPlayer.playSound(sound);
            }
        }

        gameView.queueDrawFrame(updateMessage.getDrawInstructions());
    }
}
