package com.plainsimple.spaceships.engine.map;

import java.util.Arrays;
import java.util.Random;

/**
 * Generates map tiles.
 */
public class TileGenerator {

    public static TileType[][] generateChunk(
            Random rand,
            ChunkType chunkType,
            double difficulty
    ) {
        switch (chunkType) {
            case EMPTY: {
                return generateEmpty(5);
            }
            case OBSTACLES: {
                return generateObstacleField(rand, difficulty);
            }
            case TUNNEL: {
                return generateTunnel(rand, difficulty);
            }
            case ALIEN: {
                return generateAlien(rand, difficulty);
            }
            case ALIEN_SWARM: {
                return generateAlienSwarm(rand, difficulty);
            }
            case ASTEROID: {
                return generateAsteroid(rand, difficulty);
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("ChunkType %s is not supported", chunkType.toString())
                );
            }
        }
    }

    /*
    Generate array of dimensions (NUM_ROWS, num_cols) filled with TileType t.
     */
    public static TileType[][] createArray(TileType t, int numCols) {
        TileType[][] arr = new TileType[Map.NUM_ROWS][numCols];
        for (TileType[] row : arr) {
            Arrays.fill(row, t);
        }
        return arr;
    }

    public static TileType[][] generateEmpty(int numCols) {
        return createArray(TileType.EMPTY, numCols);
    }

    public static TileType[][] generateObstacleField(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 8 + rand.nextInt(8);
        TileType[][] generated = generateEmpty(chunkLength);
        // Higher difficulty -> higher obstacle density
        float pGenerate = 0.3f + (float) (difficulty * 0.4);
        for (int col = 0; col < chunkLength; col++) {
            if (testRandom(rand, pGenerate)) {
                // Place obstacle at random row
                int row = rand.nextInt(Map.NUM_ROWS);
                generated[row][col] = TileType.OBSTACLE;

                // Possibly generate another obstacle immediately above or below
                if (testRandom(rand, 0.25f)) {
                    if (row == 0) {
                        generated[row + 1][col] = TileType.OBSTACLE;
                    } else if (row == Map.NUM_ROWS - 1) {
                        generated[row - 1][col] = TileType.OBSTACLE;
                    } else {
                        generated[row + (rand.nextBoolean() ? 1 : -1)][col] = TileType.OBSTACLE;
                    }
                }
                if (col + 1 < chunkLength && testRandom(rand, 0.25f)) {
                    // Possibly generate another obstacle immediately to the right
                    generated[row][col + 1] = TileType.OBSTACLE;
                }

                // Do not generate a new Obstacle in the next column
                col++;
            }
        }

        return generated;
    }

    /*
    A 2-tile wide tunnel.
     */
    public static TileType[][] generateTunnel(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 6 + rand.nextInt(10);
        // Create array filled with OBSTACLE, then "carve out" the passage
        TileType[][] generated = createArray(TileType.OBSTACLE, chunkLength);

        // Randomly choose starting row of the tunnel passage
        int passage_top = 1 + rand.nextInt(Map.NUM_ROWS - 3);
        int passage_btm = passage_top + 1;
        // Probability that the passage will move up or down
        float p_change_path = 0.0f;
        // The amount that the probability increases per generated column.
        // Higher difficulty = higher likelihood of path changes
        float d_change_path = 0.06f * (float) difficulty;

        for (int col = 0; col < chunkLength; col++) {
            generated[passage_top][col] = TileType.EMPTY;
            generated[passage_btm][col] = TileType.EMPTY;

            // Check whether to change direction
            if (col < chunkLength - 3 && testRandom(rand, p_change_path)) {
                // Decide in which direction to change
                int direction_change;
                if (passage_top == 0) {
                    direction_change = +1;
                } else if (passage_btm == Map.NUM_ROWS - 1) {
                    direction_change = -1;
                } else {
                    direction_change = (testRandom(rand, 0.5f) ? +1 : -1);
                }

                // Construct the change in the tunnel.
                // This can be accomplished by simply setting one tile in
                // the direction of change to EMPTY
                generated[passage_top + direction_change][col] = TileType.EMPTY;
                generated[passage_btm + direction_change][col] = TileType.EMPTY;
                generated[passage_top][col + 1] = TileType.EMPTY;
                generated[passage_btm][col + 1] = TileType.EMPTY;

                passage_top += direction_change;
                passage_btm = passage_top + 1;
                p_change_path = 0.0f;
            } else {
                // Increase probability of changing path
                p_change_path += d_change_path;
            }
        }
        return generated;
    }

    public static TileType[][] generateAlien(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> smaller chunk (less time)
        int chunkLength = 3 + (int) (9 * (1.0 - difficulty));
        TileType[][] generated = generateEmpty(chunkLength);
        generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][0] = TileType.ALIEN;
        return generated;
    }

    public static TileType[][] generateAlienSwarm(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> more aliens
        int numAliens = 2 + (int) (4 * difficulty * rand.nextDouble());
        int chunkLength = 8 * numAliens;
        TileType[][] generated = generateEmpty(chunkLength);
        for (int j = 0; j < numAliens; j++) {
            generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][j * 8] = TileType.ALIEN;
        }
        return generated;
    }

    public static TileType[][] generateAsteroid(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> smaller chunk (less time)
        int chunkLength = 5 + (int) (9 * (1.0 - difficulty));;
        TileType[][] generated = generateEmpty(chunkLength);
        generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][rand.nextInt(7)] = TileType.ASTEROID;
        return generated;
    }

    public static TileType[][] generateAsteroidField(
            Random rand,
            double difficulty
    ) {
        // Higher difficulty -> more asteroids
        int numAsteroids = 2 + (int) (4 * difficulty * rand.nextDouble());
        int chunkLength = 8 * numAsteroids;
        TileType[][] generated = generateEmpty(chunkLength);
        for (int ia = 0; ia < numAsteroids; ia++) {
            generated[1 + rand.nextInt(Map.NUM_ROWS - 2)][8 * ia + rand.nextInt(7)] =
                    TileType.ASTEROID;
        }
        return generated;
    }

    // generates a coin trail on map
