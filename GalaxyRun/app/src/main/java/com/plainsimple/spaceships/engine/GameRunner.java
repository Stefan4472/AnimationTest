package com.plainsimple.spaceships.engine;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.plainsimple.spaceships.helper.DrawParams;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 8/23/2020.
 */

public class GameRunner extends HandlerThread {
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private static final String TAG = GameRunner.class.getSimpleName();
    private Callback mCallback;
    private GameEngine mGameEngine;

    public interface Callback {
        void onGameStateUpdated(List<DrawParams> drawCalls, List<String> events);
    }

    public GameRunner(Handler responseHandler, Callback callback, GameEngine gameEngine) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCallback = callback;
        // TODO: COULD REQUIRE `GAMEENGINE` TO BE PASSED WITH THE REQUEST
        mGameEngine = gameEngine;
    }

    public void queueUpdate() {
//        Log.d(TAG, "Queued update()");
        mWorkerHandler.obtainMessage().sendToTarget();
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mGameEngine.update();
//                Log.d(TAG, "Ran game update");
                // Report results back
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGameStateUpdated(
                                new LinkedList<DrawParams>(),
                                new LinkedList<String>()
                        );
                    }
                });
//                msg.recycle();  TODO: DO WE NEED THIS? IS IT AUTO-RECYCLED?
                return true;
            }
        });
    }
}