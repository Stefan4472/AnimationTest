package com.plainsimple.spaceships.helper;

import java.util.Arrays;
import java.util.Random;

import static com.plainsimple.spaceships.helper.TileGenerator.TileID.ALIEN;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.ASTEROID;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.EMPTY;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.OBSTACLE;

/**
 * Created by Stefan on 2/13/2016.
 */
public class TileGenerator {

    // Tile IDs
    public enum TileID {
        EMPTY,
        OBSTACLE,
        COIN,
        ALIEN,
        ASTEROID
    };

    // Types of possible chunks
    public enum ChunkType {
        EMPTY,
        OBSTACLES,
        TUNNEL,
        ALIEN,
        ALIEN_SWARM,
        ASTEROID
    };

    // The number of rows of tiles the game has. Does not change.
    public static final int NUM_ROWS = 6;
    // Length of coin trails. TODO: VARY WITH DIFFICULTY
    private static final int COIN_TRAIL_LENGTH = 15;

    // Used for generating random numbers TODO: use seed? PASS IN FROM MAP?
    private static Random random = new Random();

    // TODO: RANDOMNESS IN CHUNK GENERATION, COIN GENERATOIN, DIFFICULTY USAGE(?)

    // Convenience method
    public static TileID[][] generateChunk(
            ChunkType chunkType,
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        // TODO: ADD ASTEROID_FIELD CHUNK TYPE
        switch (chunkType) {
            case EMPTY: {
                return generateEmptyChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            case OBSTACLES: {
                return generateObstaclesChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            case TUNNEL: {
                return generateTunnelChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            case ALIEN: {
                return generateAlienChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            case ALIEN_SWARM: {
                return generateAlienSwarmChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            case ASTEROID: {
                return generateAsteroidChunk(leadingBufferLength, difficulty, shouldGenerateCoins);
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("ChunkType %s is not supported", chunkType.toString())
                );
            }
        }
    }

    // Generates chunk of randomly-placed obstacles
    public static TileID[][] generateObstaclesChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        int chunk_length = leadingBufferLength + 10;
        TileID[][] generated = generateEmpty(chunk_length);

        for (int col = leadingBufferLength; col < chunk_length; col++) {
            if (testRandom(0.4f)) {
                // Place obstacle at random row
                int row = random.nextInt(NUM_ROWS);
                generated[row][col] = OBSTACLE;

                // Attempt to generate another obstacle immediately to the right
                if (col + 1 < chunk_length && testRandom(0.3f)) {
                    generated[row][col + 1] = OBSTACLE;
                }

                // Attempt to generate another obstacle immediately above or below
                if (row + 1 < NUM_ROWS && testRandom(0.3f)) {
                    generated[row + 1][col] = OBSTACLE;
                } else if (row > 0 && testRandom(0.3f)) {
                    // else try to generate another obstacle above
                    generated[row - 1][col] = OBSTACLE;
                }
            }
        }

        return generated;
    }

    // generates tunnel of the given length
    public static TileID[][] generateTunnelChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        int chunk_length = leadingBufferLength + 15;
        TileID[][] generated = generateEmpty(chunk_length);

        // TODO: MAKE PASSAGES TWO ROWS BIG

        // Randomly choose starting row of the tunnel
        int tunnel_row = 1 + random.nextInt(NUM_ROWS - 2);
        // Probability that the tunnel will move up or down
        float p_change_path = 0.0f;

        for (int col = leadingBufferLength; col < chunk_length; col++) {
            // Check whether to change direction (based on probability)
            if (col < chunk_length - 2 && testRandom(p_change_path)) {
                // Determine which direction to change in
                int direction_change = 0;

                if (tunnel_row == 0) {
                    direction_change = 1;
                } else if (tunnel_row == NUM_ROWS - 1) {
                    direction_change = -1;
                } else {
                    direction_change = (testRandom(0.5f) ? +1 : -1);
                }

                // Construct the change in the tunnel
                for (int i = 0; i < NUM_ROWS; i++) {
                    if (i == tunnel_row || i == tunnel_row + direction_change) {
                        generated[i][col] = EMPTY;
                        generated[i][col + 1] = EMPTY;
                    } else {
                        generated[i][col] = OBSTACLE;
                        generated[i][col + 1] = OBSTACLE;
                    }
                }
                col++;
                tunnel_row += direction_change;
                p_change_path = 0.0f;
            } else {
                for (int i = 0; i < NUM_ROWS; i++) {
                    if (i == tunnel_row) {
                        generated[i][col] = EMPTY;
                    } else {
                        generated[i][col] = OBSTACLE;
                    }
                }

                // Increase probability of changing path by 5 percent
                p_change_path += 0.05f;
            }
        }
        return generated;
    }

    // generates a single alien at a random row index in a chunk of the given length. Alien will be
    // placed randomly
    public static TileID[][] generateAlienChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        int chunk_length = leadingBufferLength + 9;
        TileID[][] generated = generateEmpty(chunk_length);
        generated[1 + random.nextInt(NUM_ROWS - 1)][leadingBufferLength + 3] = ALIEN;
        return generated;
    }

    // generates numAliens aliens. Each alien is preceded and succeeded by 8 empty tiles.
    public static TileID[][] generateAlienSwarmChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        int num_aliens = 5;
        int chunk_length = leadingBufferLength + 8 * num_aliens;
        TileID[][] generated = generateEmpty(chunk_length);
        for (int a = 1; a <= num_aliens; a++) {
            generated[1 + random.nextInt(NUM_ROWS - 1)][8 * a] = ALIEN;
        }
        return generated;
    }

