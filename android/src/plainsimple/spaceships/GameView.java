package plainsimple.spaceships;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Space;

import java.util.Hashtable;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private Context myContext;
    private SurfaceHolder mySurfaceHolder;
    // todo: make non-static
    public static int screenW;
    public static int screenH;
    private boolean running = false;
    private boolean onTitle = true;
    private boolean shooting = false;
    private GameViewThread thread;

    // whether game is paused currently
    private boolean paused = false;
    // whether sound on or off
    private boolean muted = false;
    // used to render score on screen
    private ScoreDisplay scoreDisplay;
    // space background (implements parallax scrolling)
    private Background background;
    // generates terrain and sprites on screen
    private Map map;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;
    // points a coin is worth
    public static final int COIN_VALUE = 100;

    private SensorManager gSensorManager;
    private Sensor gyroscope;
    private long lastSample = 0;
    private final static int sampleRateMS = 20;

    public void setMuted(boolean muted) { this.muted = muted; }
    public boolean getMuted() { return muted; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public boolean getPaused() { return paused; }

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // get sensor manager, check if gyroscope, get gyroscope
        gSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (gSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyroscope = gSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            gSensorManager.registerListener(this, gyroscope, 1_000_000); // todo: test with SENSOR_DELAY_NORMAL? // todo: works with Level 9 API +
        } else {
            Log.d("GameView Class", "No Accelerometer");
        }
        thread = new GameViewThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });

        setFocusable(true);
    }

    public GameViewThread getThread() {
        return thread;
    }
    public Map getMap() { return map; }

    class GameViewThread extends Thread {

        public GameViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            try {
                background.draw(canvas);
                if(!paused) {
                    map.update();
                    background.scroll((int) (-map.getScrollSpeed() * screenW * SCROLL_SPEED_CONST));
                }
                map.draw(canvas);
                scoreDisplay.setScore(map.getScore());
                scoreDisplay.draw(canvas);
            } catch (Exception e) {
                System.out.print("Error drawing canvas");
            }
        }

        boolean doTouchEvent(MotionEvent motionEvent) {
            synchronized (mySurfaceHolder) {
                int event_action = motionEvent.getAction();
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                switch (event_action) {
                    case MotionEvent.ACTION_DOWN:
                        if (!onTitle) {
                            map.setShooting(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP: // handle user clicking something
                        if (onTitle) { // change to game screen. Load resources
                            myContext.getResources();
                            initBackground();
                            initMap();
                            initScoreDisplay();
                            onTitle = false;
                        } else {
                            map.setShooting(false);
                        }
                        break;
                }
            }
            return true;
        }

        private void initBackground() {
            Bitmap[] tiles = {
                BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space_tile)
            };
            // todo: clean up
            // calculate scaling factor
            float scalingFactor = (screenH / 6.0f) / (float) BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_sprite).getHeight();
            background = new Background(screenW, screenH, scalingFactor, tiles);
        }

        private void initMap() { // todo: need to know which resources to load based on what bullets/rockets equipped. Load only needed resources
            Bitmap spaceshipSprite = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.spaceship_sprite);
            Bitmap spaceshipMoveSheet = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_moving_spritesheet_diff);
            Bitmap spaceshipExplodeSheet = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_exploding_spritesheet_diff);
            Bitmap spaceshipFireRocketSheet = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_firing_spritesheet_diff);

            // get current rocket type equipped and decode corresponding sprite
            int rocket_resource = SpaceShipsActivity.preferences.getInt(myContext.getString(R.string.equipped_rocket),
                            R.drawable.rocket_sprite);
            Bitmap rocketSprite = BitmapFactory.decodeResource(myContext.getResources(),
                    rocket_resource);

            SpaceShipsActivity.preferences.edit().putInt(myContext.getString(R.string.equipped_bullet),
                    R.drawable.laser_bullet_sprite).commit();
            // get current bullet type equipped and decode corresponding sprite
            int bullet_resource = SpaceShipsActivity.preferences.getInt(myContext.getString(R.string.equipped_bullet),
                    R.drawable.laser_bullet_sprite);
            Bitmap spaceshipBulletSprite = BitmapFactory.decodeResource(myContext.getResources(),
                    bullet_resource);

            Bitmap obstacleSprite = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.obstacle_sprite);
            Bitmap coinSprite = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_sprite);
            Bitmap coinSpinSheet = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_spinning_spritesheet);
            Bitmap coinDisappearSheet = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_collected_spritesheet);
            Bitmap alien1Sprite = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_sprite);
            Bitmap alienExplodeSheet = BitmapFactory.decodeResource(myContext.getResources(), // todo: use different animation (not Spaceship one)
                    R.drawable.spaceship_exploding_spritesheet_diff);
            Bitmap alienBulletSprite = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_bullet);
            map = new Map(screenW, screenH, spaceshipSprite, spaceshipMoveSheet, spaceshipFireRocketSheet,
                    spaceshipExplodeSheet, rocketSprite, spaceshipBulletSprite, obstacleSprite,
                    coinSprite, coinSpinSheet, coinDisappearSheet, alien1Sprite, alienExplodeSheet,
                    alienBulletSprite);
            map.getSpaceship().setBullets(true, bullet_resource);
            map.getSpaceship().setRockets(true, rocket_resource);
        }

        private void initScoreDisplay() {
            // todo: calculate pixels based on density-independent pixels
            // todo: make it look better
            scoreDisplay = new ScoreDisplay(10, 20);
            scoreDisplay.setStartXY(10, 10 + (int) scoreDisplay.getPaint().getTextSize());
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { // todo: tilt depends on default device orientation (landscape/portrait)
        // restrict sample rate // todo: currently disabled (see Issue #10)
        /*if(lastSample + sampleRateMS <= System.currentTimeMillis()) {
            System.out.println(sensorEvent.values[1]);
            if(map != null) {
                // send y-value of gyro to map
                map.updateGyro(sensorEvent.values[1]);
            }
        }
        lastSample = System.currentTimeMillis();*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        gSensorManager.unregisterListener(this);
    }
}
