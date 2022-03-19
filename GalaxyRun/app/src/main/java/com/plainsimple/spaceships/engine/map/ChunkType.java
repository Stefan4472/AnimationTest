package com.plainsimple.spaceships.engine.map;

/*
Types of possible "chunks" that can be generated in the Map.

TODO: ADD ASTEROID_FIELD CHUNK TYPE
 */
public enum ChunkType {
    EMPTY,
    OBSTACLES,
    TUNNEL,
    ALIEN,
    ALIEN_SWARM,
    ASTEROID,
}
