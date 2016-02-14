package plainsimple.spaceships;

import java.util.Random;

/**
 * Created by Stefan on 2/13/2016.
 */
public class TileGenerator {

    // tile id's
    public static final int EMPTY = 0; // no obstacle
    public static final int OBSTACLE = 1; // basic obstacle
    public static final int OBSTACLE_INVIS = 2; // basic obstacle collision = false
    public static final int COIN = 3; // coin tile
    public static final int ALIEN_LVL1 = 4; // level 1 alien
    public static final int ALIEN_LVL2 = 5; // level 2 alien
    public static final int ALIEN_LVL3 = 6; // level 3 alien

    // length of coin trails
    private static final int coinTrailLength = 18;
    // number of coins remaining in current trail
    private int coinsLeft;
    // number of rows of tiles to generate
    private int rows;
    // whether or not to generate a buffer
    private boolean genBuffer;
    // length of buffer
    private int bufferLength = 3;
    // whether to continue a coin trail in the next chunk
    private boolean continueCoinTrail = false;
    // index of row to be left clear in first column of next chunk
    // (used to guide coin trails between chunks and ensure map is
    // not impossible)
    private int nextRow;

    // used for generating random numbers
    private static Random random = new Random();

    public TileGenerator(int rows) {
        nextRow = random.nextInt(6);
        genBuffer = true;
        this.rows = rows;
    }

    // generates a map of sprites based on difficulty and number of rows
    // in screen vertically.
    // difficulty determines probability of certain obstacles, coin
    // trails, and todo powerups
    public byte[][] generateTiles(double difficulty) {
        if (genBuffer) {
            genBuffer = false;
            return generateBuffer(bufferLength);
        } else {
            genBuffer = true;
            byte[][] generated;
            if (getP(getPTile(difficulty))) {
                if (getP(getPTunnel(difficulty))) {
                    generated = generateTunnel();
                } else {
                    generated = generateObstacles();
                }
            } else {
                //if(getP(0.5)) {
                if (getP(getPAlienSwarm(difficulty))) {
                    generated = generateAlienSwarm();
                } else {
                    generated = generateAlien();
                }
                //} else {
                //    map = generateAsteroid();
                //}
            }
            // 15% chance of generating coin trail
            if (getP(0.15)) { // todo: getPCoinTrail
                generateCoins(generated);
            }
            return generated;
        }
    }

    // generates map cluster of simple obstacle // todo: generate buffers separately, as empty arrays in between non-empty arrays
    private byte[][] generateObstacles() {
        int size = 10 + random.nextInt(5), row = 0;
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < size; i++) {
            if (getP(0.2f)) {
                generated[row][i] = OBSTACLE;
                // generate another obstacle to the right
                if (i + 1 < size && getP(0.5)) {
                    generated[row][i + 1] = OBSTACLE;
                }
                // generate another obstacle below
                if (row + 1 < rows && getP(0.3)) {
                    generated[row + 1][i] = OBSTACLE;
                }
                // generate another obstacle above
                if (row > 0 && getP(0.2)) {
                    generated[row - 1][i] = OBSTACLE;
                }
            }
        }
        return generated;
    }

    // generates tunnel
    private byte[][] generateTunnel() {
        int tunnel_length = 18 + random.nextInt(10);
        // create 3-tile buffer zone on each end of tunnel
        byte[][] generated = new byte[rows][tunnel_length];
        int row = 1 + random.nextInt(rows - 2);
        float change_path = 0.0f;
        // generate first column
        for (int i = 0; i < rows; i++) {
            if (i != row) {
                generated[i][0] = OBSTACLE;
            }
        }
        for (int i = 1; i < tunnel_length; i++) {
            if (getP(change_path) && i < tunnel_length - 2) {
                change_path = -0.1f;
                // determine which direction to change in
                int direction = 0;
                if (getP(0.5f)) {
                    if (row < generated.length - 1) {
                        direction = 1;
                    } else if (row > 0) {
                        direction = -1;
                    }
                } else {
                    if (row > 0) {
                        direction = -1;
                    } else if (row < generated.length - 1) {
                        direction = 1;
                    }
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
        return generated;
    }

    private byte[][] generateAlien() {
        int size = 4 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        generated[random.nextInt(6)][size / 2] = ALIEN_LVL1;
        return generated;
    }

    private byte[][] generateAlienSwarm() {
        int num_aliens = 2 + random.nextInt(3);
        int size = (num_aliens + 1) * 8;
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < num_aliens; i++) {
            generated[random.nextInt(6)][8 * (i + 1)] = ALIEN_LVL1;
        }
        return generated;
    }

    // generates a coin trail on map
    private void generateCoins(byte[][] generated) {
        int col, row, end_col;
        // start coin trail somewhere in chunk making sure there is enough space
        if (generated[0].length < coinTrailLength) {
            col = 0; // todo: can change
        } else {
            col = random.nextInt(generated[0].length - coinTrailLength);
        }
        end_col = col + coinTrailLength;
        coinsLeft = coinTrailLength;
        /* establish empty row to place first coin. trail_distance is the
        length a trail can go without having to change direction. Longer
        trail_distance is preferable */
        int best_row = random.nextInt(6), max_distance = 1;
        for (int i = 0; i < generated.length; i++) {
            int trail_distance = 0, j = 0;
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
        for (int i = col; i < generated[0].length && i < end_col && coinsLeft > 0; i++, coinsLeft--) {
            if (generated[row][i] == EMPTY) {
                generated[row][i] = COIN;
            } else { // search for nearby empty tiles
                if(row < generated.length - 1 && generated[row + 1][i] == EMPTY) {
                    generated[row + 1][i] = COIN;
                    generated[row + 1][i - 1] = COIN;
                    row += 1;
                } else if(row > 0 && generated[row - 1][i] == EMPTY) {
                    generated[row - 1][i] = COIN;
                    generated[row - 1][i - 1] = COIN;
                    row -= 1;
                }
            }
        }
    }

    // generates an empty tile map with specified number of columns
    private byte[][] generateBuffer(int bufferLength) {
        byte[][] generated = new byte[rows][bufferLength];
        for (byte[] column : generated) { // todo: does this cycle through rows or columns?
            for (byte tile : column) {
                tile = EMPTY;
            }
        }
        return generated;
    }

    // given probability of an event occurring
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
