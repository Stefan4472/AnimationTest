package com.plainsimple.spaceships.engine;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

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

    public GameRunner(Handler responseHandler, Callback callback, GameEngine gameEngine) {
        super(GameRunner.class.getSimpleName());
        mResponseHandler = responseHandler;
        mCallback = callback;
        // TODO: COULD REQUIRE `GAMEENGINE` TO BE PASSED WITH THE REQUEST
        mGameEngine = gameEngine;
    }

    /*
    Tell the Thread to trigger a game update.
     */
    public void queueUpdate() {
//        Log.d(TAG, "Queued update()");
        mWorkerHandler.obtainMessage().sendToTarget();
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