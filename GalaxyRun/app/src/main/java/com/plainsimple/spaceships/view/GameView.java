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
        SurfaceHolder.Callback,
        IGameServiceProvider {

//    private Context context;
    private SurfaceHolder mySurfaceHolder;
    // GameView dimensions. screenW and screenH are full dimensions.
    // playScreenH is the height of the "playable" screen. This can be
    // configured (e.g. to remove what's underneath the HealthBarView)
//    public static int screenW;
//    public static int playScreenH;
//    private static int screenH;

    // Internal thread that runs the GameEngine and draws to the screen
    private GameViewThread thread;

    // Runs game logic, which is then drawn to screen by this view
    public GameEngine gameEngine;
    // Scrolling space background (moves at SCROLL_SPEED_CONST of regular sprites)
    private Background background;
    // Score display in top left of screen
    // TODO: THIS SHOULD REALLY BE A UI ELEMENT IN GAMEACTIVITY--BUT THAT WOULD LIKELY IMPACT PERFORMANCE DUE TO RUNONUITHREAD()
    private ScoreDisplay scoreDisplay;

    // Interface to the GameActivity. Provides a couple utility methods.
    private IGameActivity gameActivityInterface;
    // Listener registered to this view
    private IGameViewListener gameViewListener;

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        gameEngine = new GameEngine(context, 0, 0);  // TODO: HOW TO SET SCREEN SIZE? WE DON'T KNOW IT YET

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
        return thread.doTouchEvent(motionEvent);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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

    /* Begin IGameServiceProvider overriedes */
    public Context getActivityContext() {
        return getContext();
    }

    public void playSound() {
        // TODO
    }

    public int getScreenWidth() {
        return 0;
    }

    public int getScreenHeight() {
        return 0;
    }

    /* Begin utility functions for GameActivity to use */
    public void startPlayerMovingUp() {
        gameEngine.inputStartMoveUp();
    }

    public void startPlayerMovingDown() {
        gameEngine.inputStartMoveDown();
    }

    public void stopPlayerMoving() {
        gameEngine.inputStopMoving();
    }

    public void pauseGame() {
        gameEngine.inputPause();
    }

    public void resumeGame() {
        gameEngine.inputResume();
    }

    public void startGame() {

    }

    public void restartGame() {
        gameEngine.inputRestart();
        background.reset();
        scoreDisplay.reset();
    }

    private void initGame() {

    }

    public int getPlayerStartingHealth() {
        return gameEngine.getPlayerStartingHealth();
    }

    public int getPlayerHealth() {
        return gameEngine.getPlayerHealth();
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
                        gameEngine.update();
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

        // TODO: OUTSIDE METHOD NEEDS TO CALL INITNEWGAME()
        // TODO: CAN WE MOVE THIS OUT TO THE GAMEVIEW OUTER CLASS?
        private void draw() {
            // TODO: MAKE SURE GAME HAS BEEN STARTED/INITIALIZED
            background.draw(canvas);

            // todo: how to draw bullets and rockets? drawparams in spaceship?
            for (Sprite s : gameEngine.spaceship.getProjectiles()) {
                GameEngineUtil.drawSprite(s, canvas, context);
            }
            gameEngine.gameDriver.draw(canvas, context);
            GameEngineUtil.drawSprite(gameEngine.spaceship, canvas, context);
            scoreDisplay.draw(canvas);
            // fill the area outside of playScreenH but in screenH with black
//            canvas.drawRect(0, playScreenH, screenW, screenH, blackPaint);
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            background.scroll(-gameEngine.scrollSpeed * gameEngine.screenWidth * gameEngine.SCROLL_SPEED_CONST);
            scoreDisplay.update(gameEngine.score);
        }

        // handle user touching screen
        boolean doTouchEvent(MotionEvent motionEvent) {
            synchronized (mySurfaceHolder) {
                switch (motionEvent.getAction()) {
                    // Start of touch
                    case MotionEvent.ACTION_DOWN:
                        gameEngine.inputStartShooting();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    // End of touch
                    case MotionEvent.ACTION_UP:
                        gameEngine.inputEndShooting();
                        break;
                }
            }
            return true;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                gameEngine = new GameEngine(context, width, height);
            }
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
    }
}
