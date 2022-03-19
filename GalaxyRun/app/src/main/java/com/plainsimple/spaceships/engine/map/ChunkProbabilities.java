package com.plainsimple.spaceships.engine.map;

/*
Given the current difficulty, return the probability of
each ChunkType being generated.
 */
public class ChunkProbabilities {
    /*
    Return the probability of `ChunkType` at the given `difficulty`.
     */
    public static double getProbability(ChunkType chunkType, double difficulty) {
        switch (chunkType) {
            case EMPTY:
                return getProbabilityOfEmpty(difficulty);
            case OBSTACLES:
                return getProbabilityOfObstacles(difficulty);
            case TUNNEL:
                return getProbabilityOfTunnel(difficulty);
            case ALIEN:
                return getProbabilityOfAlien(difficulty);
            case ALIEN_SWARM:
                return getProbabilityOfAlienSwarm(difficulty);
            case ASTEROID:
                return getProbabilityOfAsteroid(difficulty);
            default:
                throw new IllegalArgumentException("Unsupported ChunkType");
        }
    }

    // TODO: functions based on difficulty
    public static double getProbabilityOfEmpty(double difficulty) {
        return 0.0;
    }

    public static double getProbabilityOfObstacles(double difficulty) {
        return 0.5;
    }

    public static double getProbabilityOfTunnel(double difficulty) {
        return 0.5;
    }

    public static double getProbabilityOfAlien(double difficulty) {
        return 0.0;
    }

    public static double getProbabilityOfAlienSwarm(double difficulty) {
        return 0.0;
    }

    public static double getProbabilityOfAsteroid(double difficulty) {
        return 0.0;
    }
}
