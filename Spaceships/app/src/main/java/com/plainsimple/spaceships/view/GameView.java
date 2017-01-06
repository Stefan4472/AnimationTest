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
    // selected fire mode (bullet or rocket)
    private Spaceship.FireMode selectedFireMode = Spaceship.FireMode.BULLET;
    // selected input mode (gyro or button)
    private Spaceship.InputMode inputMode = Spaceship.InputMode.BUTTON;
    // name of game save to restore (null if none)
    private String restoreGameState = null;
    private GameViewThread thread;
    private Background background;
    private Map map;
    // space background (implements parallax scrolling)
    //private DrawBackgroundService background;
    // default speed of sprites scrolling across the map (must be negative!)
    private float scrollSpeed = -0.0025f;
    // spaceship
    private Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;
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
    public interface GameEventsListener {
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
                } finally {
                    if (canvas != null) {
                        mySurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            //((GameActivity) c).incrementScore(1);
            background.draw(canvas);
            if(!GameActivity.getPaused()) {
                update();
                background.scroll((int) (-scrollSpeed * screenW * SCROLL_SPEED_CONST * 8));
                //background.scroll(-10);
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
            //score += difficulty / 2; // todo: increment score based on difficulty
            GameActivity.incrementDifficulty(0.01f);
            //updateScrollSpeed();
            updateSpaceship();
            map.update(GameActivity.getDifficulty(), scrollSpeed, spaceship);
            spaceship.updateAnimations();
            healthBar.setMovingToHealth(spaceship.getHP());
            scoreDisplay.update(GameActivity.getScore()); // todo: clumsy
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
        // difficulty starts at 0 and increases by 0.01/frame,
        // or 1 per second
        public void updateScrollSpeed() {
            scrollSpeed = (float) (-0.0025f - GameActivity.getDifficulty() / 2500.0);
            if (scrollSpeed < -0.025) { // scroll speed ceiling
                scrollSpeed = -0.025f;
            }
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

        // loads in sprites, sends ID's to the proper classes, and scales them
        private void initImgCache() { // todo: determine current rocket type equipped
            // calculate scaling factor using spaceship_sprite height as a baseline
            Bitmap spaceship = BitmapFactory.decodeResource(c.getResources(), R.drawable.spaceship);
            float scalingFactor = (screenH / 6.0f) / (float) spaceship.getHeight();
            BitmapCache.setScalingFactor(scalingFactor);
        }

        private void initSpaceship() {
            BitmapData ship_data = BitmapCache.getData(BitmapID.SPACESHIP, c);
            // initialize spaceship just off the screen in the middle
            spaceship = new Spaceship(-ship_data.getWidth(), screenH / 2 - ship_data.getHeight() / 2, c);
            spaceship.setBulletType(BulletType.LASER);//GameActivity.equipment.getEquippedBulletType()); // todo: get equipment
            spaceship.setRocketType(RocketType.ROCKET);//GameActivity.equipment.getEquippedRocketType());
            spaceship.setHP(30);
            spaceship.setDamage(30);
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                Log.d("GameView", "Screen Dimensions set to " + screenW + "," + screenH);
                background = new Background(screenW, screenH);
                initImgCache();
                initSpaceship();
                map = new Map(c, screenW, screenH);
                healthBar = new HealthBar(c, screenW, screenH, 30, 30);
                scoreDisplay = new ScoreDisplay(c, 0);
                // restore game state if flag is set
                if (restoreGameState != null) {
                    //restoreGameState(restoreGameState);
                    restoreGameState = null;
                }
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

    public void saveGameState() {
        /*GameSave save = new GameSave(c);
        save.saveAliens(aliens);
        save.saveSpaceship(spaceship);
        save.saveBullets(ssProjectiles);
        save.saveAlienBullets(alienProjectiles);
        save.saveCoins(coins);
        save.saveObstacles(obstacles);
        Log.d("GameView.java", "Saved a total of " + (aliens.size() + obstacles.size() +
                coins.size() + ssProjectiles.size() + alienProjectiles.size()) + " sprites");*/
    }

    public void flagRestoreGameState(String saveName) {
        //this.restoreGameState = saveName;
    }

    private void restoreGameState(String saveName) {
        /*Log.d("GameView.java", "Restoring game state " + System.currentTimeMillis());
        GameSave load = new GameSave(c, saveName);
        aliens = load.loadAliens(); // todo: nullpointers
        Log.d("GameView", Arrays.toString(aliens.toArray()));
        obstacles = load.loadObstacles();
        Log.d("GameView", Arrays.toString(obstacles.toArray()));
        coins = load.loadCoins();
        Log.d("GameView", Arrays.toString(coins.toArray()));
        ssProjectiles = load.loadBullets();
        Log.d("GameView", Arrays.toString(ssProjectiles.toArray()));
        alienProjectiles = load.loadAlienBullets();
        Log.d("GameView", Arrays.toString(alienProjectiles.toArray()));
        spaceship = load.loadSpaceship();
        Log.d("GameView", spaceship.toString());
        if (obstacles == null) {
            Log.d("GameView", "Obstacles are null");
        }
        Log.d("GameView.java", "Finished restoring game state " + System.currentTimeMillis());
        Log.d("GameView.java", "Restored a total of " + (aliens.size() + obstacles.size() +
                coins.size() + ssProjectiles.size() + alienProjectiles.size()) + " sprites");*/
    }

    public void clearGameState() {
        GameSave clear = new GameSave(c);
        clear.delete();
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
}
