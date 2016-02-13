package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.*;

/**
 * Auto-generation of sprites
 */
public class Map {

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    // tile id's
    private static final int EMPTY = 0; // no obstacle
    private static final int OBSTACLE = 1; // basic obstacle
    private static final int OBSTACLE_INVIS = 2; // basic obstacle collision = false
    private static final int COIN = 3; // coin tile
    private static final int ALIEN_LVL1 = 4; // level 1 alien
    private static final int ALIEN_LVL2 = 5; // level 2 alien
    private static final int ALIEN_LVL3 = 6; // level 3 alien

    private Hashtable<String, Bitmap> resources;

    // used to refer to resources in Hashtable
    public static final String spaceshipSprite = "spaceshipSprite";
    public static final String spaceshipMovingSpriteSheet = "spaceshipMovingSpritesheet";
    public static final String spaceshipFireRocketSpriteSheet = "spaceshipFireRocketSpriteSheet";
    public static final String spaceshipExplodeSpriteSheet = "spaceshipExplodeSpriteSheet";
    public static final String rocketSprite = "rocketSprite";
    public static final String spaceshipBulletSprite = "spaceshipBulletSprite";
    public static final String obstacleSprite = "obstacleSprite";
    public static final String coinSprite = "coinSprite";
    public static final String coinSpinSpriteSheet = "coinSpinSpriteSheet";
    public static final String coinCollectedSpriteSheet = "coinCollectedSpriteSheet";
    public static final String alien1Sprite = "alien1Sprite";
    public static final String alienExplodeSpriteSheet = "alienExplodeSpriteSheet";
    public static final String alienBulletSprite = "alienBulletSprite";

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
    // projectiles on screen
    private List<Sprite> projectiles = new ArrayList<>();

    // spaceship
    private Spaceship spaceship;
    private boolean shooting = false;

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

    // length of coin trails
    private static final int coinTrailLength = 18;
    // number of coins remaining in current trail
    private int coinsLeft;
    // whether to continue a coin trail in the next chunk
    private boolean continueCoinTrail = false;
    // index of row to be left clear in first column of next chunk
    // (used to guide coin trails between chunks and ensure map is
    // not impossible)
    private int nextRow;

    // dimensions of basic mapTiles
    private int tileWidth; // todo: what about bigger/smaller sprites?
    private int tileHeight;

    // used for generating random numbers
    private static Random random = new Random();

