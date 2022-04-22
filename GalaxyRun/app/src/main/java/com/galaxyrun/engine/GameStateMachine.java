package com.galaxyrun.engine;

import com.galaxyrun.sprite.Spaceship;
import com.galaxyrun.sprite.SpriteState;

import static com.galaxyrun.engine.GameState.WAITING;
import static com.galaxyrun.engine.GameState.STARTING;
import static com.galaxyrun.engine.GameState.PLAYING;
import static com.galaxyrun.engine.GameState.DEAD;
import static com.galaxyrun.engine.GameState.FINISHED;

public class GameStateMachine {
    /*
    Determine which state the game should be in.
     */
    public static GameState calcState(GameContext gameContext, Spaceship player, GameState currState) {
        switch (currState) {
            case WAITING: {
                // WAITING can only be exited by input from GameEngine
                return WAITING;
            }
            case STARTING: {
                // Move to PLAYING once the player has reached the start position
                // (one quarter of the screen width)
                if (player.getX() >= gameContext.gameWidthPx / 4.0) {
                    return PLAYING;
                }
                return STARTING;
            }
            case PLAYING: {
                // Move to DEAD if the player sprite has died
                if (player.getState() != SpriteState.ALIVE) {
                    return DEAD;
                }
                return PLAYING;
            }
            case DEAD: {
                // Move to FINISHED once the player sprite is ready to be
                // terminated.
                // TODO: a different criterion--all sprites have left the screen
                if (player.getState() == SpriteState.TERMINATED) {
                    return FINISHED;
                }
                return DEAD;
            }
            case FINISHED: {
                // FINISHED can only be exited by input from GameEngine
                return FINISHED;
            }
            default: {
                throw new IllegalArgumentException("Unsupported GameState");
            }
        }
    }
}
