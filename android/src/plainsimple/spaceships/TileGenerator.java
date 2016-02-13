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
    // whether to continue a coin trail in the next chunk
    private boolean continueCoinTrail = false;
    // index of row to be left clear in first column of next chunk
    // (used to guide coin trails between chunks and ensure map is
    // not impossible)
    private int nextRow;

    // used for generating random numbers
    private static Random random = new Random();

    public TileGenerator() {
        nextRow = random.nextInt(6);
    }

    // generates a map of sprites based on difficulty and number of rows
    // in screen vertically.
    // difficulty determines probability of certain obstacles, coin
    // trails, and todo powerups
    public byte[][] generateTiles(double difficulty, int rows) {
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
