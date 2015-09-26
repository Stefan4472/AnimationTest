package plainsimple.spaceships;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

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

    // number of rows of tiles that fit in map
    private int rows;

    // number of tiles elapsed since last map was generated
    private int mapTileCounter = 0;

    // keeps track of tile spaceship was on last time map was updated
    private long lastTile = 0;

    // default speed tiles scrolling across the map
    private float scrollSpeed = -4.0f;

    // generated sprites
    private ArrayList<Sprite> tiles = new ArrayList<>();

    // board upon which Map lies
    private Board board;

    // dimensions of screen display
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private long x = 0;

    // length of coin trails
    private static final int coinTrailLength = 15;
    // number of coins remaining in current trail
    private int coinsLeft;
    // index of row to be left clear in first column of next chunk
    // (used to guide coin trails between chunks and ensure map is
    // not impossible)
    private static int nextRow;

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
    public void setBoard(Board board) {
        this.board = board;
    }

    public ArrayList<Sprite> getProjectiles() {
        return (ArrayList<Sprite>) tiles.stream()
                .filter(s -> s.getClass().equals(Alien.class))
                .map(s -> ((Alien) s).getProjectiles())
                .flatMap(b -> b.stream())
                .collect(Collectors.toList());
    }

    public Map(Board board) {
        this.board = board;
        initMap();
    }

    private void initMap() {
        rows = SCREEN_HEIGHT / tileHeight;
        map = new byte[6][7];
        nextRow = random.nextInt(6);
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
                    addTile(getMapTile(map[i][mapTileCounter], SCREEN_WIDTH + getWOffset(), i * tileWidth),
                            (int) scrollSpeed, 0, board);
                }
            }
            mapTileCounter++;

            // generate more tiles
            if (mapTileCounter == map[0].length) {
                map = generateTiles(board.getDifficulty(), rows);
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    // calculates scrollspeed based on difficulty
    // difficulty starts at 0 and increases by 0.01/frame,
    // or 1 per second
    public float updateScrollSpeed() {
        scrollSpeed = (float) (-4.0f - board.getDifficulty() / 20);
        if (scrollSpeed < -20) {
            scrollSpeed = -20;
        }
        return scrollSpeed;
    }

    // returns sprite initialized to coordinates (x,y) given tileID
    private Sprite getMapTile(int tileID, double x, double y) throws IndexOutOfBoundsException {
        switch (tileID) {
            case OBSTACLE:
                return new Obstacle("tiles/obstacle/obstacle_tile.png", x, y, board);
            case OBSTACLE_INVIS:
                Sprite tile = new Obstacle("tiles/obstacle/obstacle_tile.png", x, y, board);
                tile.setCollides(false);
                return tile;
            case COIN:
                return new Coin("tiles/coin/coin_tile.png", x, y, board);
            case ALIEN_LVL1:
                return new Alien1("sprites/alien/alien_sprite.png", x, y, board);
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    // sets specified fields and adds sprite to arraylist
    private void addTile(Sprite s, double speedX, double speedY, Board board) {
        s.setSpeedX(speedX);
        s.setSpeedY(speedY);
        s.setBoard(board);
        tiles.add(s);
    }

    // generates a map of tiles based on difficulty and number of rows
    // in screen vertically.
    // difficulty determines probability of certain obstacles, coin
    // trails, and todo powerups
    private static byte[][] generateTiles(double difficulty, int rows) {
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
        generateCoins(generated);
        return generated;
    }

    // generates map cluster of simple obstacle
    private static byte[][] generateObstacles(int rows) {
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
        //nextRow = getNextRow(rows, row);
        return generated;
    }

    // generates tunnel
    private static byte[][] generateTunnel(int rows) {
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

    private static byte[][] generateAlien(int rows) {
        int size = 4 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        generated[random.nextInt(6)][size - 1] = ALIEN_LVL1;
        return generated;
    }

    private static byte[][] generateAlienSwarm(int rows) {
        int num_aliens = 2 + random.nextInt(3);
        int size = num_aliens * 8 + 1;
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < num_aliens; i++) {
            generated[random.nextInt(6)][8 * (i + 1)] = ALIEN_LVL1;
        }
        return generated;
    }

    // generates a coin trail on map
    private static void generateCoins(byte[][] generated) {
        int col = random.nextInt(generated[0].length / 2);
        int end_col = generated[0].length - random.nextInt(generated[0].length / 4);
        // establish empty row to place first coin,
        // prioritizing rows closer to the middle
        int row; //= 2 + random.nextInt(2);
        // trail_distance is the length a trail can go without
        // having to change direction. Longer trail_distance
        // is preferable
        int best_row = random.nextInt(6), max_distance = 1;
        for (int i = 0; i < generated.length; i++) {
            int trail_distance = 1 - 2 * (Math.abs(3 - i)), j = 0; // middle columns are favored
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
        for(int i = col; i < end_col; i++) {
            if(generated[row][col] == EMPTY) {
                generated[row][col] = COIN;
            } else { // search for nearby empty tiles
                if(row < generated.length - 1 && generated[row + 1][col] == EMPTY) {
                    row += 1;
                    generated[row][col] = COIN;
                    generated[row][col - 1] = COIN;
                } else if(row > 0 && generated[row - 1][col] == EMPTY) {
                    row -= 1;
                    generated[row][col] = COIN;
                    generated[row][col - 1] = COIN;
                }
            }
            col++;
        }

    }

    // gives a possible value for nextRow, a row to keep
    // free of obstacles in the next generated chunk
    private static int getNextRow(int rows, int lastRow) {
        int row_change = random.nextInt(3) + 1;
        int result = lastRow + (getP(0.5) ? + row_change : - row_change);
        if(result > rows - 1) {
            System.out.println("Map.java: prevRow " + lastRow + ", nextRow " + (rows - 1));
            return rows - 1;
        } else if(result < 0) {
            System.out.println("Map.java: prevRow " + lastRow + ", nextRow " + 0);
            return 0;
        }
        System.out.println("Map.java: prevRow " + lastRow + ", nextRow " + result);
        return result;
    }

    // generates random number using random.nextInt(range)
    // that is not equal to exclusive
    private static int genRandExcl(int range, int exclusive) {
        int rand;
        do {
            rand = random.nextInt(range);
        } while(rand == exclusive);
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
        if(110 - difficulty >= 50) {
            return (110 - difficulty) / 100;
        } else {
            return 0.5;
        }
    }

    private static double getPTunnel(double difficulty) {
        if(-10 + difficulty < 50) {
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
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[0].length; j++) {
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
