package plainsimple.spaceships;

import android.content.Context;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    // stores graphics with R.drawable ID as key and cached Bitmap as object
    private HashMap<Integer, Bitmap> imageCache;

    // used to play short sounds
    private SoundPool soundPool;
    private int[] soundIDs;

    // whether game is paused currently
    private boolean paused = false;
    // whether sound on or off
    private boolean muted = false;
    // difficulty level, incremented every frame
    private double difficulty = 0.0f;
    // score in current run
    private int score = 0;
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
    // default speed of sprites scrolling across the map
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
        // set up thread
        thread = new GameViewThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });
        // set up SoundPool
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new int[1];
        soundIDs[0] = soundPool.load(context, R.raw.snap, 1);
        // set up graphics HashMap

        setFocusable(true);
    }

    public GameViewThread getThread() {
        return thread;
    }

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
                            soundPool.play(soundIDs[0], 1, 1, 1, 0, 1.0f);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP: // handle user clicking something
                        if (onTitle) { // change to game screen. Load resources
                            myContext.getResources();
                            background = new Background(screenW, screenH);
                            initImgCache();
                            initSpaceship();
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

        // loads in sprites, sends ID's to the proper classes, and scales them
        private void initImgCache() {
            imageCache = new HashMap<Integer, Bitmap>();
            imageCache.put(R.drawable.spaceship_sprite, BitmapFactory.decodeResource(myContext.getResources(), R.drawable.spaceship_sprite));
            imageCache.put(R.drawable.spaceship_moving_spritesheet_diff, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_moving_spritesheet_diff));
            imageCache.put(R.drawable.spaceship_exploding_spritesheet_diff, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_exploding_spritesheet_diff));
            imageCache.put(R.drawable.spaceship_firing_spritesheet_diff, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.spaceship_firing_spritesheet_diff));

            // get current rocket type equipped and decode corresponding sprite
            int rocket_resource = SpaceShipsActivity.preferences.getInt(myContext.getString(R.string.equipped_rocket),
                    R.drawable.rocket_sprite);
            imageCache.put(rocket_resource, BitmapFactory.decodeResource(myContext.getResources(),
                    rocket_resource));

            SpaceShipsActivity.preferences.edit().putInt(myContext.getString(R.string.equipped_bullet),
                    R.drawable.laser_bullet_sprite).commit();
            // get current bullet type equipped and decode corresponding sprite
            int bullet_resource = SpaceShipsActivity.preferences.getInt(myContext.getString(R.string.equipped_bullet),
                    R.drawable.laser_bullet_sprite);
            imageCache.put(bullet_resource, BitmapFactory.decodeResource(myContext.getResources(),
                    bullet_resource));

            imageCache.put(R.drawable.obstacle_sprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.obstacle_sprite));
            imageCache.put(R.drawable.coin_sprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_sprite));
            imageCache.put(R.drawable.coin_spinning_spritesheet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_spinning_spritesheet));
            imageCache.put(R.drawable.coin_collected_spritesheet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.coin_collected_spritesheet));
            imageCache.put(R.drawable.alien_sprite, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_sprite));
            imageCache.put(R.drawable.spaceship_exploding_spritesheet_diff, BitmapFactory.decodeResource(myContext.getResources(), // todo: use different animation (not Spaceship one)
                    R.drawable.spaceship_exploding_spritesheet_diff));
            imageCache.put(R.drawable.alien_bullet, BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.alien_bullet));
            // calculate scaling factor using spaceship_sprite height as a baseline
            float scalingFactor = (screenH / 6.0f) / (float) imageCache.get(R.drawable.spaceship_sprite).getHeight();
            // scale all graphics resources in imageCache. Want textures to remain square. Scale using using height
            for (int key : imageCache.keySet()) {
                Bitmap to_scale = imageCache.get(key);
                imageCache.put(key, Bitmap.createScaledBitmap(to_scale,
                        (int) (to_scale.getWidth() * scalingFactor),
                        (int) (to_scale.getHeight() * scalingFactor), true));
            }
        }

        private void initSpaceship() {
            spaceship = new Spaceship(spaceshipSprite, -spaceshipSprite.getWidth(),
                    screenH / 2 - spaceshipSprite.getHeight() / 2);
            spaceship.injectResources(spaceshipMoveSheet, spaceshipFireRocketSheet, spaceshipExplodeSheet,
                    rocketSprite, spaceshipBulletSprite);
            map.getSpaceship().setBullets(true, bullet_resource);
            map.getSpaceship().setRockets(true, rocket_resource);
            map.getSpaceship().setHP(30);
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
                    map = tileGenerator.generateTiles(difficulty);
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
            scrollSpeed = (float) (-0.0025f - difficulty / 2500.0);
            if (scrollSpeed < -0.025) { // scroll speed ceiling
                scrollSpeed = -0.025f;
            }
        }

        // returns sprite initialized to coordinates (x,y) given tileID
        private Sprite getMapTile(int tileID, float x, float y) throws IndexOutOfBoundsException {
            switch (tileID) {
                case TileGenerator.OBSTACLE:
                    return new Obstacle(obstacleSprite, x, y);
                case TileGenerator.OBSTACLE_INVIS:
                    Sprite tile = new Obstacle(obstacleSprite, x, y);
                    tile.setCollides(false);
                    return tile;
                case TileGenerator.COIN:
                    return new Coin(coinSprite, coinSpinSheet, coinDisappearSheet, x, y);
                case TileGenerator.ALIEN_LVL1:
                    Alien1 alien_1 = new Alien1(alien1Sprite, x, y, difficulty, spaceship);
                    alien_1.injectResources(alienBulletSprite, alienExplodeSheet);
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

        // adds any new sprites and generates a new set of sprites if needed
        public void update() {
            //score += difficulty / 2; // todo: increment score based on difficulty
            difficulty += 0.01f;
            updateMap();
            updateSpaceship(); // todo: does scoring work properly?
            getAlienBullets(alienProjectiles, sprites);
            // check collisions between sprites and spaceship projectiles
            for(Sprite sprite : sprites) {
                checkCollisions(sprite, ssProjectiles);
            }
            checkCollisions(spaceship, sprites);
            checkCollisions(spaceship, alienProjectiles);
            score += spaceship.getAndClearScore();
            updateSprites(sprites);
            updateSprites(ssProjectiles);
            updateSprites(alienProjectiles);
            spaceship.updateAnimations();
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

        private void updateSprites(List<Sprite> toUpdate) {
            Iterator<Sprite> i = toUpdate.iterator(); // todo: get all sprites together, collisions, etc.
            while(i.hasNext()) {
                Sprite s = i.next();
                s.move();
                if(s.isInBounds() && s.isVisible()) {
                    s.updateActions();
                    s.updateSpeeds(); // todo: hit detection
                    s.updateAnimations();
                } else {
                    i.remove();
                }
            }
        }

        public void updateGyro(float yValue) {
            spaceship.setTiltChange(yValue);
            spaceship.updateSpeeds();
        }

        // goes through sprites, and for each alien uses getAndClearProjectiles,
        // adds those projectiles to projectiles list
        private void getAlienBullets(List<Sprite> projectiles, List<Sprite> sprites) {
            for(Sprite s : sprites) {
                if (s instanceof Alien) {
                    projectiles.addAll(((Alien) s).getAndClearProjectiles());
                }
            }
        }

        // checks sprite against each sprite in list
        // calls handleCollision method if a collision is detected
        private void checkCollisions(Sprite sprite, List<Sprite> toCheck) {
            for(Sprite s : toCheck) {
                if(sprite.collidesWith(s)) {
                    sprite.handleCollision(s);
                    s.handleCollision(sprite);
                }
            }
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
