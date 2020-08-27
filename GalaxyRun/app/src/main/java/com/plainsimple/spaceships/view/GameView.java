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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.plainsimple.spaceships.activity.IGameActivity;
import com.plainsimple.spaceships.helper.*;
import com.plainsimple.spaceships.util.FastQueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Stefan on 10/17/2015.
 *
 * Built from starter tutorial https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-5-advanced-graphics-and-views/lesson-11-canvas/11-2-p-create-a-surfaceview/11-2-p-create-a-surfaceview.html
 */
public class GameView extends SurfaceView implements Runnable {

    private Context context;
    private boolean isRunning;
    private Thread drawThread;
    private BitmapCache bitmapCache;
    private SurfaceHolder surfaceHolder;
    private int viewWidth, viewHeight;
    // Queue of game frames to draw
    private ConcurrentLinkedQueue<FastQueue<DrawParams>> drawFramesQueue;

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
        this.context = context;
        surfaceHolder = getHolder();
        drawFramesQueue = new ConcurrentLinkedQueue<>();
        // TODO: HOW TO GET BITMAPCACHE?
    }

    public void setGameActivityInterface(IGameActivity gameActivityInterface) {
        this.gameActivityInterface = gameActivityInterface;
    }

    public void setGameViewListener(IGameViewListener gameViewListener) {
        this.gameViewListener = gameViewListener;
    }

    public void setBitmapCache(BitmapCache bitmapCache) {
        this.bitmapCache = bitmapCache;
    }

    public void queueDrawFrame(FastQueue<DrawParams> drawParams) {
        drawFramesQueue.add(drawParams);
    }

    /*
    Called every time the size of the view is changed/set.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        assert(gameActivityInterface != null);
        Log.d("GameView", String.format("surfaceChanged() called with %d, %d", width, height));

        if (width == 0 || height == 0){
            Log.d("GameView", "Doing nothing in response to surfaceChanged()");
            return;
        }

        // Ask the GameActivity what dimensions should be used by the game
        viewWidth = gameActivityInterface.calcPlayableWidth(width);
        viewHeight = gameActivityInterface.calcPlayableHeight(height);

        background = new Background(width, height);
        scoreDisplay = new ScoreDisplay(context, 0);
        // Tell GameActivity that we're ready to start
        gameActivityInterface.onSizeSet(width, height);
    }

    /*
    Runs in a separate thread. All drawing happens here. DrawParams
    are passed in via thread-safe queue.
     */
    @Override
    public void run() {
        Canvas canvas;
        // TODO: THIS IS NOT THE CORRECT WAY TO DO IT
        while (isRunning) {
            // TODO: MAKE SURE THAT THERE ISN'T A BUILD-UP OF DRAW FRAMES
            if (!drawFramesQueue.isEmpty()) {
                FastQueue<DrawParams> draw_params = drawFramesQueue.poll();
                if (surfaceHolder.getSurface().isValid()) {
                    canvas = surfaceHolder.lockCanvas();
                    updateSubViews();
                    drawFrame(canvas, draw_params);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawFrame(Canvas canvas, FastQueue<DrawParams> drawParams) {
        if (background != null) {
            background.draw(canvas);
        }

        if (scoreDisplay != null) {
            scoreDisplay.update(100);
            scoreDisplay.draw(canvas);
        }
        Log.d("GameView", String.format("drawFrame() got %d DrawParams", drawParams.getSize()));
        for (DrawParams draw_param : drawParams) {
            draw_param.draw(canvas, bitmapCache);
        }
        Log.d("GameView", "finished drawing");
        // fill the area outside of playScreenH but in screenH with black
//            canvas.drawRect(0, playScreenH, screenW, screenH, blackPaint);
    }

    public void updateSubViews() {
//            background.scroll(-gameEngine.scrollSpeed * gameEngine.gameWidthPx * gameEngine.SCROLL_SPEED_CONST);
//            scoreDisplay.update(gameEngine.score);
    }

    /*
    Starts up the drawing thread.
    Call from GameActivity.onResume()
     */
    public void startThread() {
        isRunning = true;
        drawThread = new Thread(this);
        drawThread.start();
    }

    /*
    Stops the drawing thread.
    Call from GameActivity.onPause()
     */
    public void stopThread() {
        isRunning = false;
        try {
            drawThread.join();
        } catch (InterruptedException e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        assert(gameViewListener != null);
        gameViewListener.handleScreenTouch(motionEvent);
        return true;
    }
}
