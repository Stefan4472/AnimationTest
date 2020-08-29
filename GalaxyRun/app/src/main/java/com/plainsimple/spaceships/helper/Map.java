package com.plainsimple.spaceships.helper;

import android.util.Log;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;

import java.util.Random;

import static com.plainsimple.spaceships.helper.TileGenerator.NUM_ROWS;
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
    // Number of tiles elapsed since last tiles was generated
    private int mapTileCounter;
    // Keeps track of the tile spaceship was on the previous update
    private int prevTile;
    // width (px) of the side of a tile
    private int tileWidth;
    // Set the number of columns of empty space before each created chunk
    private static final int LEADING_BUFFER_LENGTH = 3;


    // TODO: ALLOW PASSING IN A "SEED"?
    public Map(GameContext gameContext) {
        this.gameContext = gameContext;
        random = new Random();  // TODO: DO WE NEED TO INIT WITH TIME AS A SEED?
        tileWidth = gameContext.getGameHeightPx() / NUM_ROWS;
        init();
    }

    public void restart() {
        mapTileCounter = 0;
        prevTile = 0;
        init();
    }

    private void init() {
        // Generate a short stretch of EMPTY
        tiles = TileGenerator.generateEmpty(5);
    }

    public void update(
            UpdateContext updateContext,
            double scrollDistance
    ) {
//        Log.d("Map", String.format("Update called with scrolldist %f", scrollDistance));
        int curr_tile = getWTile(scrollDistance);
        // Check if screen has progressed far enough to render the next column of tiles
        if (curr_tile != prevTile) {
            // Add any non-empty tiles in the current column to the edge of the screen
            for (int i = 0; i < tiles.length; i++) {
                // process for adding Obstacles: count the number of adjacent obstacles in the row.
                // Set them all to EMPTY in the array. Construct the obstacle using this data
                if (tiles[i][mapTileCounter] == OBSTACLE) {
                    int num_cols = 0;
                    for (int col = mapTileCounter; col < tiles[0].length && tiles[i][col] == OBSTACLE; col++) {
                        num_cols++;
                        tiles[i][col] = TileGenerator.TileID.EMPTY;
                    }
                    updateContext.createEvent(EventID.SPRITE_SPAWNED);
                    updateContext.registerChild(gameContext.createObstacle(
                            gameContext.getGameWidthPx() + getWOffset(scrollDistance),
                            i * tileWidth,
                            num_cols * tileWidth,
                            tileWidth
                    ));
                } else if (tiles[i][mapTileCounter] != TileGenerator.TileID.EMPTY) {
                    addMapTile(
                            tiles[i][mapTileCounter],
                            gameContext.getGameWidthPx() + getWOffset(scrollDistance),
                            i * tileWidth,
                            updateContext
                    );
                }
            }
            mapTileCounter++;

            // Generate more sprites if we've reached the end of this set
            if (mapTileCounter == tiles[0].length) {
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
                mapTileCounter = 0;
                Log.d("Map", String.format("Generated chunk of %s", next_chunk_type.toString()));
                Log.d("Map", TileGenerator.mapToString(tiles));
            }
            prevTile = curr_tile;
        }
    }

    // current horizontal tile
    private int getWTile(double scrollDistance) {
        return ((int) scrollDistance) / tileWidth;
    }

    // number of pixels from start of current tile
    private int getWOffset(double scrollDistance) {
        return ((int) scrollDistance) % tileWidth;
    }

    // initializes sprite and adds to the proper list, given parameters
    private void addMapTile(
            TileGenerator.TileID tileID,
            double x,
            double y,
            UpdateContext updateContext
    ) throws IndexOutOfBoundsException {
        switch (tileID) {
            case COIN: {
                updateContext.registerChild(gameContext.createCoin(
                        x,
                        y
                ));
                updateContext.createEvent(EventID.SPRITE_SPAWNED);
                break;
            }
            case ALIEN: {
                updateContext.registerChild(gameContext.createAlien(
                        x,
                        y,
                        updateContext.getDifficulty()
                ));
                updateContext.createEvent(EventID.SPRITE_SPAWNED);
                break;
            }
            case ASTEROID: {
                updateContext.registerChild(gameContext.createAsteroid(
                        x,
                        y,
                        updateContext.getDifficulty()
                ));
                updateContext.createEvent(EventID.SPRITE_SPAWNED);
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
        Log.d("Map", String.format("%f", random_decimal));
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
