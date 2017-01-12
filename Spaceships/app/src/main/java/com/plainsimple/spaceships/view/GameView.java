package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.helper.*;
import com.plainsimple.spaceships.sprite.*;
import com.plainsimple.spaceships.util.GameEngineUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context c;
    private SurfaceHolder mySurfaceHolder;
    // todo: make non-static
    public static int screenW;
    public static int screenH;
    private boolean running = false;
    private boolean onTitle = true;
    private boolean initialized = false;
    private static int score = 0;
    private int difficulty = 0;
    // points a coin is worth
    public static final int COIN_VALUE = 100;
    // selected fire mode (bullet or rocket)
    private Spaceship.FireMode selectedFireMode = Spaceship.FireMode.BULLET;
    // selected input mode (gyro or button)
    private Spaceship.InputMode inputMode = Spaceship.InputMode.BUTTON;
    // whether to restore the default save game
    private boolean restoreGameState;
    private GameViewThread thread;
    // space background (implements parallax scrolling)
    private Background background;
    private Map map;
    //private DrawBackgroundService background;
    // speed of sprites scrolling across the map (must be negative!)
    private double scrollSpeed = -0.0025;
    // spaceship
    private Spaceship spaceship;
    private static final float MAX_SCROLL_SPEED = -0.03f;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    private static final float SCORING_CONST = 800;
    private Paint debugPaintRed = new Paint();
    private Paint debugPaintPink = new Paint();

    // health bar along bottom of screen
    private HealthBar healthBar;

    // score display in top left of screen
    private ScoreDisplay scoreDisplay;

    // listener passed in by GameActivity
    private GameEventsListener gameEventsListener;

    // button or gyro input
    private float input;

    // interface for events to fire
    public interface GameEventsListener { // todo: GameActivity should implement GameActivity.GameEventsListener
        // fired when spaceship has reached starting position
        void onGameStarted();
        // fired when spaceship has finished exploding
        void onGameFinished();
    }

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        // set up thread
        thread = new GameViewThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });
        setFocusable(true);
        debugPaintRed.setColor(Color.RED);
        debugPaintRed.setStyle(Paint.Style.STROKE);
        debugPaintRed.setStrokeWidth(3);
        debugPaintRed.setTextSize(20);
        debugPaintPink.setColor(Color.rgb(255, 105, 180));
        debugPaintPink.setStyle(Paint.Style.STROKE);
        debugPaintPink.setStrokeWidth(3);
    }

    class GameViewThread extends Thread {

        public GameViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            c = context;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        draw(canvas);
                    }
                } catch (NullPointerException e) {
                    Log.d("GameView", "Caught a NullPointer (canvas == null) and not sure what it means");
                } finally {
                    if (canvas != null) {
                        mySurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            if (!initialized) { // todo: find a better way (put this in onMeasure? But there were issues with pauseDialog
                initialized = true;
                // if flag is set, restore game state
                if (restoreGameState) {
                    //restoreGameState();
                    initNewGame();
                } else { // creates standard objects
                    initNewGame();
                }
            }
            background.draw(canvas);
            if (!GameActivity.getPaused()) {
                update();
            }
            // todo: how to draw bullets and rockets? drawparams in spaceship?
            for (Sprite s : spaceship.getProjectiles()) {
                GameEngineUtil.drawSprite(s, canvas, c);
            }
            map.draw(canvas, c);
            GameEngineUtil.drawSprite(spaceship, canvas, c);
            healthBar.draw(canvas);
            scoreDisplay.draw(canvas);
            canvas.drawText(Double.toString(scrollSpeed), 20, 100, debugPaintRed);
            canvas.drawText(Integer.toString(difficulty), 20, 150, debugPaintRed);
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            difficulty++;
            score += 1 + difficulty / SCORING_CONST;
            updateScrollSpeed();
            background.scroll(-scrollSpeed * screenW * SCROLL_SPEED_CONST);
            updateSpaceship();
            map.update(difficulty, scrollSpeed, spaceship);
            spaceship.updateAnimations();
            healthBar.setMovingToHealth(spaceship.getHP());
            scoreDisplay.update(score); // todo: clumsy
        }

        private void updateSpaceship() {
            // move spaceship to initial position
            if (spaceship.getX() > screenW / 4) {
                spaceship.setX(screenW / 4);
                spaceship.setSpeedX(0);
                spaceship.setControllable(true);
                if (gameEventsListener != null) {
                    gameEventsListener.onGameStarted();
                }
            }
            spaceship.updateInput(inputMode, input);
            spaceship.updateSpeeds();
            spaceship.move();
            spaceship.updateActions();
            GameEngineUtil.updateSprites(spaceship.getProjectiles());
        }

        // calculates scrollspeed based on difficulty
        public void updateScrollSpeed() {
            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
            scrollSpeed = -Math.log(difficulty + 1) / 600;
        }

        // handle user touching screen
        boolean doTouchEvent(MotionEvent motionEvent) {
            synchronized (mySurfaceHolder) {
                int event_action = motionEvent.getAction();
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                switch (event_action) {
                    case MotionEvent.ACTION_DOWN:
                        //if (!onTitle) {
                            spaceship.setFireMode(selectedFireMode);
                        //}
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP: // handle user clicking something
                        //if (onTitle) { // todo: tap to start?
                        //} else {
                            spaceship.setFireMode(Spaceship.FireMode.NONE);
                        //}
                        break;
                }
            }
            return true;
        }

        // initializes all objects required to start a new game
        private void initNewGame() {
            // calculate scaling factor using spaceship_sprite height as a baseline
            Bitmap spaceship_bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.spaceship);
            float scalingFactor = (screenH / 6.0f) / (float) spaceship_bmp.getHeight();
            BitmapCache.setScalingFactor(scalingFactor);
            // get spaceship image data from cache
            BitmapData ship_data = BitmapCache.getData(BitmapID.SPACESHIP, c);
            // initialize spaceship just off the screen in the middle
            spaceship = new Spaceship(-ship_data.getWidth(), screenH / 2 - ship_data.getHeight() / 2, c);
            spaceship.setBulletType(GameActivity.getEquippedBulletType());
            spaceship.setRocketType(GameActivity.getEquippedRocketType());
            spaceship.setHP(30);
            spaceship.setDamage(30);
            background = new Background(screenW, screenH); // todo: re-create background from save
            map = new Map(c, screenW, screenH);
            healthBar = new HealthBar(c, screenW, screenH, 30, 30);
            scoreDisplay = new ScoreDisplay(c, 0);
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                Log.d("GameView", "Screen Dimensions set to " + screenW + "," + screenH);
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return thread.doTouchEvent(motionEvent);
    }

    public void updateInput(float newInput) {
        input = newInput;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        if(thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
    }

    public void restartGame() {

    }

    public void saveGameState() {
        GameSave save = new GameSave(c);
        save.saveMap(map);
        save.saveBackground(background);
        save.saveHealthBar(healthBar);
        save.saveScoreDisplay(scoreDisplay);
        save.saveSpaceship(spaceship);
    }

    public void flagRestoreGameState() {
        this.restoreGameState = true;
    }

    private void restoreGameState() {
        GameSave save = new GameSave(c);
        long start_time = System.currentTimeMillis();
        Log.d("GameView.java", "Restoring Game State");
        map = save.loadMap();
        healthBar = save.loadHealthBar();
        scoreDisplay = save.loadScoreDisplay();
        spaceship = save.loadSpaceship();
        background = save.loadBackground();
        Log.d("GameView.java", "Finished restoring game state. Took " + (System.currentTimeMillis() - start_time) + "ms");
    }

    // sets spaceship's firing mode
    public void setFiringMode(Spaceship.FireMode fireMode) {
        selectedFireMode = fireMode;
    }

    public void setInputMode(Spaceship.InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public void setGameEventsListener(GameEventsListener gameEventsListener) {
        this.gameEventsListener = gameEventsListener;
    }

    public static void incrementScore(int toAdd) {
        score += toAdd;
    }
}
