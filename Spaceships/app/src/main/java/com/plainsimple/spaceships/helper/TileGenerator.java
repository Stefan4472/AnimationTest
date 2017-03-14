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
    public static final int ASTEROID = 7; // asteroid

    // models to simulate probability todo: make less arbitrary
    private LinearProbability pTunnel = new LinearProbability(0.15f, 0.3f, 1_500, 300);
    private LinearProbability pAlienSwarm = new LinearProbability(0.05f, 0.3f, 500, 200);
    private LinearProbability pCoinTrail = new LinearProbability(0.15f, 0.7f, 1_000, 100);
    private LinearProbability pAsteroid = new LinearProbability(0.10f, 0.25f, 2_000, 250);

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
            if (testRandom(pTunnel.getP(difficulty))) {
                generated = generateTunnel();
            } else if (testRandom(pAlienSwarm.getP(difficulty))) {
                generated = generateAlienSwarm();
            } else if (testRandom(pAsteroid.getP(difficulty))) {
                generated = generateAsteroid();
            } else {
                generated = generateObstacles();
            }
            if (testRandom(pCoinTrail.getP(difficulty))) {
                generateCoins(generated);
            }
            return generated;
        }
    }

    // generates chunk of randomly-placed obstacles
    private byte[][] generateObstacles() {
        int size = COIN_TRAIL_LENGTH + random.nextInt(5);
        int row = random.nextInt(rows);
        byte[][] generated = new byte[rows][size];
        for (int i = 0; i < size; i++) {
            if (testRandom(0.4f)) {
                generated[row][i] = OBSTACLE;
                // generate another obstacle to the right
                if (i + 1 < size && testRandom(0.3f)) {
                    generated[row][i + 1] = OBSTACLE;
                    i++;
                }
                // generate another obstacle below
                if (row + 1 < rows && testRandom(0.3f)) {
                    generated[row + 1][i] = OBSTACLE;
                } else if (row > 0 && testRandom(0.2f)) { // else try to generate another obstacle above
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
            if (j < tunnel_length - 2 && testRandom(change_path)) {
                change_path = -0.1f;
                // determine which direction to change in
                int direction = 0;
                if (testRandom(0.5f)) {
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

    private byte[][] generateAsteroid() {
        byte[][] generated = new byte[rows][8];
        generated[2][random.nextInt(6)] = ASTEROID;
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

    // generates a random float and checks if it is below given
    // probability. Returns true if it does. Used to decide whether
    // to generate certain types of obstacles given their probabilities
    // of occurring.
    private static boolean testRandom(float probability) {
        return random.nextFloat() <= probability;
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
                {0, 0, 0, 0, 0, ASTEROID, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, ASTEROID, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    // a class to calculate linear probability for map generation
    private class LinearProbability {
        
        // starting probability
        private float initialProbability;
        // final probability (max/min)
        private float finalProbability;
        // minimum difficulty for probability to start changing
        private int changeThreshold;
        // change in difficulty required to increase/decrease probability by 0.01f
        private float changeRate;

        private LinearProbability(float initialProbability, 
                                  float finalProbability, 
                                  int changeThreshold, 
                                  float changeRate) throws IllegalArgumentException {

            if (initialProbability < 0 || initialProbability > 1 || finalProbability < 0 ||
                    finalProbability > 1 || changeThreshold < 0 || changeRate < 0) {
                throw new IllegalArgumentException("Invalid Parameter(s)");
            } else {
                this.initialProbability = initialProbability;
                this.finalProbability = finalProbability;
                this.changeThreshold = changeThreshold;
                this.changeRate = changeRate;
            }
        }

        // calculates and returns probability based on given difficulty
        public float getP(int difficulty) {
            // return initialProbability if difficulty isn't
            // high enough to have reached changeThreshold
            if (difficulty < changeThreshold) {
                return initialProbability;
            } else {
                // calculate probability based on given difficulty and parameters
                float p = initialProbability + (difficulty - changeThreshold) / changeRate / 100;
                // ensure we don't return a p outside the max/min
                if (changeRate > 0 && p > finalProbability) {
                    return finalProbability;
                } else if (changeRate < 0 && p < finalProbability) {
                    return finalProbability;
                } else {
                    return finalProbability;
                }
            }
        }
    }
}
