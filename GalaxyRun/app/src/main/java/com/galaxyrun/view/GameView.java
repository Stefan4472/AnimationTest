package com.galaxyrun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.util.FastQueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Built from starter tutorial https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-5-advanced-graphics-and-views/lesson-11-canvas/11-2-p-create-a-surfaceview/11-2-p-create-a-surfaceview.html
 */
public class GameView extends SurfaceView implements Runnable {

    /**
     * Event callbacks triggered by GameView.
     */
    public interface IGameViewListener {
        void onSizeSet(int widthPx, int heightPx);
        void handleScreenTouch(MotionEvent motionEvent);
    }

    private final Context context;
    private boolean isRunning;
    private Thread drawThread;
    private SurfaceHolder surfaceHolder;
    // Queue of game frames to draw
    private ConcurrentLinkedQueue<FastQueue<DrawInstruction>> drawFramesQueue;
    // Listener registered to this view
    private IGameViewListener gameViewListener;

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        drawFramesQueue = new ConcurrentLinkedQueue<>();
    }

    public void setListener(IGameViewListener gameViewListener) {
        this.gameViewListener = gameViewListener;
    }

    public void queueDrawFrame(FastQueue<DrawInstruction> drawInstructions) {
        drawFramesQueue.add(drawInstructions);
    }

    /*
    Called every time the size of the view is changed/set.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Log.d("GameView", String.format("surfaceChanged() called with %d, %d", width, height));

        if (width == 0 || height == 0){
            Log.d("GameView", "Doing nothing in response to surfaceChanged()");
            return;
        }

        // Tell GameActivity that we're ready to start
        gameViewListener.onSizeSet(width, height);
    }

    /*
    Runs in a separate thread. All drawing happens here. DrawInstructions
    are passed in via thread-safe queue.
     */
    @Override
    public void run() {
        Canvas canvas;
        // TODO: THIS IS NOT THE CORRECT WAY TO DO IT
        while (isRunning) {
            // TODO: MAKE SURE THAT THERE ISN'T A BUILD-UP OF DRAW FRAMES
            if (drawFramesQueue.size() > 1) {
                Log.w("GameView", drawFramesQueue.size() + " frames queued");
            }
            if (!drawFramesQueue.isEmpty()) {
                FastQueue<DrawInstruction> drawInstructions = drawFramesQueue.poll();
                if (surfaceHolder.getSurface().isValid()) {
                    canvas = surfaceHolder.lockCanvas();
                    drawFrame(canvas, drawInstructions);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawFrame(Canvas canvas, FastQueue<DrawInstruction> drawInstructions) {
//        Log.d("GameView", "Drawing Frame with " + drawParams.getSize() + " DrawParams");
        for (DrawInstruction drawInst : drawInstructions) {
            drawInst.draw(canvas);
        }
    }

    /*
    Starts up the drawing thread.
     */
    public void startThread() {
        isRunning = true;
        drawThread = new Thread(this);
        drawThread.start();
    }

    /*
    Stops the drawing thread.
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
