package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Auto-generation of sprites
 */
public class Map {

    private Context context;

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

    // resources
    private Bitmap spaceshipImg;
    private Bitmap spaceshipMovingSpriteSheet;
    private Bitmap spaceshipFireRocketSpriteSheet;
    private Bitmap spaceshipExplodeSpriteSheet;
    private Bitmap rocketBitmap;
    private Bitmap spaceshipBulletBitmap;
    private Bitmap obstacleBitmap;
    private Bitmap coinBitmap;
    private Bitmap alien1Bitmap;
    private Bitmap alienBulletBitmap;

    // number of rows of tiles that fit in map
    private int rows;

    // number of tiles elapsed since last map was generated
    private int mapTileCounter = 0;

    // keeps track of tile spaceship was on last time map was updated
    private long lastTile = 0;

    // default speed of tiles scrolling across the map
    private float scrollSpeed = -4.0f;

    // generated sprites
    private ArrayList<Sprite> tiles = new ArrayList<>();

    // spaceship
    private Spaceship spaceship;

    // dimensions of screen display
    private int screenW;
    private int screenH;
    private float scaleW;
    private float scaleH;

    // coordinates of upper-left of "window" being shown
    private long x = 0;

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
    private final int tileWidth = 50; // todo: what about bigger/smaller sprites?
    private final int tileHeight = 50;

    // used for generating random numbers
    private static Random random = new Random();

    public ArrayList<Sprite> getTiles() {
        return tiles;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }


    public ArrayList<Sprite> getProjectiles() { // todo: map.update method, map.draw(canvas) method
        return (ArrayList<Sprite>) tiles.stream()
                .filter(s -> s.getClass().equals(Alien.class))
                .map(s -> ((Alien) s).getProjectiles())
                .flatMap(b -> b.stream())
                .collect(Collectors.toList());
    }

    public Map(int screenW, int screenH, float scaleW, float scaleH, Context context) {
        this.context = context;
        this.screenW = screenW;
        this.screenH = screenH;
        this.scaleW = scaleW;
        this.scaleH = scaleH;
        rows = screenH / tileHeight;
        map = new byte[6][7];
        nextRow = random.nextInt(6);
        initResources();
    }

