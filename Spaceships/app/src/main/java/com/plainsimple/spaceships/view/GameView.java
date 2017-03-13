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
    // wehther game components have been initialized
    private boolean initialized;
    // whether to restore the default save game
    private boolean restoreGameState;
    // whether game has started (spaceship has reached starting position)
    private boolean gameStarted;
    // whether the spaceship has been destroyed
    private boolean spaceshipDestroyed;
    // whether game is completely finished and screen has come to a halt
    private boolean gameFinished;
    // current game score
    private static int score = 0;
    // current level of difficulty (used to determine some game mechanics)
    private int difficulty = 0;
    // used to store this run's stats
    public static GameStats currentStats;
    // used to keep track of how long this run has taken (takes account of pausing the game)
    //private GameTimer gameTimer = new GameTimer();
    // points a coin is worth
    public static final int COIN_VALUE = 100;
    // selected fire mode (bullet or rocket)
    private Spaceship.FireMode selectedFireMode = Spaceship.FireMode.BULLET;
    // selected input mode (gyro or button)
    private Spaceship.InputMode inputMode = Spaceship.InputMode.BUTTON;
    private GameViewThread thread;
    // space background (implements parallax scrolling)
    private Background background;
    private Map map;
    //private DrawBackgroundService background;
    // speed of sprites scrolling across the map (must be negative!)
    private static float scrollSpeed = -0.0025f;
    // spaceship
    private Spaceship spaceship;
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
        // fired when score changes (sends updated score)
        void onScoreChanged(int newScore);
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
                initNewGame();
                // if flag is set, restore game state by populating initialized objects
                if (restoreGameState) {
                    //restoreGameState();
                }
            }
            background.draw(canvas);
            if (!GameActivity.getPaused() && !gameFinished) {
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
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            if (gameStarted) {
                if (!spaceshipDestroyed) {
                    difficulty++;
                    score += 1 + difficulty / SCORING_CONST;
                }
                updateScrollSpeed();
                background.scroll(-scrollSpeed * screenW * SCROLL_SPEED_CONST);
            }
            updateSpaceship();
            map.update(difficulty, scrollSpeed, spaceship);
            spaceship.updateAnimations();
            healthBar.setMovingToHealth(spaceship.getHP());
            scoreDisplay.update(score);
//            gameEventsListener.onScoreChanged(score);
        }

        private void updateSpaceship() {
            // move spaceship to initial position
            if (spaceship.getX() > screenW / 4) {
                //gameTimer.start();
                gameStarted = true;
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
            if (spaceship.terminate()) {
                //gameTimer.stop();
                spaceshipDestroyed = true;
            }
        }

        // calculates scrollspeed based on difficulty
        public void updateScrollSpeed() {
            // spaceship destroyed: slow down scrolling to a halt and fire onGameFinished when scrollspeed = 0
            if (spaceshipDestroyed) {
                scrollSpeed /= 1.01f;
                if (scrollSpeed > -0.0001f) {
                    gameFinished = true;
                    Log.d("GameView.java", "OnGameFinished()");
                    gameEventsListener.onGameFinished();
                }
            } else { // normal scrolling progression
                //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
                scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
            }
        }

        // handle user touching screen
        boolean doTouchEvent(MotionEvent motionEvent) {
            synchronized (mySurfaceHolder) {
                if (gameStarted) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: // start of touch
                            spaceship.setFireMode(selectedFireMode);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP: // end of touch
                            spaceship.setFireMode(Spaceship.FireMode.NONE);
                            break;
                    }
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
            spaceship.setCannonType(GameActivity.getEquippedCannon());
            spaceship.setRocketType(GameActivity.getEquippedRocket());
            spaceship.setArmorType(GameActivity.getEquippedArmor());
            background = new Background(screenW, screenH); // todo: re-create background from save
            map = new Map(c, screenW, screenH);
            healthBar = new HealthBar(c, screenW, screenH, spaceship.getHP(), spaceship.getHP());
            scoreDisplay = new ScoreDisplay(c, 0);
            currentStats = new GameStats();
            gameFinished = false;
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

    // resets all elements and fields so that a new game can begin
    public void restartGame() {
        spaceship = new Spaceship(-spaceship.getWidth(), screenH / 2 - spaceship.getHeight() / 2, c);
        spaceship.setCannonType(GameActivity.getEquippedCannon());
        spaceship.setRocketType(GameActivity.getEquippedRocket());
        spaceship.setArmorType(GameActivity.getEquippedArmor());
        background.reset();
        map.reset();
        //gameTimer.reset();
        healthBar.setCurrentHealth(spaceship.getHP());
        healthBar.setMovingToHealth(spaceship.getHP());
        scoreDisplay.reset();
        spaceshipDestroyed = false;
        gameStarted = false;
        gameFinished = false;
        difficulty = 0;
        score = 0;
        currentStats = new GameStats();
    }

    // updates this run's currentStats which aren't necessarily constantly updated
    public void forceUpdateStats() {
        currentStats.set(GameStats.GAME_SCORE, score);
        currentStats.set(GameStats.DISTANCE_TRAVELED, background.getDistanceTravelled());
        //currentStats.set(GameStats.TIME_PLAYED, gameTimer.getTotalTime());
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
        save.loadMap(map);
        /*healthBar = save.loadHealthBar();
        scoreDisplay = save.loadScoreDisplay();
        spaceship = save.loadSpaceship();
        background = save.loadBackground();*/
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

    public static float getScrollSpeed() {
        return scrollSpeed;
    }

    //public GameTimer getGameTimer() {
    //    return gameTimer;
    //}
}
