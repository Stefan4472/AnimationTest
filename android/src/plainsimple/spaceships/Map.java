package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.*;

/**
 * Auto-generation of sprites
 */
public class Map {

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    private TileGenerator tileGenerator;

    // used to refer to resources in Hashtable
    public static Bitmap spaceshipSprite;
    public static Bitmap spaceshipMoveSheet;
    public static Bitmap spaceshipFireRocketSheet;
    public static Bitmap spaceshipExplodeSheet;
    public static Bitmap rocketSprite;
    public static Bitmap spaceshipBulletSprite;
    public static Bitmap obstacleSprite;
    public static Bitmap coinSprite;
    public static Bitmap coinSpinSheet;
    public static Bitmap coinDisappearSheet;
    public static Bitmap alien1Sprite;
    public static Bitmap alienExplodeSheet;
    public static Bitmap alienBulletSprite;

    // number of rows of sprites that fit in map
    private static final int rows = 6;

    // number of sprites elapsed since last map was generated
    private int mapTileCounter = 0;

    // keeps track of tile spaceship was on last time map was updated
    private long lastTile = 0;

    // default speed of sprites scrolling across the map
    private float scrollSpeed = -0.0025f;

    // generated sprites
    private List<Sprite> sprites = new ArrayList<>();
    // projectiles on screen fired by spaceship
    private List<Sprite> ssProjectiles = new ArrayList<>();
    // projectiles on screen fired by aliens
    private List<Sprite> alienProjectiles = new ArrayList<>();

    // spaceship
    private Spaceship spaceship;

    // dimensions of screen display
    private int screenW;
    private int screenH;
    private float scalingFactor;

    // coordinates of upper-left of "window" being shown
    private long x = 0;
    // difficulty level, incremented every frame
    private double difficulty = 0.0f;
    // score in current run
    private int score = 0;

    // dimensions of basic mapTiles
    private int tileWidth; // todo: what about bigger/smaller sprites?
    private int tileHeight;

    public Spaceship getSpaceship() { return spaceship; }
    public void setShooting(boolean shooting) { spaceship.setShooting(shooting); }
    public double getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public void incrementScore(int increment) {
        score += increment;
    }
    public float getScrollSpeed() {
        return scrollSpeed;
    }

    /* screenW, screenH: dimensions of screen
     * scalingFactor: factor used to scale resources
     * resources: hashtable containing resources with proper keys */
    public Map(int screenW, int screenH, Bitmap spaceshipSprite, Bitmap spaceshipMoveSheet,
               Bitmap spaceshipFireRocketSheet, Bitmap spaceshipExplodeSheet,
               Bitmap rocketSprite, Bitmap spaceshipBulletSprite, Bitmap obstacleSprite,
               Bitmap coinSprite, Bitmap coinSpinSheet, Bitmap coinDisappearSheet,
               Bitmap alien1Sprite, Bitmap alienExplodeSheet, Bitmap alienBulletSprite) {
        this.screenW = screenW;
        this.screenH = screenH;
        tileWidth = this.screenH / rows;
        tileHeight = this.screenH / rows;
        map = new byte[1][screenW / tileWidth];
        tileGenerator = new TileGenerator(rows);
        this.spaceshipSprite = spaceshipSprite;
        this.spaceshipMoveSheet = spaceshipMoveSheet;
        this.spaceshipFireRocketSheet = spaceshipFireRocketSheet;
        this.spaceshipExplodeSheet = spaceshipExplodeSheet;
        this.rocketSprite = rocketSprite;
        this.spaceshipBulletSprite = spaceshipBulletSprite;
        this.obstacleSprite = obstacleSprite;
        this.coinSprite = coinSprite;
        this.coinSpinSheet = coinSpinSheet;
        this.coinDisappearSheet = coinDisappearSheet;
        this.alien1Sprite = alien1Sprite;
        this.alienExplodeSheet = alienExplodeSheet;
        this.alienBulletSprite = alienBulletSprite;
        initResources();
    }