    public Spaceship getSpaceship() { return spaceship; }
    public void setShooting(boolean shooting) { this.shooting = shooting; }
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
    public Map(int screenW, int screenH, Hashtable<String, Bitmap> resources) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.resources = resources;
        tileWidth = this.screenH / rows;
        tileHeight = this.screenH / rows;
        map = new byte[1][screenW / tileWidth];
        nextRow = random.nextInt(6);
        initResources();
    }

    private void initResources() {
        // calculate scaling factor
        scalingFactor = (screenH / 6.0f) / (float) resources.get(spaceshipSprite).getHeight();
        // scale graphics resources
        for (String key : resources.keySet()) {
            // scale so textures remain square and are scaled using height
            resources.put(key, Bitmap.createScaledBitmap(resources.get(key),
                    (int) (resources.get(key).getWidth() * scalingFactor),
                    (int) (resources.get(key).getHeight() * scalingFactor), true));
        }
        spaceship = new Spaceship(resources.get(spaceshipSprite), -resources.get(spaceshipSprite).getWidth(),
                screenH / 2 - resources.get(spaceshipSprite).getHeight() / 2);
        spaceship.injectResources(resources.get(spaceshipMovingSpriteSheet),
                resources.get(spaceshipFireRocketSpriteSheet), resources.get(spaceshipExplodeSpriteSheet),
                resources.get(rocketSprite), resources.get(spaceshipBulletSprite));
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
        scrollSpeed = updateScrollSpeed(); // todo: figure out how to update scrollspeed without letting sprites become disjointed
        this.x += screenW * scrollSpeed;

        // take care of map rendering
        if (getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty sprites in the current column at the edge of the screen
                if (map[i][mapTileCounter] != EMPTY) {
                    addTile(getMapTile(map[i][mapTileCounter], screenW + getWOffset(), i * tileHeight),
                            scrollSpeed, 0);
                }
            }
            mapTileCounter++;

            // generate more sprites
            if (mapTileCounter == map[0].length) {
                map = generateTiles(difficulty, rows);
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
            case OBSTACLE:
                return new Obstacle(resources.get(obstacleSprite), x, y);
            case OBSTACLE_INVIS:
                Sprite tile = new Obstacle(resources.get(obstacleSprite), x, y);
                tile.setCollides(false);
                return tile;
            case COIN:
                return new Coin(resources.get(coinSprite), resources.get(coinSpinSpriteSheet), resources.get(coinCollectedSpriteSheet),
                        x, y, this);
            case ALIEN_LVL1:
                Alien1 alien_1 = new Alien1(resources.get(alien1Sprite), x, y, this);
                alien_1.injectResources(resources.get(alienBulletSprite), resources.get(alienExplodeSpriteSheet));
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
        score += difficulty / 2;
        difficulty += 0.01f;
        updateMap();
        updateSpaceship();
        projectiles.addAll(spaceship.getAndClearProjectiles());
        getAlienBullets(projectiles, sprites);
        for(Sprite sprite : sprites) {
            checkCollisions(sprite, projectiles);
        }
        updateSprites(sprites);
        updateSprites(projectiles);
    }

    private void updateSpaceship() {
        spaceship.move();
        // for when spaceship first comes on to screen
        if (spaceship.getX() < screenW / 4) {
            spaceship.setControllable(false);
            spaceship.setSpeedX(0.003f);
        } else if (spaceship.getX() > screenW / 4) {
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
        checkCollisions(spaceship, projectiles);
        checkCollisions(spaceship, sprites);
        spaceship.setShooting(shooting);
        spaceship.updateActions();
        spaceship.updateAnimations();
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
            if(s instanceof Alien) {
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
        for(Sprite s : sprites) {
            s.draw(canvas);
        }
        for(Sprite s : projectiles) {
            s.draw(canvas);
        }
        spaceship.draw(canvas);
    }

    // generates a map of sprites based on difficulty and number of rows
    // in screen vertically.
    // difficulty determines probability of certain obstacles, coin
    // trails, and todo powerups
    private byte[][] generateTiles(double difficulty, int rows) {
        byte[][] generated;
        if (getP(getPTile(difficulty))) {
            if (getP(getPTunnel(difficulty))) {
                generated = generateTunnel(rows);
            } else {
                generated = generateObstacles(rows);
            }
        } else {
            //if(getP(0.5)) {
            if (getP(getPAlienSwarm(rows))) {
                generated = generateAlienSwarm(rows);
            } else {
                generated = generateAlien(rows);
            }
            //} else {
            //    map = generateAsteroid();
            //}
        }
        // 15% chance of generating coin trail or 100%
        // chance if a coin trail is in the process of being
        // moved on the next chunk
        if (continueCoinTrail || getP(0.15)) { // todo: getPCoinTrail
            generateCoins(generated);
        }
        return generated;
    }

    // generates map cluster of simple obstacle
    private byte[][] generateObstacles(int rows) {
        int size = 10 + random.nextInt(5), row = 0;
        byte[][] generated = new byte[rows][size + 2];
        for (int i = 0; i < size; i++) {
            if (getP(0.2f)) {
                row = genRandExcl(rows, nextRow);
                generated[row][i] = OBSTACLE;
                if (getP(0.5) && i + 1 < size) {
                    generated[row][i + 1] = OBSTACLE;
                }
                if (getP(0.3) && row + 1 < rows) {
                    generated[row + 1][i] = OBSTACLE;
                }
                if (getP(0.2) && row > 0) {
                    generated[row - 1][i] = OBSTACLE; // todo: this could block nextRow
                }
                nextRow = getNextRow(rows, row);
            }
        }
        return generated;
    }

    // generates tunnel
    private byte[][] generateTunnel(int rows) {
        int tunnel_length = 15 + random.nextInt(10);
        byte[][] generated = new byte[rows][tunnel_length + 3];
        int row = nextRow;
        float change_path = 0.0f;
        // generate first column
        for (int i = 0; i < rows; i++) {
            if (i != row)
                generated[i][0] = OBSTACLE;
        }
        for (int i = 1; i < tunnel_length; i++) {
            if (getP(change_path) && i < tunnel_length - 1) {
                change_path = -0.1f;
                int direction;
                if (getP(0.5f)) {
                    if (row < generated.length - 1)
                        direction = 1;
                    else
                        direction = -1;
                } else {
                    if (row > 0)
                        direction = -1;
                    else
                        direction = 1;
                }
                for (int j = 0; j < rows; j++) {
                    if (j != row && j != row + direction) {
                        generated[j][i] = OBSTACLE;
                        generated[j][i + 1] = OBSTACLE;
                    }
                }
                i++;
                row += direction;
            } else {
                for (int j = 0; j < rows; j++) {
                    if (j < row - 1 || j > row + 1) {
                        generated[j][i] = OBSTACLE_INVIS;
                    } else if (j != row) {
                        generated[j][i] = OBSTACLE;
                    }
                }
                change_path += 0.05f;
            }
        }
        nextRow = getNextRow(rows, row);
        return generated;
    }

    private byte[][] generateAlien(int rows) {
        int size = 4 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        generated[random.nextInt(6)][size - 1] = ALIEN_LVL1;
        return generated;
    }

    private byte[][] generateAlienSwarm(int rows) {
        int num_aliens = 2 + random.nextInt(3);
        int size = num_aliens * 8 + 1;
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < num_aliens; i++) {
            generated[random.nextInt(6)][8 * (i + 1)] = ALIEN_LVL1;
        }
        return generated;
    }

    // generates a coin trail on map
    private void generateCoins(byte[][] generated) {
        int col, row, end_col;
        if (continueCoinTrail) {
            col = 0;
            end_col = coinsLeft;
            row = nextRow;
        } else { // start coin trail somewhere 1/4 to 3/4 of way through chunk
            col = generated[0].length / 4 + random.nextInt(generated[0].length / 2);
            end_col = col + coinTrailLength;
            coinsLeft = coinTrailLength;
            /* establish empty row to place first coin. trail_distance is the
            length a trail can go without having to change direction. Longer
            trail_distance is preferable */
            int best_row = random.nextInt(6), max_distance = 1;
            for (int i = 0; i < generated.length; i++) {
                int trail_distance = 0, j = 0;
                //int trail_distance = 1 - 2 * (Math.abs(3 - i)), j = 0; // middle columns are favored
                while (col + j < generated[0].length && generated[i][col + j] == EMPTY) {
                    trail_distance++;
                    if (trail_distance > max_distance) {
                        max_distance = trail_distance;
                        best_row = i;
                    }
                    j++;
                }
            }
            row = best_row;
        }
        for (int i = col; i < generated[0].length && i < end_col && coinsLeft > 0; i++, coinsLeft--) {
            if (generated[row][i] == EMPTY) {
                generated[row][i] = COIN;
            } else { // search for nearby empty sprites
                if(row < generated.length - 1 && generated[row + 1][i] == EMPTY) {
                    row += 1;
                    generated[row][i] = COIN;
                    if(i > 0) {
                        generated[row][i - 1] = COIN;
                    }
                } else if(row > 0 && generated[row - 1][i] == EMPTY) {
                    row -= 1;
                    generated[row][i] = COIN;
                    if(i > 0) {
                        generated[row][i - 1] = COIN;
                    }
                }
            }
        }
        // coin trail over - reset counter
        if (coinsLeft == 0) {
            continueCoinTrail = false;
        } else {
            continueCoinTrail = true;
        }
    }

    // gives a possible value for nextRow, a row to keep
    // free of obstacles in the next generated chunk
    private int getNextRow(int rows, int lastRow) {
        int row_change = random.nextInt(2) + 1;
        int result = lastRow + (getP(0.5) ? +row_change : -row_change);
        if (result > rows - 1) {
            return rows - 1;
        } else if (result < 0) {
            return 0;
        }
        return result;
    }

    // generates random number using random.nextInt(range)
    // that is not equal to exclusive
    private int genRandExcl(int range, int exclusive) {
        int rand;
        do {
            rand = random.nextInt(range);
        } while (rand == exclusive);
        return rand;
    }

    // give probability of an event occurring
    // uses random numbers and will return if event should
    // occur or not
    private static boolean getP(double probability) {
        return random.nextInt(100) + 1 <= probability * 100;
    }

    // calculates and returns probability of a tile-based obstacle
    private static double getPTile(double difficulty) {
        if (110 - difficulty >= 50) {
            return (110 - difficulty) / 100;
        } else {
            return 0.5;
        }
    }

    private static double getPTunnel(double difficulty) {
        if (-10 + difficulty < 50) {
            return (-10 + difficulty) / 100;
        } else {
            return 0.5;
        }
    }

    // calculates and returns probability of aliens appearing in a swarm
    private static double getPAlienSwarm(double difficulty) {
        return (-30 + difficulty) / 100;
    }

    // prints map in a 2-d array
    private String mapToString(byte[][] map) {
        String result = "";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                result += map[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }
}
