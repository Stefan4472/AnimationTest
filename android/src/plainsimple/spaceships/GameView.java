package plainsimple.spaceships;

import android.content.Context;
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
import android.widget.ImageButton;

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
    private boolean paused = false; // todo: non-static?
    // whether sound on or off
    private boolean muted = false;
    // space background (implements parallax scrolling)
    private Background background;
    // generates terrain and sprites on screen
    private Map map;
    // num pixels scrolled
    public static float scrollCounter = 0; // todo: make non-static

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
                    scrollCounter += map.getSpaceship().getSpeedX();
                    if (scrollCounter > 0.04) { // scroll background slowly
                        background.scroll(1);
                        scrollCounter = 0;
                    }
                }
                map.draw(canvas);
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
                            // initialize background and map
                            initBackground();
                            initMap();
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
            Hashtable<String, Bitmap> resources = new Hashtable<>();
            resources.put(Map.spaceshipSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_sprite));
            resources.put(Map.spaceshipMovingSpriteSheet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_moving_spritesheet_diff));
            resources.put(Map.spaceshipExplodeSpriteSheet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_exploding_spritesheet_diff));
            resources.put(Map.spaceshipFireRocketSpriteSheet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_firing_spritesheet_diff));
            resources.put(Map.rocketSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.rocket_sprite));
            resources.put(Map.spaceshipBulletSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.bullet_sprite));
            resources.put(Map.obstacleSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.obstacle_sprite));
            resources.put(Map.coinSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_sprite));
            resources.put(Map.alien1Sprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_sprite));
            resources.put(Map.alienBulletSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_bullet));
            map = new Map(screenW, screenH, resources);
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
