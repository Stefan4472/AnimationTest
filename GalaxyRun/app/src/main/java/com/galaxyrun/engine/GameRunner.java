package com.galaxyrun.engine;

import android.content.Context;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.engine.external.GameUpdateMessage;
import com.galaxyrun.engine.external.SoundPlayer;
import com.galaxyrun.view.GameView;

import galaxyrun.R;

/**
 * Runs the GameEngine in a worker thread.
 */
public class GameRunner extends HandlerThread {
    private Handler mWorkerHandler;
    private final Handler mResponseHandler;
    private final GameEngine mGameEngine;
    private final GameView mGameView;
    private boolean isThreadPaused;
    // Whether game is currently muted.
    private boolean isMuted;
    // Plays game audio
    private final SoundPlayer soundPlayer;
    // Plays background song.
    // TODO: this should really be controlled by commands from GameEngine.
    //   I am taking a shortcut by directly playing the song from `GameActivity`
    private MediaPlayer songPlayer;
    private static final float MUSIC_VOLUME = 0.25f;

    private static final double TARGET_FPS = 30.0;
    private static final int MS_PER_UPDATE = (int) (1000.0 / TARGET_FPS);

    public GameRunner(
            Handler responseHandler,
            Context context,
            GameView gameView,
            boolean inDebugMode
    ) {
        super(GameRunner.class.getSimpleName());
        mResponseHandler = responseHandler;
        mGameView = gameView;
        soundPlayer = new SoundPlayer(context);
        songPlayer = MediaPlayer.create(context, R.raw.game_song);
        songPlayer.setVolume(MUSIC_VOLUME, MUSIC_VOLUME);
        songPlayer.setLooping(true);
        mGameEngine = new GameEngine(
                context.getApplicationContext(),
                mGameView.getWidth(),
                mGameView.getHeight(),
                inDebugMode
        );
    }

    /**
     * Starts the game.
     * TODO: is this even necessary? Logic is partially repeated from onResume().
     */
    public void startGame() {
        Log.d("GameRunner", "Starting the game.");
        mGameView.startThread();
        isThreadPaused = false;
        mGameEngine.inputExternalStartGame();
        songPlayer.start();
        queueUpdate();
    }

    public void onPause() {
        mGameEngine.inputExternalPauseGame();
        songPlayer.pause();
        mGameView.stopThread();
        isThreadPaused = true;
    }

    public void onResume() {
        mGameView.startThread();
        isThreadPaused = false;
        queueUpdate();
    }

    public void finish() {
        soundPlayer.release();
        songPlayer.release();
        songPlayer = null;
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
                        if (updateMessage.fps != 0 && updateMessage.fps < TARGET_FPS / 2) {
                            Log.w("GameActivity", "Low FPS: " + updateMessage.fps);
                        }

                        // Handle change of "muted" state.
                        // TODO: "muting" should be entirely up to the in-game logic.
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

                        mGameView.queueDrawFrame(updateMessage.getDrawInstructions());
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