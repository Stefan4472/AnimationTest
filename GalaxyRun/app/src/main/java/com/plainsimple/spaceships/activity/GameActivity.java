package com.plainsimple.spaceships.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.plainsimple.spaceships.engine.GameRunner;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.engine.external.GameUpdateMessage;
import com.plainsimple.spaceships.engine.external.SoundPlayer;
import com.plainsimple.spaceships.view.GameView;

import androidx.fragment.app.FragmentActivity;
import plainsimple.spaceships.R;

/**
 * Activity containing the game.
 *
 * The Game logic is run in a parallel thread (`GameRunner`).
 * The GameActivity receives `DrawParams` for the
 * current game and forwards them to `GameView`, which draws them.
 */
public class GameActivity extends FragmentActivity implements
        GameRunner.Callback, // Receive game state updates
        GameView.IGameViewListener // Receive events from GameView
{
    // Runs the game in a separate thread
    private GameRunner mGameRunner;
    // View element that draws the game
    private GameView gameView;
    // Plays game audio
    private SoundPlayer soundPlayer;

    // Whether the Activity is in an active state
    private boolean isActivityActive;

    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);

        // Go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.game_layout);
        gameView = findViewById(R.id.spaceships);
        gameView.setListener(this);
        soundPlayer = new SoundPlayer(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivityActive = true;
        gameView.startThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityActive = false;
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
    IGameViewListener--handle touch event.
     */
    @Override
    public void handleScreenTouch(MotionEvent motionEvent) {
        mGameRunner.inputMotionEvent(motionEvent);
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
        mGameRunner = new GameRunner(
                new Handler(), this, getApplicationContext(), screenWidthPx, screenHeightPx);
        mGameRunner.start();
        mGameRunner.prepareHandler();
        // Send START signal and queue the first update
        mGameRunner.startGame();
        mGameRunner.queueUpdate();
    }

    /*
    GameRunner.Callback. Called when the next game state is ready.
     */
    @Override
    public void onGameStateUpdated(GameUpdateMessage updateMessage) {
        if (updateMessage.frameNumber > 0 && updateMessage.frameNumber % 150 == 0) {
            // Log debugging info every 150 frames
            Log.d("GameActivity", "FrameNumber = " + updateMessage.frameNumber);
            Log.d("GameActivity", "FPS = " + updateMessage.fps);
            Log.d("GameActivity", "Got " + updateMessage.getDrawParams().getSize() + " drawParams");
        }

        // TODO: may need to support pausing and resuming sounds
        for (SoundID sound : updateMessage.getSounds()) {
            soundPlayer.playSound(sound);
        }

        gameView.queueDrawFrame(updateMessage.getDrawParams());

        // Call the next update
        if (isActivityActive) {
            // Sleep--for testing  TODO: framerate management?
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {

            }
            mGameRunner.queueUpdate();
        }
        else {
            Log.d("GameActivity", "Activity is inactive");
        }
    }
}
