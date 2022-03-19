package com.plainsimple.spaceships.engine.map;

import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameTime;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

import static com.plainsimple.spaceships.engine.map.TileGenerator.NUM_ROWS;

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
    // Grid of tiles specifying which sprites will be generated in the current chunk.
    // A "tile-based map".
    private TileType[][] currTiles;

    // Width (px) of the side of a tile (equal to one-sixth of game height px)
    private final int tileWidth;
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
        tileWidth = gameContext.gameHeightPx / NUM_ROWS;
        spawnBeyondScreenPx = tileWidth;
        mapGenerator = new MapGenerator(randomSeed);
        nextSpawnAtPx = 0;
    }

    public void update(GameTime gameTime, ProtectedQueue<Sprite> createdSprites) {
        numPixelsScrolled += chunkScrollSpeedPx * (gameTime.msSincePrevUpdate / 1000.0);

        // We've scrolled far enough to spawn in the next chunk
        // TODO: there is something fishy going on here. It seems like one chunk may
        // spawn on top of another chunk, or at least that some overlap is possible
        if (numPixelsScrolled >= nextSpawnAtPx) {
            Log.d("Map", "Time to spawn! PxScrolled = " + numPixelsScrolled);
            // Update difficulty and scroll speed
            chunkDifficulty = calcDifficulty(gameTime.runTimeMs);
            chunkScrollSpeedPx = calcScrollSpeed(chunkDifficulty) * gameContext.gameWidthPx;
            Log.d("Map", String.format("Runtime is %f, difficult is %f, scrollSpeed is %f",
                    gameTime.runTimeMs / 1000.0, chunkDifficulty, chunkScrollSpeedPx));
            // Generate the next chunk
            currTiles = mapGenerator.generateNextChunk(chunkDifficulty);
            Log.d("Map", TileGenerator.chunkToString(currTiles));

            // Calculate where to begin spawning in the new chunk
            long offset = (long) numPixelsScrolled % tileWidth;
            double spawnX = gameContext.gameWidthPx + spawnBeyondScreenPx - offset;

            // Spawn in all non-empty tiles
            for (int i = 0; i < currTiles.length; i++) {
                for (int j = 0; j < currTiles[i].length; j++) {
                    if (currTiles[i][j] != TileType.EMPTY) {
                        createdSprites.push(createMapTile(
                                currTiles[i][j],
                                spawnX + j * tileWidth,
                                i * tileWidth
                        ));
                    }
                }
            }

            nextSpawnAtPx += currTiles[0].length * tileWidth;
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
                return gameContext.createAlien(x, y, chunkDifficulty);
            }
            case ASTEROID: {
                return gameContext.createAsteroid(x, y, chunkDifficulty);
            }
            case COIN: {
                return gameContext.createCoin(x, y);
            }
            case OBSTACLE: {
                return gameContext.createObstacle(x, y, tileWidth, tileWidth);
            }
            default: {
                throw new IllegalArgumentException(String.format(
                        "Unsupported tileID %s", tileType.toString())
                );
            }
        }
    }
}