    // generates a single asteroid at a random row index in a chunk of the given length. Asteroid is
    // placed randomly within the chunk
    public static TileID[][] generateAsteroidChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        int chunk_length = leadingBufferLength + 7;
        TileID[][] generated = generateEmpty(chunk_length);
        generated[1 + random.nextInt(NUM_ROWS - 1)][random.nextInt(7)] = ASTEROID;
        return generated;
    }

    // generates a coin trail on map
//    private static void generateCoins(TileID[][] generated) {
//        int col, row, end_col;
//        // start coin trail somewhere in chunk making sure there is enough space
//        if (generated[0].length <= COIN_TRAIL_LENGTH) {
//            col = 0; // todo: can change
//        } else {
//            col = random.nextInt(generated[0].length - COIN_TRAIL_LENGTH);
//        }
//        end_col = col + COIN_TRAIL_LENGTH;
//        int coinsLeft = COIN_TRAIL_LENGTH;
//        /* establish empty row to place first coin. trail_distance is the
//        length a trail can go without having to change direction. Longer
//        trail_distance is preferable */
//        int best_row = random.nextInt(6), max_distance = 1;
//        for (int i = 0; i < generated.length; i++) {
//            int trail_distance = 0, j = 0;
//            while (col + j < generated[0].length && generated[i][col + j] == EMPTY) {
//                trail_distance++;
//                if (trail_distance > max_distance) {
//                    max_distance = trail_distance;
//                    best_row = i;
//                }
//                j++;
//            }
//        }
//        row = best_row;
//        for (int i = col; i < generated[0].length && i < end_col && coinsLeft > 0; i++, coinsLeft--) {
//            if (generated[row][i] == EMPTY) {
//                generated[row][i] = COIN;
//            } else { // search for nearby empty tiles
//                if(row < generated.length - 1 && generated[row + 1][i] == EMPTY) {
//                    generated[row + 1][i] = COIN;
//                    generated[row + 1][i - 1] = COIN;
//                    row += 1;
//                } else if(row > 0 && generated[row - 1][i] == EMPTY) {
//                    generated[row - 1][i] = COIN;
//                    generated[row - 1][i - 1] = COIN;
//                    row -= 1;
//                }
//            }
//        }
//    }

    // TODO: THE ARGS TO THIS DON'T REALLY MAKE SENSE
    public static TileID[][] generateEmptyChunk(
            int leadingBufferLength,
            double difficulty,
            boolean shouldGenerateCoins
    ) {
        return generateEmpty(leadingBufferLength + 5);
    }

    public static TileID[][] generateEmpty(int numCols) {
        TileID empty[][] = new TileID[NUM_ROWS][numCols];
        // Fill with `EMPTY` tiles (default is NULL)
        for (TileID[] row : empty) {
            Arrays.fill(row, TileID.EMPTY);
        }
        return empty;
    }

    // generates a random float and checks if it is below given
    // probability. Returns true if it does. Used to decide whether
    // to generate certain types of obstacles given their probabilities
    // of occurring.
    private static boolean testRandom(float probability) {
        return random.nextFloat() <= probability;
    }

    // generates specific tile set for debugging
    public static TileID[][] generateDebugTiles() {
        TileID tiles[][] = new TileID[NUM_ROWS][8];
        tiles[4][0] = ASTEROID;
        tiles[2][6] = ALIEN;
        tiles[0][0] = OBSTACLE;
        tiles[0][1] = OBSTACLE;
        tiles[0][2] = OBSTACLE;
        tiles[0][3] = OBSTACLE;
        return tiles;
    }

    // prints map in a 2-d array
    public static String mapToString(TileID[][] map) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                result.append(map[i][j].ordinal());
                result.append('\t');
            }
            result.append('\n');
        }
        return result.toString();
    }

