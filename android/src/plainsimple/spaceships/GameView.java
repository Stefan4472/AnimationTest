package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Hashtable;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

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
    private float scaleW;
    private float scaleH;

    // num pixels scrolled
    public static int scrollCounter = 0;
    // whether game is paused currently
    public static boolean paused;
    // space background (implements parallax scrolling)
    private Background background;
    // generates terrain and sprites on screen
    private Map map;
    // difficulty level, incremented every frame
    public static double difficulty = 0.0f;
    // score in current run
    public static int score = 0;

    public Map getMap() {
        return map;
    }
    public static void incrementScrollCounter(int increment) {
        scrollCounter += increment;
    }

    public GameView(Context context, AttributeSet attributes) {
        super(context, attributes);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

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
                map.setShooting(shooting);
                map.update();
                map.draw(canvas);
                scrollCounter += map.getSpaceship().getSpeedX();
                if (scrollCounter > 30) { // scroll background slowly
                    background.scroll(1);
                    scrollCounter = 0;
                }
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
                        if (!onTitle && !shooting) {
                            shooting = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP: // handle user clicking something
                        if (onTitle) { // change to game screen. Load resources
                            backgroundImg = BitmapFactory.decodeResource(myContext.getResources(),
                                    R.drawable.game_background);
                            myContext.getResources();
                            // establish scale factors based on original background image's dimensions
                            scaleW = screenW / (float) backgroundOrigW;
                            scaleH = screenH / (float) backgroundOrigH;
                            Log.d("GameView Class", "Screen " + screenW + "," + screenH + " orig screen " + backgroundOrigW + "," + backgroundOrigH + " scaling " + scaleW + "," + scaleH);
                            // initialize background and map
                            initBackground();
                            initMap();
                            onTitle = false;
                        } else {
                            shooting = false;
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

        private void initMap() {
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
            map = new Map(screenW, screenH, scaleW, scaleH, resources);
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenW = width;
                screenH = height;
                //backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
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
}
