package com.plainsimple.spaceships.helper;

import java.util.Random;

/**
 * Created by Stefan on 2/13/2016.
 */
public class TileGenerator {

    // tile id's
    public static final int EMPTY = 0; // no obstacle
    public static final int OBSTACLE = 1; // basic obstacle
    public static final int COIN = 3; // coin tile
    public static final int ALIEN = 4; // alien
    public static final int ASTEROID = 5; // asteroid
    public static final int END_GAME = 6; // end the game

    // chunkTypes, used by GenCommand to tell the Generator what type of chunk to generate
    public static final String GEN_OBSTACLES = "genObstacles";
    public static final String GEN_COINS = "genCoins";
    public static final String GEN_ALIENS = "genAliens";
    public static final String GEN_ALIEN = "genAlien";
    public static final String GEN_ASTEROID = "genAsteroid";
    public static final String GEN_TUNNEL = "genTunnel;";
    public static final String GEN_RANDOM = "genRandom";
    public static final String GEN_DEBUG = "genDebug";
    public static final String GEN_GAMEOVER = "END";

    // models to simulate probability todo: make less arbitrary
    private static final LinearProbability pTunnel = new LinearProbability(0.15f, 0.3f, 1_500, 300);
    private static final LinearProbability pAlienSwarm = new LinearProbability(0.05f, 0.3f, 500, 200);
    private static final LinearProbability pCoinTrail = new LinearProbability(0.15f, 0.7f, 1_000, 100);
    private static final LinearProbability pAsteroid = new LinearProbability(0.10f, 0.25f, 2_000, 250);

    // length each coin trail must be
    private static final int COIN_TRAIL_LENGTH = 15;
    // number of rows of tiles to generate todo: make final? determine by map?
    private static final int ROWS = 6;
    // used for generating random numbers todo: use seed?
    private static final Random random = new Random();

    // generates a chunk of tiles based on GenCommand given and current difficulty, which might be
    // used to determine certain probabilities. Throws IllegalArgumentException if GenCommand has an
    // invalid field.
    public static byte[][] generateTiles(GenCommand genCommand, int difficulty) throws IllegalArgumentException {
        byte[][] generated;
        // determine generating method to call based on ChunkType defined by the command
        switch (genCommand.getChunkType()) {
            case GEN_OBSTACLES:
                generated = generateObstacles(genCommand.getChunkParam()); // todo: size as a parameter
                break;
            case GEN_ALIEN:
                generated = generateAlien(genCommand.getChunkParam());
                break;
            case GEN_ALIENS:
                generated = generateAlienSwarm(genCommand.getChunkParam());
                break;
            case GEN_ASTEROID:
                generated = generateAsteroid(genCommand.getChunkParam());
                break;
            case GEN_TUNNEL:
                generated = generateTunnel(genCommand.getChunkParam());
                break;
            case GEN_RANDOM:
                generated = generateRandom(difficulty, genCommand.getChunkParam());
                break;
            case GEN_DEBUG:
                return generateDebugTiles();
            case GEN_GAMEOVER:
                return genEndGame(genCommand.getChunkParam());
            default:
                throw new IllegalArgumentException("Invalid ChunkType '" + genCommand.getChunkType() + "'");
        }
        if (testRandom(pCoinTrail.getP(difficulty))) {
            generateCoins(generated);
        }
        return generated;
    }

    // generates a chunk randomly, based on current difficulty. Takes size as a parameter
    private static byte[][] generateRandom(int difficulty, int param) {
        byte[][] generated;
        if (testRandom(pTunnel.getP(difficulty))) {
            generated = generateTunnel(param);
        } else if (testRandom(pAlienSwarm.getP(difficulty))) {
            generated = generateAlienSwarm(param);
        } else if (testRandom(pAsteroid.getP(difficulty))) {
            generated = generateAsteroid(param);
        } else {
            generated = generateObstacles(param);
        }
        return generated;
    }

    // generates chunk of randomly-placed obstacles
    private static byte[][] generateObstacles(int chunkLength) {
        // calculate default size if desired; else use given size
        if (chunkLength == GenCommand.DEFAULT) {
            chunkLength = COIN_TRAIL_LENGTH + random.nextInt(5);
        }
        int row = random.nextInt(ROWS);
        byte[][] generated = new byte[ROWS][chunkLength];
        for (int i = 0; i < chunkLength; i++) {
            if (testRandom(0.4f)) {
                generated[row][i] = OBSTACLE;
                // generate another obstacle to the right
                if (i + 1 < chunkLength && testRandom(0.3f)) {
                    generated[row][i + 1] = OBSTACLE;
                    i++;
                }
                // generate another obstacle below
                if (row + 1 < ROWS && testRandom(0.3f)) {
                    generated[row + 1][i] = OBSTACLE;
                } else if (row > 0 && testRandom(0.2f)) { // else try to generate another obstacle above
                    generated[row - 1][i] = OBSTACLE;
                }
                row = random.nextInt(ROWS);
            }
        }
        return generated;
    }

