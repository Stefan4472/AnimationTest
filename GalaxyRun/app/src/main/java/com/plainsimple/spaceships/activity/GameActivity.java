package com.plainsimple.spaceships.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.plainsimple.spaceships.engine.GameEngine;
import com.plainsimple.spaceships.engine.GameRunner;
import com.plainsimple.spaceships.engine.GameUpdateMessage;
import com.plainsimple.spaceships.view.GameView;

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
        GameView.IGameViewListener // Receive events from GameView
{
    private GameEngine gameEngine;
    private GameRunner mGameRunner;

    private long startTime;
    private long numUpdates;

    // View element that draws the game
    private GameView gameView;

    // Whether to keep running the game in a background thread
    //private boolean isRunning; // TODO: improve and work with ACTIVITY LIFECYCLE! -> stop the background thread when activity is not being shown

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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
        gameView.startThread();
        // TODO: send message to GameEngine
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
        gameView.stopThread();
        // TODO: send message to GameEngine
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
}
