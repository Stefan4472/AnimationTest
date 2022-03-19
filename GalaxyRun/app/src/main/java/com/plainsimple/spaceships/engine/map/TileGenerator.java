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
                return generateObstaclesChunk(rand, difficulty);
            }
            case TUNNEL: {
                return generateTunnelChunk(rand, difficulty);
            }
            case ALIEN: {
                return generateAlienChunk(rand, difficulty);
            }
            case ALIEN_SWARM: {
                return generateAlienSwarmChunk(rand, difficulty);
            }
            case ASTEROID: {
                return generateAsteroidChunk(rand, difficulty);
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("ChunkType %s is not supported", chunkType.toString())
                );
            }
        }
    }

    // Generates chunk of randomly-placed obstacles
    public static TileType[][] generateObstaclesChunk(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 10;
        TileType[][] generated = generateEmpty(chunkLength);

        for (int col = 0; col < chunkLength; col++) {
            if (testRandom(rand, 0.4f)) {
                // Place obstacle at random row
                int row = rand.nextInt(Map.NUM_ROWS);
                generated[row][col] = TileType.OBSTACLE;

                // Attempt to generate another obstacle immediately to the right
                if (col + 1 < chunkLength && testRandom(rand, 0.3f)) {
                    generated[row][col + 1] = TileType.OBSTACLE;
                }

                // Attempt to generate another obstacle immediately above or below
                if (row + 1 < Map.NUM_ROWS && testRandom(rand, 0.3f)) {
                    generated[row + 1][col] = TileType.OBSTACLE;
                } else if (row > 0 && testRandom(rand, 0.3f)) {
                    // else try to generate another obstacle above
                    generated[row - 1][col] = TileType.OBSTACLE;
                }
            }
        }

        return generated;
    }

    // generates tunnel of the given length
    public static TileType[][] generateTunnelChunk(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 15;
        TileType[][] generated = generateEmpty(chunkLength);

        // TODO: MAKE PASSAGES TWO ROWS BIG

        // Randomly choose starting row of the tunnel
        int tunnel_row = 1 + rand.nextInt(Map.NUM_ROWS - 2);
        // Probability that the tunnel will move up or down
        float p_change_path = 0.0f;

        for (int col = 0; col < chunkLength; col++) {
            // Check whether to change direction (based on probability)
            if (col < chunkLength - 2 && testRandom(rand, p_change_path)) {
                // Determine which direction to change in
                int direction_change = 0;

                if (tunnel_row == 0) {
                    direction_change = 1;
                } else if (tunnel_row == Map.NUM_ROWS - 1) {
                    direction_change = -1;
                } else {
                    direction_change = (testRandom(rand, 0.5f) ? +1 : -1);
                }

                // Construct the change in the tunnel
                for (int i = 0; i < Map.NUM_ROWS; i++) {
                    if (i == tunnel_row || i == tunnel_row + direction_change) {
                        generated[i][col] = TileType.EMPTY;
                        generated[i][col + 1] = TileType.EMPTY;
                    } else {
                        generated[i][col] = TileType.OBSTACLE;
                        generated[i][col + 1] = TileType.OBSTACLE;
                    }
                }
                col++;
                tunnel_row += direction_change;
                p_change_path = 0.0f;
            } else {
                for (int i = 0; i < Map.NUM_ROWS; i++) {
                    if (i == tunnel_row) {
                        generated[i][col] = TileType.EMPTY;
                    } else {
                        generated[i][col] = TileType.OBSTACLE;
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
    public static TileType[][] generateAlienChunk(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 9;
        TileType[][] generated = generateEmpty(chunkLength);
        generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][4] = TileType.ALIEN;
        return generated;
    }

    // generates numAliens aliens. Each alien is preceded and succeeded by 8 empty tiles.
    public static TileType[][] generateAlienSwarmChunk(
            Random rand,
            double difficulty
    ) {
        int num_aliens = 5;
        int chunkLength = 8 * (num_aliens + 1);
        TileType[][] generated = generateEmpty(chunkLength);
        for (int a = 1; a <= num_aliens; a++) {
            generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][8 * a] = TileType.ALIEN;
        }
        return generated;
    }

    // generates a single asteroid at a random row index in a chunk of the given length. Asteroid is
    // placed randomly within the chunk
    public static TileType[][] generateAsteroidChunk(
            Random rand,
            double difficulty
    ) {
        int chunkLength = 7;
        TileType[][] generated = generateEmpty(chunkLength);
        generated[1 + rand.nextInt(Map.NUM_ROWS - 1)][rand.nextInt(7)] = TileType.ASTEROID;
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

    public static TileType[][] generateEmpty(int numCols) {
        TileType[][] empty = new TileType[Map.NUM_ROWS][numCols];
        // Fill with `EMPTY` tiles (default is NULL)
        for (TileType[] row : empty) {
            Arrays.fill(row, TileType.EMPTY);
        }
        return empty;
    }

    // generates a random float and checks if it is below given
    // probability. Returns true if it does. Used to decide whether
    // to generate certain types of obstacles given their probabilities
    // of occurring.
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
