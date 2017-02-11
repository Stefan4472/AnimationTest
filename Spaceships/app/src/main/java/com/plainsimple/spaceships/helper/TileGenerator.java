package com.plainsimple.spaceships.helper;

import java.util.Random;

/**
 * Created by Stefan on 2/13/2016.
 */
public class TileGenerator {

    // tile id's // todo: use enums
    public static final int EMPTY = 0; // no obstacle
    public static final int OBSTACLE = 1; // basic obstacle
    public static final int OBSTACLE_INVIS = 2; // basic obstacle collision = false
    public static final int COIN = 3; // coin tile
    public static final int ALIEN_LVL1 = 4; // level 1 alien
    public static final int ALIEN_LVL2 = 5; // level 2 alien
    public static final int ALIEN_LVL3 = 6; // level 3 alien

    // length of coin trails
    private static final int COIN_TRAIL_LENGTH = 15;
    // number of coins remaining in current trail
    private int coinsLeft;
    // number of rows of tiles to generate
    private int rows;
    // whether or not to generate a buffer
    private boolean genBuffer;
    // length of buffer
    private int bufferLength = 5;
    // current difficulty
    private int difficulty;

    // used for generating random numbers
    private static Random random = new Random();

    public TileGenerator(int rows) {
        this.rows = rows;
    }

    // generates a map of sprites based on difficulty and number of rows
    // in screen vertically.
    // difficulty determines probability of certain obstacles, coin
    // trails, and todo: powerups
    // between each generated chunk is a buffer, i.e. empty space
    public byte[][] generateTiles(int difficulty) {
        this.difficulty = difficulty;
        genBuffer = !genBuffer;
        if (genBuffer) {
            return new byte[rows][bufferLength];
        } else {
            byte[][] generated;
            if (getPTunnel()) {
                generated = generateTunnel();
            } else if (getPAlienSwarm()) {
                generated = generateAlienSwarm();
            } else {
                generated = generateObstacles();
            }
            if (getPCoinTrail()) {
                generateCoins(generated);
            }
            return generated; // todo: asteroids???
        }
    }