    private void initResources() {
        spaceshipImg = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.spaceship_sprite); // todo: load and scale resources, init sprites
        spaceshipImg = Bitmap.createScaledBitmap(spaceshipImg,
                (int) (spaceshipImg.getWidth() * scaleW), (int) (spaceshipImg.getHeight() * scaleH), true);
        spaceshipMovingSpriteSheet = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.spaceship_moving_spritesheet_diff);
        spaceshipMovingSpriteSheet= Bitmap.createScaledBitmap(spaceshipMovingSpriteSheet,
                (int) (spaceshipMovingSpriteSheet.getWidth() * scaleW),
                (int) (spaceshipMovingSpriteSheet.getHeight() * scaleH), true);
        spaceshipExplodeSpriteSheet = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.spaceship_exploding_spritesheet_diff);
        spaceshipExplodeSpriteSheet= Bitmap.createScaledBitmap(spaceshipExplodeSpriteSheet,
                (int) (spaceshipExplodeSpriteSheet.getWidth() * scaleW),
                (int) (spaceshipExplodeSpriteSheet.getHeight() * scaleH), true);
        spaceshipFireRocketSpriteSheet = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.spaceship_firing_spritesheet_diff);
        spaceshipFireRocketSpriteSheet = Bitmap.createScaledBitmap(spaceshipFireRocketSpriteSheet,
                (int) (spaceshipFireRocketSpriteSheet.getWidth() * scaleW),
                (int) (spaceshipFireRocketSpriteSheet.getHeight() * scaleH), true);
        rocketBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.rocket_sprite);
        rocketBitmap = Bitmap.createScaledBitmap(rocketBitmap,
                (int) (rocketBitmap.getWidth() * scaleW), (int) (rocketBitmap.getHeight() * scaleH), true);
        spaceshipBulletBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bullet_sprite);
        spaceshipBulletBitmap = Bitmap.createScaledBitmap(spaceshipBulletBitmap,
                (int) (spaceshipBulletBitmap.getWidth() * scaleW),
                (int) (spaceshipBulletBitmap.getHeight() * scaleH), true);
        spaceship = new Spaceship(spaceshipImg, -spaceshipImg.getWidth(), screenH / 2 - spaceshipImg.getHeight() / 2);
        spaceship.injectResources(spaceshipMovingSpriteSheet, spaceshipFireRocketSpriteSheet,
                spaceshipExplodeSpriteSheet, rocketBitmap, spaceshipBulletBitmap);
        spaceship.setBullets(true, Bullet.BULLET_LASER, 100);
        spaceship.setRockets(true, Rocket.ROCKET, 420);

        obstacleBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.obstacle_tile);
        obstacleBitmap = Bitmap.createScaledBitmap(obstacleBitmap, (int) (obstacleBitmap.getWidth() * scaleW),
                (int) (obstacleBitmap.getHeight() * scaleH), true);

        coinBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.coin_tile);
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, (int) (coinBitmap.getWidth() * scaleW),
                (int) (coinBitmap.getHeight() * scaleH), true);

        alien1Bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alien_sprite);
        alien1Bitmap = Bitmap.createScaledBitmap(alien1Bitmap, (int) (alien1Bitmap.getWidth() * scaleW),
                (int) (alien1Bitmap.getHeight() * scaleH), true);

        alienBulletBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alien_bullet);
        alienBulletBitmap = Bitmap.createScaledBitmap(alienBulletBitmap, (int) (alienBulletBitmap.getWidth() * scaleW),
                (int) (alienBulletBitmap.getHeight() * scaleH), true);
    }

    // current horizontal tile
    private long getWTile() {
        return x / tileWidth;
    }

    // number of pixels from start of current tile
    private int getWOffset() {
        return (int) x % tileWidth;
    }

    // adds any new tiles and generates a new set of tiles if needed
    public void update() {
        scrollSpeed = updateScrollSpeed(); // todo: figure out how to update scrollspeed gradually without letting sprites become disjointed
        this.x += (int) scrollSpeed;

        // perform rendering if spaceship has changed tiles
        if (getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty tiles in the current column at the edge of the screen
                if (map[i][mapTileCounter] != EMPTY) {
                    addTile(getMapTile(map[i][mapTileCounter], screenW + getWOffset(), i * tileWidth),
                            (int) scrollSpeed, 0);
                }
            }
            mapTileCounter++;

            // generate more tiles
            if (mapTileCounter == map[0].length) {
                map = generateTiles(GameView.difficulty, rows);
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    // calculates scrollspeed based on difficulty
    // difficulty starts at 0 and increases by 0.01/frame,
    // or 1 per second
    public float updateScrollSpeed() {
        scrollSpeed = (float) (-4.0f - GameView.difficulty / 20);
        if (scrollSpeed < -20) {
            scrollSpeed = -20;
        }
        return scrollSpeed;
    }

    // returns sprite initialized to coordinates (x,y) given tileID
    private Sprite getMapTile(int tileID, float x, float y) throws IndexOutOfBoundsException {
        switch (tileID) {
            case OBSTACLE:
                return new Obstacle(obstacleBitmap, x, y);
            case OBSTACLE_INVIS:
                Sprite tile = new Obstacle(obstacleBitmap, x, y);
                tile.setCollides(false);
                return tile;
            case COIN:
                return new Coin(coinBitmap, x, y);
            case ALIEN_LVL1:
                Alien1 alien_1 = new Alien1(alien1Bitmap, x, y);
                alien_1.injectResources(alienBulletBitmap);
                return alien_1;
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    // sets specified fields and adds sprite to arraylist
    private void addTile(Sprite s, float speedX, float speedY) {
        s.setSpeedX(speedX);
        s.setSpeedY(speedY);
        tiles.add(s);
    }

    // generates a map of tiles based on difficulty and number of rows
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
            } else { // search for nearby empty tiles
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
    private void printMap(byte[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
