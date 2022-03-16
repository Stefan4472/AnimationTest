package com.plainsimple.spaceships.engine;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MotionEvent;

/**
 * Runs the GameEngine in a worker thread.
 */
public class GameRunner extends HandlerThread {
    private Handler mWorkerHandler;
    private final Handler mResponseHandler;
    private final Callback mCallback;
    private final GameEngine mGameEngine;

    public interface Callback {
        void onGameStateUpdated(GameUpdateMessage message);
    }

    public GameRunner(Handler responseHandler, Callback callback,
                      Context context, int screenWidthPx, int screenHeightPx) {
        super(GameRunner.class.getSimpleName());
        mResponseHandler = responseHandler;
        mCallback = callback;
        mGameEngine = new GameEngine(context.getApplicationContext(), screenWidthPx, screenHeightPx);

    }

    /*
    Send the signal to start the game.
     */
    public void startGame() {
        // TODO: could just do this on the first queueUpdate()
        mGameEngine.inputStartGame();
    }

    /*
    Tell the Thread to trigger a game update.
     */
    public void queueUpdate() {
        mWorkerHandler.obtainMessage().sendToTarget();
    }

    public void inputMotionEvent(MotionEvent e) {
        mGameEngine.inputExternalMotionEvent(e);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            /*
            Simply run GameEngine.update() and call the callback with
            the resulting UpdateMessage.
             */
            @Override
            public boolean handleMessage(Message msg) {
                final GameUpdateMessage m = mGameEngine.update();
//                Log.d(TAG, "Ran game update");
                // Report results back
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGameStateUpdated(m);
                    }
                });
//                msg.recycle();  TODO: DO WE NEED THIS? IS IT AUTO-RECYCLED?
                return true;
            }
        });
    }
}