    private void initResources() {
        // calculate scaling factor
        scalingFactor = (screenH / 6.0f) / (float) spaceshipSprite.getHeight();
        // scale graphics resources. Want textures to remain square. Scale using using height
        spaceshipSprite = Bitmap.createScaledBitmap(spaceshipSprite,
                    (int) (spaceshipSprite.getWidth() * scalingFactor),
                    (int) (spaceshipSprite.getHeight() * scalingFactor), true);
        spaceshipMoveSheet = Bitmap.createScaledBitmap(spaceshipMoveSheet,
                (int) (spaceshipMoveSheet.getWidth() * scalingFactor),
                (int) (spaceshipMoveSheet.getHeight() * scalingFactor), true);
        spaceshipFireRocketSheet = Bitmap.createScaledBitmap(spaceshipFireRocketSheet,
                (int) (spaceshipFireRocketSheet.getWidth() * scalingFactor),
                (int) (spaceshipFireRocketSheet.getHeight() * scalingFactor), true);
        spaceshipExplodeSheet = Bitmap.createScaledBitmap(spaceshipExplodeSheet,
                (int) (spaceshipExplodeSheet.getWidth() * scalingFactor),
                (int) (spaceshipExplodeSheet.getHeight() * scalingFactor), true);
        rocketSprite = Bitmap.createScaledBitmap(rocketSprite,
                (int) (rocketSprite.getWidth() * scalingFactor),
                (int) (rocketSprite.getHeight() * scalingFactor), true);
        spaceshipBulletSprite = Bitmap.createScaledBitmap(spaceshipBulletSprite,
                (int) (spaceshipBulletSprite.getWidth() * scalingFactor),
                (int) (spaceshipBulletSprite.getHeight() * scalingFactor), true);
        obstacleSprite = Bitmap.createScaledBitmap(obstacleSprite,
                (int) (obstacleSprite.getWidth() * scalingFactor),
                (int) (obstacleSprite.getHeight() * scalingFactor), true);
        coinSprite = Bitmap.createScaledBitmap(coinSprite,
                (int) (coinSprite.getWidth() * scalingFactor),
                (int) (coinSprite.getHeight() * scalingFactor), true);
        coinSpinSheet = Bitmap.createScaledBitmap(coinSpinSheet,
                (int) (coinSpinSheet.getWidth() * scalingFactor),
                (int) (coinSpinSheet.getHeight() * scalingFactor), true);
        coinDisappearSheet = Bitmap.createScaledBitmap(coinDisappearSheet,
                (int) (coinDisappearSheet.getWidth() * scalingFactor),
                (int) (coinDisappearSheet.getHeight() * scalingFactor), true);
        alien1Sprite = Bitmap.createScaledBitmap(alien1Sprite,
                (int) (alien1Sprite.getWidth() * scalingFactor),
                (int) (alien1Sprite.getHeight() * scalingFactor), true);
        alienExplodeSheet = Bitmap.createScaledBitmap(alienExplodeSheet,
                (int) (alienExplodeSheet.getWidth() * scalingFactor),
                (int) (alienExplodeSheet.getHeight() * scalingFactor), true);
        alienBulletSprite = Bitmap.createScaledBitmap(alienBulletSprite,
                (int) (alienBulletSprite.getWidth() * scalingFactor),
                (int) (alienBulletSprite.getHeight() * scalingFactor), true);


        spaceship = new Spaceship(spaceshipSprite, -spaceshipSprite.getWidth(),
                screenH / 2 - spaceshipSprite.getHeight() / 2);
        spaceship.injectResources(spaceshipMoveSheet, spaceshipFireRocketSheet, spaceshipExplodeSheet,
                rocketSprite, spaceshipBulletSprite);
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
        this.x += screenW * scrollSpeed;

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
                scrollSpeed = updateScrollSpeed();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    // calculates scrollspeed based on difficulty
    // difficulty starts at 0 and increases by 0.01/frame,
    // or 1 per second
    public float updateScrollSpeed() {
        scrollSpeed = (float) (-0.0025f - difficulty / 2500.0);
        if (scrollSpeed < -0.025) { // scroll speed ceiling
            scrollSpeed = -0.025f;
        }
        return scrollSpeed;
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

    // draws sprites on canvas
    public void draw(Canvas canvas) {
        for (Sprite s : sprites) {
            s.draw(canvas);
        }
        for (Sprite s : ssProjectiles) {
            s.draw(canvas);
        }
        for (Sprite s : alienProjectiles) {
            s.draw(canvas);
        }
        spaceship.draw(canvas);
    }
}
