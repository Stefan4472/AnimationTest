package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.plainsimple.spaceships.activity.IGameActivity;
import com.plainsimple.spaceships.engine.GameEngine;
import com.plainsimple.spaceships.engine.IGameServiceProvider;
import com.plainsimple.spaceships.helper.*;
import com.plainsimple.spaceships.sprite.*;
import com.plainsimple.spaceships.util.GameEngineUtil;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements
        SurfaceHolder.Callback {

    // SurfaceHolder to the View's canvas
    private SurfaceHolder mySurfaceHolder;
    // Internal thread that runs the GameEngine and draws to the screen
    private GameViewThread thread;

    // TODO: THIS SHOULD REALLY BE A UI ELEMENT IN GAMEACTIVITY--BUT THAT WOULD LIKELY IMPACT PERFORMANCE DUE TO RUNONUITHREAD()
    // Score display in top left of screen
    private ScoreDisplay scoreDisplay;
    // Scrolling space background (moves at SCROLL_SPEED_CONST of regular sprites)
    private Background background;

    // Interface to the GameActivity. Provides a couple utility methods.
    private IGameActivity gameActivityInterface;
    // Listener registered to this view
    private IGameViewListener gameViewListener;

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // Set up thread
        thread = new GameViewThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });
        setFocusable(true);
    }

    public void setGameActivityInterface(IGameActivity gameActivityInterface) {
        this.gameActivityInterface = gameActivityInterface;
    }

    public void setGameViewListener(IGameViewListener gameViewListener) {
        this.gameViewListener = gameViewListener;
    }

    /* Begin SurfaceHolder overrides */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        assert(gameViewListener != null);
        gameViewListener.handleScreenTouch(motionEvent);
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        assert(gameActivityInterface != null);
        // Ask the GameActivity what dimensions should be used by the game
        // TODO: more elegant way? using layout?
        Log.d("GameView", String.format("surfaceChanged() called with %d, %d", width, height));
        int playable_width = gameActivityInterface.calcPlayableWidth(width);
        int playable_height = gameActivityInterface.calcPlayableHeight(height);
        thread.setSurfaceSize(playable_width, playable_height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GameView", "surfaceCreated() called");
        thread.setRunning(true);
        if(thread.getState() == Thread.State.NEW) {
            Log.d("GameView", "Starting thread");
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GameView", "surfaceDestroyed() called");
        thread.setRunning(false);
    }

    /*
    Thread that runs the game and draws the GameView.
     */
    class GameViewThread extends Thread {
        // Canvas used for drawing the game
        private Canvas canvas;
        // Paint object used to clear the canvas before drawing the next frame
        private Paint blackPaint;
        // Whether the thread is running
        private boolean isRunning;
        private Context context;

        public GameViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            this.context = context;
//            GameView.this.context = context;  // TODO: IS THIS CORRECT?

            // Init blackPaint object to fill
            blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            blackPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void run() {
            // TODO: THIS IS NOT THE CORRECT WAY TO DO IT
            while (isRunning) {
                canvas = null;
//                try {
                    canvas = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        draw();
                    }
//                } catch (NullPointerException e) {
//                    Log.d("GameView", "Caught a NullPointer, canvas is null (" + e.getMessage() + ")");
//                } finally {
                    if (canvas != null) {
                        mySurfaceHolder.unlockCanvasAndPost(canvas);
                    }
//                }
            }
        }

        private void draw() {
            if (background != null) {
                background.draw(canvas);
            }

            // todo: how to draw bullets and rockets? drawparams in spaceship?
//            for (Sprite s : gameEngine.spaceship.getProjectiles()) {
//                GameEngineUtil.drawSprite(s, canvas, context);
//            }
//            gameEngine.gameDriver.draw(canvas, context);
//            GameEngineUtil.drawSprite(gameEngine.spaceship, canvas, context);
            if (scoreDisplay != null) {
                scoreDisplay.update(100);
                scoreDisplay.draw(canvas);
            }
            // fill the area outside of playScreenH but in screenH with black
//            canvas.drawRect(0, playScreenH, screenW, screenH, blackPaint);
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
//            background.scroll(-gameEngine.scrollSpeed * gameEngine.gameWidthPx * gameEngine.SCROLL_SPEED_CONST);
//            scoreDisplay.update(gameEngine.score);
        }

        public void setSurfaceSize(int width, int height) {
            Log.d("GameView", String.format("setSurfaceSize() called %d, %d", width, height));
            synchronized (mySurfaceHolder) {
//                gameEngine = new GameEngine(context, width, height);
                background = new Background(width, height);
                scoreDisplay = new ScoreDisplay(context, 0);
            }
            gameActivityInterface.onSizeSet(width, height);
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
    }
}
