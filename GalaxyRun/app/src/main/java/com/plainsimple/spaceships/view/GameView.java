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
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.stats.GameTimer;
import com.plainsimple.spaceships.store.ArmorType;
import com.plainsimple.spaceships.store.CannonType;
import com.plainsimple.spaceships.store.RocketType;
import com.plainsimple.spaceships.util.GameEngineUtil;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context c;
    private SurfaceHolder mySurfaceHolder;
    // GameView dimensions. screenW and screenH are full dimensions.
    // playScreenH is the height of the "playable" screen. This can be
    // configured (e.g. to remove what's underneath the HealthBarView)
    public static int screenW;
    public static int playScreenH;
    private static int screenH;
    private boolean running = false;
    private boolean isPaused;
    // wehther game components have been initialized
    private boolean initialized;
    // whether game has started (spaceship has reached starting position)
    private boolean gameStarted;
    // whether the spaceship has been destroyed
    private boolean spaceshipDestroyed;
    // whether game is completely finished and screen has come to a halt
    private boolean gameFinished;
    // current game score
    private static int score = 0;
    // current level of difficulty (used to determine some game mechanics)
    private float difficulty = 0;
    // defines this game's GameMode
    private GameMode gameMode;
    // used to store this run's stats
    public static GameStats currentStats;
    // tracks duration of this game (non-paused). Start/Pause controlled by GameActivity
    private GameTimer gameTimer = new GameTimer();
    // paint object
    private Paint blackPaint;
    // points a coin is worth
    public static final int COIN_VALUE = 100;
    // selected fire mode (bullet or rocket)
    private Spaceship.FireMode selectedFireMode = Spaceship.FireMode.BULLET;
    private GameViewThread thread;
    // scrolling space background (moves at SCROLL_SPEED_CONST of regular sprites)
    private Background background;
    // runs sprite generation and updating
    private GameDriver gameDriver;
    // speed of sprites scrolling across the screen (must be negative!)
    private static float scrollSpeed = -0.0025f;
    // spaceship
    private Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;
    // number of frames that must pass before score per frame is increased
    private static final float SCORING_CONST = 800;

    // this game's level of difficulty (default to EASY)
    private Difficulty difficultyLevel = Difficulty.EASY;

    // available difficulty levels with their difficulty increments per frame
    public enum Difficulty {
        EASY(0.6f), MEDIUM(1.0f), HARD(1.4f);

        private float perFrameIncrease;
        public float getPerFrameIncrease() {
            return perFrameIncrease;
        }
        Difficulty(float perFrameIncrease) {
            this.perFrameIncrease = perFrameIncrease;
        }
    }

    // score display in top left of screen
    // TODO: THIS SHOULD REALLY BE A UI ELEMENT IN GAMEACTIVITY
    private ScoreDisplay scoreDisplay;

    // listener passed in by GameActivity
    private GameEventsListener gameEventsListener;

    // interface for events to fire
    public interface GameEventsListener {
        // fired when the GameView's dimensions have been determined (setSurfaceSize)
        // returns an int, which is the screenHeight the game should be set
        // this allows the height of the screen used to be different that the full
        // height of the GameView
        int onGameViewSurfaced(int screenHeight);
        // fired when spaceship has reached starting position
        void onGameStarted();
        // fired when screen come to a halt and it's time to pop up GameOverDialog
        void onGameFinished();
        // fired when spaceship's health changes
        void onHealthChanged(int healthChange);
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
        // init blackPaint object to fill
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);
    }

    class GameViewThread extends Thread implements Spaceship.SpaceshipListener {

        public GameViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            c = context;
        }

        private Canvas canvas;
        @Override
        public void run() {
            while (running) {
                // TODO: THIS IS NOT THE CORRECT WAY TO DO IT
                canvas = null;
//                try {
                    canvas = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        draw(canvas);
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

        private void draw(Canvas canvas) {
            // check if game has been initialized
            if (!initialized) {
                Log.d("GameView", "Initializing new game");
                initialized = true;
                initNewGame();
            }
            background.draw(canvas);
            if (!isPaused && !gameFinished) {
                update();
            }
            // todo: how to draw bullets and rockets? drawparams in spaceship?
            for (Sprite s : spaceship.getProjectiles()) {
                GameEngineUtil.drawSprite(s, canvas, c);
            }
            gameDriver.draw(canvas, c);
            GameEngineUtil.drawSprite(spaceship, canvas, c);
            scoreDisplay.draw(canvas);
            // fill the area outside of playScreenH but in screenH with black
            canvas.drawRect(0, playScreenH, screenW, screenH, blackPaint);
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            if (gameStarted) {
                if (!spaceshipDestroyed) {
                    // increment difficulty by amount specified in difficultyLevel
                    difficulty += difficultyLevel.getPerFrameIncrease();
                    score += 1 + difficulty / SCORING_CONST;
                }
                updateScrollSpeed();
                background.scroll(-scrollSpeed * screenW * SCROLL_SPEED_CONST);
            }
            updateSpaceship();
            gameDriver.update((int) difficulty, scrollSpeed, spaceship);
            spaceship.updateAnimations();
            scoreDisplay.update(score);
        }

        private void updateSpaceship() {
            // move spaceship to initial position
            if (!gameStarted && spaceship.getX() > screenW / 4) {
                gameTimer.start();
                gameStarted = true;
                spaceship.setX(screenW / 4);
                spaceship.setSpeedX(0);
                spaceship.setControllable(true);
                if (gameEventsListener != null) {
                    gameEventsListener.onGameStarted();
                }
            }
            spaceship.updateSpeeds();
            spaceship.move();
            spaceship.updateActions();
            GameEngineUtil.updateSprites(spaceship.getProjectiles());
        }

        // calculates scrollspeed based on difficulty
        public void updateScrollSpeed() {
            // spaceship destroyed: slow down scrolling to a halt and fire onGameFinished when scrollspeed = 0
            if (spaceshipDestroyed) {
                scrollSpeed /= 1.03f;
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

        @Override // handle spaceship changing health (send back to GameEventsListener)
        public void onHealthChanged(int change) {
            Log.d("GameView.java", "Received onHealthChanged for " + change);
            gameEventsListener.onHealthChanged(change);
        }

        @Override // handle spaceship becoming invisible
        public void onInvisible() {
            Log.d("GameView.java", "Received onInvisible");
            spaceshipDestroyed = true;
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
            float scalingFactor = (playScreenH / 6.0f) / (float) spaceship_bmp.getHeight();
            BitmapCache.setScalingFactor(scalingFactor);
            // get spaceship image data from cache
            BitmapData ship_data = BitmapCache.getData(BitmapID.SPACESHIP, c);

            // initialize spaceship just off the screen centered vertically
            spaceship = new Spaceship(-ship_data.getWidth(), playScreenH / 2 - ship_data.getHeight() / 2, c);
            // set spaceship equipment based on settings in GameActivity
            spaceship.setCannonType(CannonType.CANNON_0);
            spaceship.setRocketType(RocketType.ROCKET_0);
            spaceship.setArmorType(ArmorType.ARMOR_0);
            // set this class to receive Spaceship events
            spaceship.setListener(this);

            // initialize other required components
            background = new Background(screenW, playScreenH);
            gameDriver = new GameDriver(c, screenW, playScreenH, gameMode.getLevelData());
            scoreDisplay = new ScoreDisplay(c, 0);
            currentStats = new GameStats();
            gameFinished = false;
            score = 0;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                // fire event, asking receiver height of screen *that should be used* todo: more elegant way? using layout?
                if (gameEventsListener != null) {
                    playScreenH = gameEventsListener.onGameViewSurfaced(height);
                } else {
                    playScreenH = height;
                }
                Log.d("GameView", "Screen Dimensions set to " + screenW + "," + playScreenH);
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

    public void setDirectionInput(Spaceship.Direction newDirection) {
        spaceship.updateInput(newDirection);
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public int getPlayerStartingHealth() {
        return 100;  // TODO
    }

    public int getPlayerHealth() {
        return 100; // TODO
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
        spaceship.reset();
        spaceship.setX(-spaceship.getWidth());
        spaceship.setY(screenH / 2 - spaceship.getHeight() / 2);
        background.reset();
        gameDriver.reset();
        gameTimer.reset();
        scoreDisplay.reset();
        spaceshipDestroyed = false;
        gameStarted = false;
        gameFinished = false;
        difficulty = 0;
        score = 0;
        currentStats = new GameStats();
    }

    // sets difficultyLevel of the game
    public void setDifficultyLevel(Difficulty difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        Log.d("GameView", "Difficulty Level set to " + difficultyLevel);
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

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }
}