//    private static void generateCoins(TileType[][] generated) {
//        int col, row, end_col;
//        // start coin trail somewhere in chunk making sure there is enough space
//        if (generated[0].length <= COIN_TRAIL_LENGTH) {
//            col = 0; // todo: can change
//        } else {
//            col = rand.nextInt(generated[0].length - COIN_TRAIL_LENGTH);
//        }
//        end_col = col + COIN_TRAIL_LENGTH;
//        int coinsLeft = COIN_TRAIL_LENGTH;
//        /* establish empty row to place first coin. trail_distance is the
//        length a trail can go without having to change direction. Longer
//        trail_distance is preferable */
//        int best_row = rand.nextInt(6), max_distance = 1;
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

    /*
    Generates a random float in range [0, 1] and returns whether
    it is less than the given probability.
     */
    private static boolean testRandom(Random rand, float probability) {
        return rand.nextFloat() <= probability;
    }

    /*
    Returns a string representation of the given chunk.
     */
    public static String chunkToString(TileType[][] chunk) {
        StringBuilder result = new StringBuilder();
        for (TileType[] row : chunk) {
            for (TileType tile : row) {
                result.append(tile.ordinal());
                result.append('\t');
            }
            result.append('\n');
        }
        return result.toString();
    }

    public static TileType[][] mergeTiles(TileType[][] arr1, TileType[][] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Arrays must have equal number of rows");
        }
        // TODO: use System.arraycopy() for everything
        TileType[][] result = new TileType[arr1.length][arr1[0].length + arr2[0].length];
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                result[i][j] = arr1[i][j];
            }
        }
        for (int i = 0; i < arr2.length; i++) {
            for (int j = 0; j < arr2[i].length; j++) {
                result[i][j + arr1[i].length] = arr2[i][j];
            }
        }
        return result;
    }
}