    // generates chunk of randomly-placed obstacles
    private byte[][] generateObstacles() {
        int size = COIN_TRAIL_LENGTH + random.nextInt(5);
        int row = random.nextInt(rows);
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < size; i++) {
            if (getP(0.4f)) {
                generated[row][i] = OBSTACLE;
                // generate another obstacle to the right
                if (i + 1 < size && getP(0.3)) {
                    generated[row][i + 1] = OBSTACLE;
                    i++;
                }
                // generate another obstacle below
                if (row + 1 < rows && getP(0.3)) {
                    generated[row + 1][i] = OBSTACLE;
                } else if (row > 0 && getP(0.2)) { // else try to generate another obstacle above
                    generated[row - 1][i] = OBSTACLE;
                }
                row = random.nextInt(rows);
            }
        }
        return generated;
    }

    // generates tunnel
    private byte[][] generateTunnel() {
        int tunnel_length = COIN_TRAIL_LENGTH + 3 + random.nextInt(10);
        byte[][] generated = new byte[rows][tunnel_length];
        int row = 1 + random.nextInt(rows - 2);
        // probability tunnel will move up or down
        float change_path = 0.0f;
        // generate first column
        for (int i = 0; i < rows; i++) {
            if (i != row) {
                generated[i][0] = OBSTACLE;
            }
        }
        for (int j = 1; j < tunnel_length; j++) {
            if (j < tunnel_length - 2 && getP(change_path)) {
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
                for (int i = 0; i < rows; i++) {
                    if (i == row || i == row + direction) {
                        generated[i][j] = EMPTY;
                        generated[i][j + 1] = EMPTY;
                    } else if (i == row + 2 * direction) {
                        generated[i][j] = OBSTACLE;
                        generated[i][j + 1] = OBSTACLE;
                    } else if (i == row - direction) {
                        generated[i][j] = OBSTACLE;
                        generated[i][j + 1] = OBSTACLE;
                    } else {
                        generated[i][j] = OBSTACLE_INVIS;
                        generated[i][j + 1] = OBSTACLE_INVIS;
                    }
                }
                j++;
                row += direction;
            } else {
                for (int i = 0; i < rows; i++) {
                    if (i == row) {
                        generated[i][j] = EMPTY;
                    } else if (i == row + 1 || i == row - 1) {
                        generated[i][j] = OBSTACLE;
                    } else {
                        generated[i][j] = OBSTACLE_INVIS;
                    }
                }
                change_path += 0.05f;
            }
        }
        return generated;
    }

    private byte[][] generateAlien() {
        int size = 6 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        generated[1 + random.nextInt(6)][size / 2] = ALIEN_LVL1;
        return generated;
    }

    private byte[][] generateAlienSwarm() {
        int num_aliens = 2 + random.nextInt(3);
        int size = (num_aliens + 1) * 8;
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < num_aliens; i++) {
            generated[1 + random.nextInt(4)][8 * (i + 1)] = ALIEN_LVL1;
        }
        return generated;
    }

    // generates a coin trail on map
    private void generateCoins(byte[][] generated) {
        int col, row, end_col;
        // start coin trail somewhere in chunk making sure there is enough space
        if (generated[0].length <= COIN_TRAIL_LENGTH) {
            col = 0; // todo: can change
        } else {
            col = random.nextInt(generated[0].length - COIN_TRAIL_LENGTH);
        }
        end_col = col + COIN_TRAIL_LENGTH;
        coinsLeft = COIN_TRAIL_LENGTH;
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

    // given probability of an event occurring
    // uses random numbers and will return if event should
    // occur or not
    private static boolean getP(double probability) {
        return random.nextInt(100) + 1 <= probability * 100;
    }

    // starting probability of generating a tunnel
    private static final float TUNNEL_P0 = 0.15f;
    // max probability of generating a tunnel
    private static final float TUNNEL_Pf = 0.3f;
    // distance before probability of tunnel increases
    private static final int TUNNEL_INCREASE_THRESHOLD = 1500;
    // distance that must be traveled to increase tunnel probability by 1%
    private static final int TUNNEL_INCREASE_RATE = 300;

    private boolean getPTunnel() {
        double p = TUNNEL_P0 + (difficulty > TUNNEL_INCREASE_THRESHOLD ? (difficulty - TUNNEL_INCREASE_THRESHOLD) / (100 * TUNNEL_INCREASE_RATE) : 0);
        return getP(p > TUNNEL_Pf ? TUNNEL_Pf : p);
    }

    // starting probability of generating an alien swarm // todo: decreasing probabilities?
    private static final float SWARM_P0 = 0.05f;
    // max probability of generating an alien swarm
    private static final float SWARM_Pf = 0.3f;
    // distance before probability of alien swarm increases
    private static final int SWARM_INCREASE_THRESHOLD = 500;
    // distance that must be traveled to increase alien swarm probability by 1%
    private static final int SWARM_INCREASE_RATE = 200;

    // calculates and returns probability of aliens appearing in a swarm
    private boolean getPAlienSwarm() {
        double p = SWARM_P0 + (difficulty > SWARM_INCREASE_THRESHOLD ? (difficulty - SWARM_INCREASE_THRESHOLD) / (100 * SWARM_INCREASE_RATE) : 0);
        return getP(p > SWARM_Pf ? SWARM_Pf : p);
    }

    // starting probability of generating a coin trail
    private static final float COIN_P0 = 0.15f;
    // max probability of generating a coin trail
    private static final float COIN_Pf = 0.7f;
    // distance before probability of cointrail increases
    private static final int COIN_INCREASE_THRESHOLD = 1000;
    // distance that must be traveled to increase coin probability by 1%
    private static final int COIN_INCREASE_RATE = 100;

    // returns whether a coin trail should be generated
    // p = p0 + (d - t) / (100 * r)
    private boolean getPCoinTrail() { // todo: double-check, graph the probabilities out
        double p = COIN_P0 + (difficulty > COIN_INCREASE_THRESHOLD ? (difficulty - COIN_INCREASE_THRESHOLD) / (100 * COIN_INCREASE_RATE) : 0);
        return getP((p > COIN_Pf ? COIN_Pf : p));
    }

    // prints map in a 2-d array
    public static String mapToString(byte[][] map) {
        String result = "";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                result += map[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }

    // generates specific tile set for debugging
    public static byte[][] generateDebugTiles() {
        return new byte[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, ALIEN_LVL1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }
}
