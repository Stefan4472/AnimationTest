package com.plainsimple.spaceships.engine;

/**
 * "Vehicle" for inputting events into the GameEngine. Currently
 * we just use enums, but this may need to be made more complex
 * in the future.
 */

public class GameInput {
    public enum InputID {
        START_SHOOTING,
        STOP_SHOOTING,
        START_MOVING_UP,
        START_MOVING_DOWN,
        STOP_MOVING,
        START_GAME,
        PAUSE_GAME,
        RESUME_GAME,
        RESTART_GAME
    }
}
