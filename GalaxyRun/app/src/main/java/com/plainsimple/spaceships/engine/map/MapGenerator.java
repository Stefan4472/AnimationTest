package com.plainsimple.spaceships.engine.map;

import android.util.Log;

import com.plainsimple.spaceships.util.WeightedRandomChooser;

import java.util.Random;

public class MapGenerator {
    private final Random random;

    // Set the number of columns of empty space before each created chunk
    private static final int LEADING_BUFFER_LENGTH = 3;

    public MapGenerator(long seed) {
        random = new Random(seed);
    }

    public TileType[][] generateNextChunk(double difficulty) {
        Log.d("MapGenerator", "Generating chunk with difficulty " + difficulty);
        // Generate five columns of EMPTY to start the game
        // TODO: is this necessary?
        if (difficulty == 0) {
            Log.d("MapGenerator", "Generating empty chunk for game start");
            return TileGenerator.generateEmpty(5);
        }

        // Generate the next chunk
        ChunkType nextChunkType = decideChunkType(difficulty);
        boolean shouldGenerateCoins = decideGenerateCoins(difficulty);
        Log.d("Map", "Generating a chunk of " + nextChunkType.name());
        Log.d("Map", "ShouldGenerateCoins = " + shouldGenerateCoins);
        return TileGenerator.generateChunk(
                nextChunkType,
                LEADING_BUFFER_LENGTH,
                difficulty,
                shouldGenerateCoins
        );
    }

    private ChunkType decideChunkType(double difficulty) {
        WeightedRandomChooser<ChunkType> chooser = new WeightedRandomChooser<>(random);
        for (ChunkType chunkType : ChunkType.values()) {
            double chunkProb = ChunkProbabilities.getProbability(chunkType, difficulty);
            chooser.addItem(chunkType, chunkProb);
        }
        return chooser.choose();
    }

    private boolean decideGenerateCoins(double difficulty) {
        return (random.nextDouble() <= 0.3);
    }
}
