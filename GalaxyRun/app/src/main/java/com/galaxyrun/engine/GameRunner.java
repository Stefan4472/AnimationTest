package com.galaxyrun.engine;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.galaxyrun.engine.external.GameUpdateMessage;

/**
 * Runs the GameEngine in a worker thread.
 */
public class GameRunner extends HandlerThread {
    private Handler mWorkerHandler;
    private final Handler mResponseHandler;
    private final Callback mCallback;
    private final GameEngine mGameEngine;
    private boolean isThreadPaused;

    private static final double TARGET_FPS = 30.0;
    private static final int MS_PER_UPDATE = (int) (1000.0 / TARGET_FPS);

    public interface Callback {
        void onGameStateUpdated(GameUpdateMessage message);
    }

    public GameRunner(
            Handler responseHandler,
            Callback callback,
            Context context,
            int screenWidthPx,
            int screenHeightPx,
            boolean inDebugMode
    ) {
        super(GameRunner.class.getSimpleName());
        mResponseHandler = responseHandler;
        mCallback = callback;
        mGameEngine = new GameEngine(
                context.getApplicationContext(),
                screenWidthPx,
                screenHeightPx,
                inDebugMode
        );
    }

    /*
    Send the signal to start the game.
     */
    public void startGame() {
        mGameEngine.inputExternalStartGame();
        queueUpdate();
    }

    public void pauseThread() {
        mGameEngine.inputExternalPauseGame();
        isThreadPaused = true;
    }

    public void resumeThread() {
        isThreadPaused = false;
        queueUpdate();
    }

    public void inputMotionEvent(MotionEvent e) {
        mGameEngine.inputExternalMotionEvent(e);
    }

    public void inputSensorEvent(SensorEvent e) {
        mGameEngine.inputExternalSensorEvent(e);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            // Simply run GameEngine.update() and report the resulting
            // UpdateMessage to the callback.
            @Override
            public boolean handleMessage(Message msg) {
                long updateTime = System.currentTimeMillis();

                // Call game engine update
                final GameUpdateMessage updateMessage = mGameEngine.update();

                // Report results
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGameStateUpdated(updateMessage);
                    }
                });

                // Queue next update
                if (!isThreadPaused) {
                    // Calculate when the next update should occur in order to
                    // achieve TARGET_FPS.
                    long nextUpdate = updateTime + MS_PER_UPDATE;
                    long currTime = System.currentTimeMillis();
                    if (currTime < nextUpdate) {
                        try {
                            // Sleep until it's time to run the next frame.
                            Thread.sleep(nextUpdate - currTime);
                        } catch (InterruptedException e) {
                            Log.e("GameRunner", "Sleep was interrupted: " + e.getMessage());
                        }
                    } else {
                        Log.w("GameRunner", "Slipped by " + (currTime - nextUpdate) + "ms");
                    }

                    queueUpdate();
                }

                return true;
            }
        });
    }

    /*
    Tell the Thread to trigger a game update.
     */
    private void queueUpdate() {
        mWorkerHandler.obtainMessage().sendToTarget();
    }
}