package com.galaxyrun.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.galaxyrun.engine.GameRunner;
import com.galaxyrun.view.GameView;

import androidx.fragment.app.FragmentActivity;
import galaxyrun.R;

/**
 * Activity containing the game.
 *
 * The Game logic is run in a parallel thread (`GameRunner`).
 * The GameActivity receives `DrawInstructions` for the
 * current game and forwards them to `GameView`, which draws them.
 */
public class GameActivity extends FragmentActivity implements
        GameView.IGameViewListener, // Receive events from GameView
        SensorEventListener
{
    // Runs the game in a separate thread
    private GameRunner gameRunner;
    // View element that draws the game
    private GameView gameView;
    private SensorManager sensorManager;

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
        // TODO: we can probably use view.onLayoutChange() and view.OnTouchListener() directly.
        gameView = findViewById(R.id.spaceships);
        gameView.setListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameRunner != null) {
            gameRunner.resumeThread();
        }
        gameView.startThread();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameRunner != null) {
            gameRunner.finish();
        }
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
        if (gameRunner != null) {
            gameRunner.inputMotionEvent(motionEvent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (gameRunner != null) {
            gameRunner.inputSensorEvent(sensorEvent);
        }
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
                getApplicationContext(),
                gameView,
                false
//                BuildConfig.DEBUG
        );
        gameRunner.start();
        gameRunner.prepareHandler();
        gameRunner.startGame();
    }
}
