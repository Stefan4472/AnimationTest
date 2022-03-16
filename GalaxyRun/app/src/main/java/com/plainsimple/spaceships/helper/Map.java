package com.plainsimple.spaceships.helper;

import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameTime;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.plainsimple.spaceships.helper.TileGenerator.NUM_ROWS;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.EMPTY;

/**
 * The Map class manages the creation of non-playing sprites on the screen.
 * Internally, it makes calls to TileGenerator. It generates one "chunk"
 * of non-playing sprites at a time.
 *
 * Types of "chunks":
 * - Empty: P(0.10)
 * - Obstacles: P(0.25)
 * - Tunnel: P(0.10)
 * - Alien: P(0.15)
 * - Alien Swarm: P(0.10)
 * - Asteroid: P(0.30)
 *
 * Each chunk has some leading "buffer" without any sprites, which gives
 * the player a chance to reset their position.
 *
 * TODO: improve generation
 */

public class Map {

    private final GameContext gameContext;
    private final Random random;

    private double difficulty;
    private double scrollSpeed;
    private double distanceFlown;

    // Grid of tile IDs specifying which sprites will be generated where.
    // A "tile-based map".
    private TileGenerator.TileID[][] tiles;
    // Width (px) of the side of a tile (equal to one-sixth of game height px)
    private final int tileWidth;
    // How many pixels past the right of the screen edge to spawn in new
    // chunks
    private final int spawnBeyondScreenPx;
    // X-coordinate at which the next chunk will be spawned
    private double nextSpawnAtX;
    // Set the number of columns of empty space before each created chunk
    private static final int LEADING_BUFFER_LENGTH = 3;

    // TODO: ALLOW PASSING IN A "SEED"?
    public Map(GameContext gameContext) {
        this.gameContext = gameContext;
        random = new Random();  // TODO: DO WE NEED TO INIT WITH TIME AS A SEED?
        tileWidth = gameContext.getGameHeightPx() / NUM_ROWS;
        spawnBeyondScreenPx = tileWidth;
        init();
    }

    private void init() {
        // Generate a short stretch of EMPTY
        tiles = TileGenerator.generateEmpty(5);
        nextSpawnAtX = tileWidth * 5;
        difficulty = calcDifficulty(0);
        scrollSpeed = calcScrollSpeed(difficulty);
        distanceFlown = 0;
    }

    public void restart() {
        init();
    }

    public double getDifficulty() {
        return difficulty;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public double getDistanceFlown() {
        return distanceFlown;
    }

    public void update(GameTime gameTime, ProtectedQueue<Sprite> createdSprites) {
        distanceFlown += scrollSpeed * (gameTime.msSincePrevUpdate / 1000.0);

        // We've scrolled far enough to spawn in the next chunk
        if (distanceFlown >= nextSpawnAtX) {
            Log.d("Map", "Time to spawn!");
            // Update difficulty and scroll speed
            difficulty = calcDifficulty(gameTime.runTimeMs);
            scrollSpeed = calcScrollSpeed(difficulty);
            Log.d("Map", String.format("Runtime is %f, difficult is %f, scrollSpeed is %f",
                    gameTime.msSincePrevUpdate / 1000.0, difficulty, scrollSpeed));
            // Generate the next chunk
            TileGenerator.ChunkType next_chunk_type = determineNextChunkType(difficulty);
            boolean should_generate_coins = determineGenerateCoins(difficulty);
            tiles = TileGenerator.generateChunk(
                    next_chunk_type,
                    LEADING_BUFFER_LENGTH,
                    difficulty,
                    should_generate_coins
            );
            Log.d("Map", String.format("Generated chunk of %s", next_chunk_type.toString()));
            Log.d("Map", TileGenerator.mapToString(tiles));

            // Calculate where to begin spawning in the new chunk
            long offset = (long) distanceFlown % tileWidth;
            double spawnX = gameContext.getGameWidthPx() + spawnBeyondScreenPx - offset;

            // Spawn in all non-empty tiles
            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    if (tiles[i][j] != EMPTY) {
                        createdSprites.push(createMapTile(
                                tiles[i][j],
                                spawnX + j * tileWidth,
                                i * tileWidth
                        ));
                    }
                }
            }

            nextSpawnAtX += tiles[0].length * tileWidth;
            Log.d("Map", String.format("nextSpawnAtX = %f", nextSpawnAtX));
        }
    }

    /*
    Calculate difficulty "magic number" based on how long the game
    has run. Difficulty is between 0 and 1.
     */
    private double calcDifficulty(long gameRuntimeMs) {
        double gametime_sec = gameRuntimeMs / 1000.0;
        double difficulty = (gametime_sec / 45.0) / (1.0 + gametime_sec / 45.0) + 0.1;
        if (difficulty > 1.0) {
            difficulty = 1.0;
        }
        return difficulty;
    }

    /*
    Calculates "scrollspeed" based on current scroll speed, game state,
    and difficulty. Return as pixels per second.
     */
    private double calcScrollSpeed(double difficulty) {
        return (0.43 * difficulty + 0.12) * gameContext.getGameWidthPx();
        // Spaceship destroyed: slow down scrolling to a halt.
//        if (currState == GameState.PLAYER_KILLED) {
//            return scrollSpeed / 1.03f;
//        } else { // Normal scrolling progression
//            //scrollSpeed = MAX_SCROLL_SPEED * Math.atan(difficulty / 500.0f) * 2 / Math.PI;
////            scrollSpeed = (float) (-Math.log(difficulty + 1) / 600);
//            return currDifficulty / 10;
//        }
    }

    /*
    Creates the given tile at the given coordinates and returns the created
    Sprite. Only supports TileIDs that generate a sprite.
     */
    private Sprite createMapTile(
            TileGenerator.TileID tileID,
            double x,
            double y
    ) throws IndexOutOfBoundsException {
        switch (tileID) {
            case ALIEN: {
                return gameContext.createAlien(x, y, difficulty);
            }
            case ASTEROID: {
                return gameContext.createAsteroid(x, y, difficulty);
            }
            case COIN: {
                return gameContext.createCoin(x, y);
            }
            case OBSTACLE: {
                return gameContext.createObstacle(x, y, tileWidth, tileWidth);
            }
            default: {
                throw new IllegalArgumentException(String.format(
                        "Unsupported tileID %s", tileID.toString())
                );
            }
        }
    }

    TileGenerator.ChunkType determineNextChunkType(double difficulty) {
        double random_decimal = random.nextDouble();
        if (random_decimal >= 0.7) {
            return TileGenerator.ChunkType.ASTEROID;
        } else if (random_decimal >= 0.6) {
            return TileGenerator.ChunkType.ALIEN_SWARM;
        } else if (random_decimal >= 0.45) {
            return TileGenerator.ChunkType.ALIEN;
        } else if (random_decimal >= 0.35) {
            return TileGenerator.ChunkType.TUNNEL;
        } else if (random_decimal >= 0.1) {
            return TileGenerator.ChunkType.OBSTACLES;
        } else {
            return TileGenerator.ChunkType.EMPTY;
        }
    }

    boolean determineGenerateCoins(double difficulty) {
        return (random.nextDouble() <= 0.3);
    }
}