    // generates tunnel of the given length todo: preceded by five columns of empty?
    private static byte[][] generateTunnel(int tunnelLength) {
        if (tunnelLength == GenCommand.DEFAULT) {
            tunnelLength = COIN_TRAIL_LENGTH + 3 + random.nextInt(10);
        }
        byte[][] generated = new byte[ROWS][tunnelLength];
        int row = 1 + random.nextInt(ROWS - 2);
        // probability tunnel will move up or down
        float change_path = 0.0f;
        // generate first column
        for (int i = 0; i < ROWS; i++) {
            if (i != row) {
                generated[i][0] = OBSTACLE;
            }
        }
        for (int j = 1; j < tunnelLength; j++) {
            // check whether to change direction (based on probability)
            if (j < tunnelLength - 2 && testRandom(change_path)) {
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
                // construct the change in the tunnel
                for (int i = 0; i < ROWS; i++) {
                    if (i == row || i == row + direction) {
                        generated[i][j] = EMPTY;
                        generated[i][j + 1] = EMPTY;
                    } else {
                        generated[i][j] = OBSTACLE;
                        generated[i][j + 1] = OBSTACLE;
                    }
                }
                j++;
                row += direction;
            } else {
                for (int i = 0; i < ROWS; i++) {
                    if (i == row) {
                        generated[i][j] = EMPTY;
                    } else {
                        generated[i][j] = OBSTACLE;
                    }
                }
                // increase probability of changing path by 5%
                change_path += 0.05f;
            }
        }
        return generated;
    }

    // generates a single alien at a random row index in a chunk of the given length. Alien will be
    // placed randomly
    private static byte[][] generateAlien(int chunkLength) {
        if (chunkLength == GenCommand.DEFAULT) {
            chunkLength = 6 + random.nextInt(10);
        }
        byte[][] generated = new byte[ROWS][chunkLength];
        generated[1 + random.nextInt(ROWS - 1)][chunkLength / 2] = ALIEN;
        return generated;
    }

    // generates numAliens aliens. Each alien is preceded and succeeded by 8 empty tiles.
    private static byte[][] generateAlienSwarm(int numAliens) {
        if (numAliens == GenCommand.DEFAULT) {
            numAliens = 2 + random.nextInt(3);
        }
        int size = (numAliens + 1) * 8;
        byte[][] generated = new byte[ROWS][size];
        for (int i = 0; i < numAliens; i++) {
            generated[1 + random.nextInt(4)][8 * (i + 1)] = ALIEN;
        }
        return generated;
    }

    // generates a single asteroid at a random row index in a chunk of the given length. Asteroid is
    // placed randomly within the chunk
    private static byte[][] generateAsteroid(int chunkLength) {
        if (chunkLength == GenCommand.DEFAULT) {
            chunkLength = 8;
        }
        byte[][] generated = new byte[ROWS][chunkLength];
        generated[2][random.nextInt(chunkLength)] = ASTEROID;
        return generated;
    }

    // generates a coin trail on map
    private static void generateCoins(byte[][] generated) {
        int col, row, end_col;
        // start coin trail somewhere in chunk making sure there is enough space
        if (generated[0].length <= COIN_TRAIL_LENGTH) {
            col = 0; // todo: can change
        } else {
            col = random.nextInt(generated[0].length - COIN_TRAIL_LENGTH);
        }
        end_col = col + COIN_TRAIL_LENGTH;
        int coinsLeft = COIN_TRAIL_LENGTH;
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

    // generates a chunk whose last column is filled with END_GAME tiles. The numEmpty param specifies
    // how many columns of empty tiles to have before the END_GAME column
    private static byte[][] genEndGame(int numEmpty) {
        if (numEmpty == GenCommand.DEFAULT) {
            numEmpty = 4;
        }
        byte[][] generated = new byte[ROWS][numEmpty + 1];
        for (int i = 0; i < ROWS; i++) {
            generated[i][numEmpty] = END_GAME;
        }
        return generated;
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
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, ALIEN, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {ASTEROID, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    // a class to calculate linear probability for map generation
    private static class LinearProbability {
        
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

    // object that contains information required to generate a chunk of tiles. This includes the
    // tileId, which must be
    public static class GenCommand {

        // key specifying type of chunk to generate. Must be a valid chunkType defined in the class
        private String chunkType;
        // parameter for generation. Chunk-generating methods may interpret it in different ways
        private int chunkParam;
        // tells generator to calculate and use a default size for the chunk
        public static final int DEFAULT = 0;

        public GenCommand(String chunkType, int chunkParam) {
            this.chunkType = chunkType;
            this.chunkParam = chunkParam;
        }

        public String getChunkType() {
            return chunkType;
        }

        public int getChunkParam() {
            return chunkParam;
        }

        @Override
        public String toString() {
            return chunkType + "[" + chunkParam + "]";
        }
    }
}
