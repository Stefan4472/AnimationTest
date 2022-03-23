package com.plainsimple.spaceships.engine.map;

import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameTime;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * The Map class manages the creation of non-playing sprites on the screen.
 * Uses a `MapGenerator` to generate chunks of Tiles. The Map spawns sprites
 * based on the tiles.
 */

public class Map {

    private final GameContext gameContext;
    private MapGenerator mapGenerator;
    // Difficulty calculated for this chunk.
    // Re-calculated each time a new chunk is generated.
    private double chunkDifficulty;
    // The base "scroll speed" for obstacles in this chunk.
    // Scroll speed is calculated as a function of difficulty,
    // and is re-calculated for each new chunk.
    // Generated sprites do not need to hold themselves to the
    // scroll speed.
    private double chunkScrollSpeedPx;
    // Number of pixels scrolled in total
    private double numPixelsScrolled;
    // X-coordinate at which the next chunk will be spawned
    private double nextSpawnAtPx;

    // The number of rows of tiles the game has. Does not change.
    public static final int NUM_ROWS = 6;
    // Width (px) of the side of a tile (equal to one-sixth of game height px)
    private final int tileWidthPx;
    // How many pixels past the right of the screen edge to spawn in new
    // chunks
    private final int spawnBeyondScreenPx;

    public double getDifficulty() {
        return chunkDifficulty;
    }

    public double getScrollSpeed() {
        return chunkScrollSpeedPx;
    }

    public double getNumPixelsScrolled() {
        return numPixelsScrolled;
    }

    public Map(GameContext gameContext, long randomSeed) {
        this.gameContext = gameContext;
        tileWidthPx = gameContext.gameHeightPx / NUM_ROWS;
        spawnBeyondScreenPx = tileWidthPx;
        mapGenerator = new MapGenerator(randomSeed);
        nextSpawnAtPx = 0;
    }

    // TODO: would be really great to have access to UpdateContext here...
    public void update(GameTime gameTime, ProtectedQueue<Sprite> createdSprites) {
        numPixelsScrolled += chunkScrollSpeedPx * (gameTime.msSincePrevUpdate / 1000.0);

        // We've scrolled far enough to spawn in the next chunk
        if (numPixelsScrolled >= nextSpawnAtPx) {
            Log.d("Map", "Time to spawn! PxScrolled = " + numPixelsScrolled);
            // Update difficulty and scroll speed
            chunkDifficulty = calcDifficulty(gameTime.runTimeMs);
            chunkScrollSpeedPx = calcScrollSpeed(chunkDifficulty) * gameContext.gameWidthPx;
            Log.d("Map", String.format("Runtime is %f, difficult is %f, scrollSpeed is %f",
                    gameTime.runTimeMs / 1000.0, chunkDifficulty, chunkScrollSpeedPx));
            // Generate the next chunk
            Chunk currChunk = mapGenerator.generateChunk(chunkDifficulty);
            Log.d("Map", currChunk.toString());

            // Calculate where to begin spawning in the new chunk
            long offset = (long) numPixelsScrolled % tileWidthPx; // TODO: sure this shouldn't be a "+ offset"?
            double spawnX = gameContext.gameWidthPx + spawnBeyondScreenPx - offset;

            // Spawn in all non-empty tiles
            for (int i = 0; i < currChunk.numRows; i++) {
                for (int j = 0; j < currChunk.numCols; j++) {
                    if (currChunk.tiles[i][j] != TileType.EMPTY) {
                        createdSprites.push(createMapTile(
                                currChunk.tiles[i][j],
                                spawnX + j * tileWidthPx,
                                i * tileWidthPx
                        ));
                    }
                }
            }

            nextSpawnAtPx = numPixelsScrolled + currChunk.numCols * tileWidthPx;
            Log.d("Map", String.format("nextSpawnAtX = %f", nextSpawnAtPx));
        }
    }

    /*
    Calculate difficulty based on runtime of the game.
    Difficulty is between 0 and 1.
     */
    private static double calcDifficulty(long gameRuntimeMs) {
        // Each second of runtime = 0.01 points of difficulty
        double difficulty = (gameRuntimeMs / 1000.0) / 100.0;
        return Math.min(difficulty, 1.0);
    }

    /*
    Calculates scrollspeed based on current difficulty *as a fraction
    of the game width*.
     */
    private static double calcScrollSpeed(double difficulty) {
        return (0.43 * difficulty + 0.12);
    }

    /*
    Creates the given tile at the given coordinates and returns the created
    Sprite. Only supports TileIDs that generate a sprite.
     */
    private Sprite createMapTile(
            TileType tileType,
            double x,
            double y
    ) throws IndexOutOfBoundsException {
        switch (tileType) {
            case ALIEN: {
                return new Alien(x, y, chunkDifficulty, gameContext);
            }
            case ASTEROID: {
                return new Asteroid(x, y, chunkDifficulty, gameContext, chunkScrollSpeedPx);
            }
            case COIN: {
                return new Coin(x, y, gameContext);
            }
            case OBSTACLE: {
                return new Obstacle(x, y, tileWidthPx, tileWidthPx, gameContext);
            }
            default: {
                throw new IllegalArgumentException(String.format(
                        "Unsupported tileID %s", tileType.toString())
                );
            }
        }
    }
}
