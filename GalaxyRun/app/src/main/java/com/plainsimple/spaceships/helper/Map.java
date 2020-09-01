package com.plainsimple.spaceships.helper;

import android.util.Log;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;

import java.util.Random;

import static com.plainsimple.spaceships.helper.TileGenerator.NUM_ROWS;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.EMPTY;
import static com.plainsimple.spaceships.helper.TileGenerator.TileID.OBSTACLE;

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
 */

public class Map {

    private GameContext gameContext;
    private Random random;

    // Grid of tile IDs specifying which sprites will be generated where.
    // A "tile-based map".
    private TileGenerator.TileID[][] tiles;
    // Width (px) of the side of a tile (equal to one-sixth of game height px)
    private int tileWidth;
    // Set the number of columns of empty space before each created chunk
    private static final int LEADING_BUFFER_LENGTH = 3;
    // How many pixels past the right of the screen edge to spawn in new
    // chunks
    private int spawnBeyondScreenPx;
    // X-coordinate at which the next chunk will be spawned
    private double nextSpawnAtX;


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
    }

    public void restart() {
        init();
    }

    public void update(
            UpdateContext updateContext,
            double scrollDistance
    ) {
        // We've scrolled far enough to spawn in the next chunk
        if (scrollDistance >= nextSpawnAtX) {
            // Generate the next chunk
            TileGenerator.ChunkType next_chunk_type =
                    determineNextChunkType(updateContext.getDifficulty());
            boolean should_generate_coins =
                    determineGenerateCoins(updateContext.getDifficulty());
            tiles = TileGenerator.generateChunk(
                    next_chunk_type,
                    LEADING_BUFFER_LENGTH,
                    updateContext.getDifficulty(),
                    should_generate_coins
            );
            Log.d("Map", String.format("Generated chunk of %s", next_chunk_type.toString()));
            Log.d("Map", TileGenerator.mapToString(tiles));

            // Calculate where to begin spawning in the new chunk
            long offset = (long) scrollDistance % tileWidth;
            double spawn_x = gameContext.getGameWidthPx() + spawnBeyondScreenPx - offset;

            // Spawn in all non-empty tiles
            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    if (tiles[i][j] != EMPTY) {
                        addMapTile(
                                tiles[i][j],
                                spawn_x + j * tileWidth,
                                i * tileWidth,
                                updateContext
                        );
                    }
                }
            }

            nextSpawnAtX += tiles[0].length * tileWidth;
            Log.d("Map", String.format("nextSpawnAtX = %f", nextSpawnAtX));
        }
    }

    // Initializes sprite at given coordinates
    private void addMapTile(
            TileGenerator.TileID tileID,
            double x,
            double y,
            UpdateContext updateContext
    ) throws IndexOutOfBoundsException {
//        Log.d("Map", String.format("Adding tile at %f, %f", x, y));
        switch (tileID) {
            case ALIEN: {
                updateContext.registerChild(gameContext.createAlien(
                        x,
                        y,
                        updateContext.getDifficulty()
                ));
                break;
            }
            case ASTEROID: {
                updateContext.registerChild(gameContext.createAsteroid(
                        x,
                        y,
                        updateContext.getDifficulty()
                ));
                break;
            }
            case COIN: {
                updateContext.registerChild(gameContext.createCoin(
                        x,
                        y
                ));
                break;
            }
            case OBSTACLE: {
                updateContext.registerChild(gameContext.createObstacle(
                        x,
                        y,
                        tileWidth,
                        tileWidth
                ));
                break;
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