// models to simulate probability todo: PROBABILITY CALCULATION HAS BEEN MOVED TO `MAP`
//    private static final LinearProbability pTunnel = new LinearProbability(0.15f, 0.3f, 1_500, 300);
//    private static final LinearProbability pAlienSwarm = new LinearProbability(0.05f, 0.3f, 500, 200);
//    private static final LinearProbability pCoinTrail = new LinearProbability(0.15f, 0.7f, 1_000, 100);
//    private static final LinearProbability pAsteroid = new LinearProbability(0.10f, 0.25f, 2_000, 250);
//    // a class to calculate linear probability for map generation
//    private static class LinearProbability {
//
//        // starting probability
//        private float initialProbability;
//        // final probability (max/min)
//        private float finalProbability;
//        // minimum difficulty for probability to start changing
//        private int changeThreshold;
//        // change in difficulty required to increase/decrease probability by 0.01f
//        private float changeRate;
//
//        private LinearProbability(float initialProbability,
//                                  float finalProbability,
//                                  int changeThreshold,
//                                  float changeRate) throws IllegalArgumentException {
//
//            if (initialProbability < 0 || initialProbability > 1 || finalProbability < 0 ||
//                    finalProbability > 1 || changeThreshold < 0 || changeRate < 0) {
//                throw new IllegalArgumentException("Invalid Parameter(s)");
//            } else {
//                this.initialProbability = initialProbability;
//                this.finalProbability = finalProbability;
//                this.changeThreshold = changeThreshold;
//                this.changeRate = changeRate;
//            }
//        }
//
//        // calculates and returns probability based on given difficulty
//        public float getP(int difficulty) {
//            // return initialProbability if difficulty isn't
//            // high enough to have reached changeThreshold
//            if (difficulty < changeThreshold) {
//                return initialProbability;
//            } else {
//                // calculate probability based on given difficulty and parameters
//                float p = initialProbability + (difficulty - changeThreshold) / changeRate / 100;
//                // ensure we don't return a p outside the max/min
//                if (changeRate > 0 && p > finalProbability) {
//                    return finalProbability;
//                } else if (changeRate < 0 && p < finalProbability) {
//                    return finalProbability;
//                } else {
//                    return finalProbability;
//                }
//            }
//        }
//    }
}
