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
    private Bitmap backgroundImg;
    public static int screenW;
    public static int screenH;
    private boolean running = false;
    private boolean onTitle = true;
    private boolean shooting = false;
    private GameViewThread thread;
    // original width and height of background
    private final int backgroundOrigW = 800;
    private final int backgroundOrigH = 600;
    private float scaleH;

    // whether game is paused currently
    public static boolean paused = false; // todo: non-static?
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

    class GameViewThread extends Thread {

        public GameViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
            backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.title_graphic);
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
                            // establish scale factors based on original background image's dimensions
                            scaleH = screenH / (float) backgroundOrigH;
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
                BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space1_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space2_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space3_tile),
                        BitmapFactory.decodeResource(myContext.getResources(), R.drawable.space4_tile)
            };
            background = new Background(screenW, screenH, scaleH, tiles);
        }

        private void initMap() { // todo: auto-scales based on device resolution... Problem?
            Hashtable<String, Bitmap> resources = new Hashtable<>();
            resources.put(Map.spaceshipSprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_sprite)); // todo: load and scale resources, init sprites
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
            Log.d("GameView Class", "Dimensions of sprite: " + resources.get(Map.spaceshipSprite).getWidth() + " * " + resources.get(Map.spaceshipSprite).getHeight());
            map = new Map(screenW, screenH, resources);
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                Log.d("GameView Class", "Screen = " + screenW + "*" + screenH);
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

    public void onPausePressed(ImageButton pauseButton) {
        if(paused) { // todo: causes activity to crash
            pauseButton.setBackgroundResource(R.drawable.pausebutton_pause);
        } else {
            pauseButton.setBackgroundResource(R.drawable.pausebutton_play);
        }
        paused = !paused;
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
