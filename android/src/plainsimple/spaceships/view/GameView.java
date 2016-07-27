package plainsimple.spaceships.view;

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
import plainsimple.spaceships.activity.GameActivity;
import plainsimple.spaceships.R;
import plainsimple.spaceships.activity.MainActivity;
import plainsimple.spaceships.sprites.*;
import plainsimple.spaceships.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private Context c;
    private SurfaceHolder mySurfaceHolder;
    // todo: make non-static
    public static int screenW;
    public static int screenH;
    private boolean running = false;
    private boolean onTitle = true;
    private GameViewThread thread;
    // todo: AnimationCache using BitmapResource enums. Store num frames per animation in r/values/integers
    // stores initialized SpriteAnimations with R.drawable ID of spritesheet as key
    private HashMap<Integer, SpriteAnimation> animations;
    // todo: paused and muted should be public static GameActivity fields
    // dimensions of basic mapTiles
    private int tileWidth; // todo: what about bigger/smaller sprites?
    private int tileHeight;
    // used to render score on screen
    private ScoreDisplay scoreDisplay;
    // space background (implements parallax scrolling)
    private Background background;
    // grid of tile ID's instructing which sprites to initialize on screen
    private byte[][] map;
    // used to generate tile-based terrain
    private TileGenerator tileGenerator;
    // number of rows of sprites that fit on screen
    private static final int ROWS = 6;
    // number of tiles elapsed since last map was generated
    private int mapTileCounter = 0;
    // keeps track of tile spaceship was on last time map was updated
    private long lastTile = 0;
    // coordinates of upper-left of "window" being shown
    private long x = 0;
    // default speed of sprites scrolling across the map (must be negative!)
    private float scrollSpeed = -0.0025f;
    // active generated non-projectile sprites
    private List<Sprite> sprites = new ArrayList<>();
    // active projectiles on screen fired by spaceship
    private List<Sprite> ssProjectiles = new ArrayList<>();
    // active projectiles on screen fired by aliens
    private List<Sprite> alienProjectiles = new ArrayList<>();
    // spaceship
    private Spaceship spaceship;
    // relative speed of background scrolling to foreground scrolling
    private static final float SCROLL_SPEED_CONST = 0.4f;

    private SensorManager gSensorManager;
    private Sensor gyroscope;
    private long lastSample = 0;
    private final static int sampleRateMS = 20;

    public void setFiringMode(int firingMode) { spaceship.setFiringMode(firingMode);}

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
        // set up thread
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
            try {
                background.draw(canvas);
                if(!GameActivity.getPaused()) {
                    update();
                    background.scroll((int) (-scrollSpeed * screenW * SCROLL_SPEED_CONST));
                }
                for (Sprite s : sprites) { // todo: list of non-colliding sprites?
                    drawSprite(s, canvas);
                }
                for (Sprite s : ssProjectiles) {
                    drawSprite(s, canvas);
                }
                for (Sprite s : alienProjectiles) {
                    drawSprite(s, canvas);
                }
                drawSprite(spaceship, canvas);
                scoreDisplay.setScore(GameActivity.getScore());
                scoreDisplay.draw(canvas);
            } catch (Exception e) {
                System.out.print("Error drawing canvas");
            }
        }

        // draws sprite onto canvas using sprite drawing params and imageCache
        private void drawSprite(Sprite sprite, Canvas canvas) {
            ArrayList<DrawParams> draw_params = sprite.getDrawParams();
            for (DrawParams img_params : draw_params) {
                ImageUtil.drawBitmap(canvas, BitmapCache.getImage(img_params.getBitmapID(), c), img_params);
            }
        }

        // updates all game logic
        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            //score += difficulty / 2; // todo: increment score based on difficulty
            GameActivity.incrementDifficulty(0.01f);
            updateMap();
            updateSpaceship(); // todo: does scoring work properly?
            GameEngineUtil.getAlienBullets(alienProjectiles, sprites);
            // check collisions between sprites and spaceship projectiles
            for(Sprite sprite : sprites) {
                GameEngineUtil.checkCollisions(sprite, ssProjectiles);
            }
            GameEngineUtil.checkCollisions(spaceship, sprites);
            GameEngineUtil.checkCollisions(spaceship, alienProjectiles);
            GameEngineUtil.updateSprites(sprites);
            GameEngineUtil.updateSprites(ssProjectiles);
            GameEngineUtil.updateSprites(alienProjectiles);
            spaceship.updateAnimations();
        }

        private void updateMap() {
            x += screenW * scrollSpeed;

            // take care of map rendering
            if (getWTile() != lastTile) {
                for (int i = 0; i < map.length; i++) {
                    // add any non-empty sprites in the current column at the edge of the screen
                    if (map[i][mapTileCounter] != TileGenerator.EMPTY) {
                        addTile(getMapTile(map[i][mapTileCounter], screenW + getWOffset(), i * tileHeight),
                                scrollSpeed, 0);
                    }
                }
                mapTileCounter++;

                // generate more sprites
                if (mapTileCounter == map[0].length) {
                    //map = tileGenerator.generateTiles(difficulty);
                    map = tileGenerator.generateDebugTiles();
                    updateScrollSpeed();
                    mapTileCounter = 0;
                }
                lastTile = getWTile();
            }
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

        private void updateSpaceship() {
            spaceship.move();
            spaceship.updateActions();
            ssProjectiles.addAll(spaceship.getAndClearProjectiles());
            // for when spaceship first comes on to screen
            if (spaceship.getX() < screenW / 4) {
                spaceship.setControllable(false);
                spaceship.setSpeedX(0.003f);
            } else {
                spaceship.setX(screenW / 4);
                spaceship.setSpeedX(0.0f);
                spaceship.setControllable(true);
            }
            // prevent spaceship from going off-screen
            if (spaceship.getY() < 0) {
                spaceship.setY(0);
            } else if (spaceship.getY() > screenH - spaceship.getHeight()) {
                spaceship.setY(screenH - spaceship.getHeight());
            }
        }

        public void updateGyro(float yValue) {
            spaceship.setTiltChange(yValue);
            spaceship.updateSpeeds();
        }

        // handle user touching screen
        boolean doTouchEvent(MotionEvent motionEvent) {
            synchronized (mySurfaceHolder) {
                int event_action = motionEvent.getAction();
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                switch (event_action) {
                    case MotionEvent.ACTION_DOWN:
                        if (!onTitle) {
                            spaceship.setShooting(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP: // handle user clicking something
                        if (onTitle) { // change to game screen. Load resources
                            c.getResources();
                            background = new Background(screenW, screenH);
                            initImgCache();
                            initAnimations();
                            initSpaceship();
                            initScoreDisplay();
                            tileWidth = screenH / ROWS;
                            tileHeight = screenH / ROWS;
                            map = new byte[1][screenW / tileWidth];
                            tileGenerator = new TileGenerator(ROWS);
                            onTitle = false;
                        } else {
                            spaceship.setShooting(false);
                        }
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

        private void initSpaceship() { // todo: clean up
            spaceship = new Spaceship(BitmapCache.getData(BitmapResource.SPACESHIP, c),
                    -BitmapCache.getData(BitmapResource.SPACESHIP, c).getWidth(),
                    screenH / 2 - BitmapCache.getData(BitmapResource.SPACESHIP, c).getHeight() / 2);
            int bullet_resource = MainActivity.preferences.getInt(c.getString(R.string.equipped_bullet),
                    R.drawable.laserbullet);
            int rocket_resource = MainActivity.preferences.getInt(c.getString(R.string.equipped_rocket),
                    R.drawable.rocket);
            spaceship.injectResources(animations.get(R.drawable.spaceship_move),
                    animations.get(R.drawable.spaceship_fire_rocket), animations.get(R.drawable.spaceship_explode),
                    BitmapCache.getData(BitmapResource.LASER_BULLET, c), BitmapCache.getData(BitmapResource.ROCKET, c));
            spaceship.setBullets(true, bullet_resource);
            spaceship.setRockets(true, rocket_resource);
            spaceship.setHP(30);
        }

        // initializes SpriteAnimations and stores them in animations HashMap
        private void initAnimations() {
            animations = new HashMap<>();
            animations.put(R.drawable.spaceship_move, new SpriteAnimation(BitmapCache.getData(BitmapResource.SPACESHIP_MOVE, c), BitmapCache.getData(BitmapResource.SPACESHIP, c).getWidth(), 5, true));
            animations.put(R.drawable.spaceship_fire_rocket, new SpriteAnimation(BitmapCache.getData(BitmapResource.SPACESHIP_FIRE, c), BitmapCache.getData(BitmapResource.SPACESHIP, c).getWidth(), 8, false));
            animations.put(R.drawable.spaceship_explode, new SpriteAnimation(BitmapCache.getData(BitmapResource.SPACESHIP_EXPLODE, c), BitmapCache.getData(BitmapResource.SPACESHIP, c).getWidth(), 5, false));
            animations.put(R.drawable.coin_spin, new SpriteAnimation(BitmapCache.getData(BitmapResource.COIN_SPIN, c), BitmapCache.getData(BitmapResource.COIN, c).getWidth(), 10, true));
            animations.put(R.drawable.coin_collect, new SpriteAnimation(BitmapCache.getData(BitmapResource.COIN_DISAPPEAR, c), BitmapCache.getData(BitmapResource.COIN, c).getWidth(), 5, false));
        }

        private void initScoreDisplay() {
            // todo: calculate pixels based on density-independent pixels
            // todo: make it look better
            scoreDisplay = new ScoreDisplay(10, 20);
            scoreDisplay.setStartXY(10, 10 + (int) scoreDisplay.getPaint().getTextSize());
        }

        // current horizontal tile
        private long getWTile() {
            return x / tileWidth;
        }

        // number of pixels from start of current tile
        private int getWOffset() {
            return (int) x % tileWidth;
        }

        // returns sprite initialized to coordinates (x,y) given tileID
        private Sprite getMapTile(int tileID, int x, int y) throws IndexOutOfBoundsException {
            switch (tileID) {
                case TileGenerator.OBSTACLE:
                    return new Obstacle(BitmapCache.getData(BitmapResource.OBSTACLE, c), x, y); // todo: use static ImageData?
                case TileGenerator.OBSTACLE_INVIS:
                    Sprite tile = new Obstacle(BitmapCache.getData(BitmapResource.OBSTACLE, c), x, y);
                    tile.setCollides(false);
                    return tile;
                case TileGenerator.COIN: // todo: cache SpriteAnimations
                    return new Coin(BitmapCache.getData(BitmapResource.COIN, c), animations.get(R.drawable.coin_spin), animations.get(R.drawable.coin_collect), x, y);
                case TileGenerator.ALIEN_LVL1:
                    Alien1 alien_1 = new Alien1(BitmapCache.getData(BitmapResource.ALIEN, c), x, y, spaceship);
                    alien_1.injectResources(BitmapCache.getData(BitmapResource.ALIEN_BULLET, c), animations.get(R.drawable.spaceship_explode));
                    return alien_1;
                default:
                    throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
            }
        }

        // sets specified fields and adds sprite to arraylist
        private void addTile(Sprite s, float speedX, float speedY) {
            s.setSpeedX(speedX);
            s.setSpeedY(speedY);
            sprites.add(s);
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
