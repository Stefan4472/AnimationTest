package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Passed to Sprites to update themselves. Meant for internal
 * GameEngine use only!
 */

public class UpdateContext {
    public final GameTime gameTime;
    public final GameState gameState;
    public final double difficulty;
    public final double scrollSpeed;
    public final int score;
    public final int playerHealth;
    public final boolean isPaused;
    public final boolean isMuted;
    public final Spaceship.Direction playerDirection;
    public final Sprite playerSprite;
    private ProtectedQueue<Sprite> createdChildren;
    private ProtectedQueue<EventID> createdEvents;
    private ProtectedQueue<SoundID> createdSounds;

    public UpdateContext(
            GameTime gameTime,
            GameState gameState,
            double difficulty,
            double scrollSpeed,
            int score,
            int playerHealth,
            boolean isPaused,
            boolean isMuted,
            Spaceship.Direction playerDirection,
            Sprite playerSprite,
            ProtectedQueue<Sprite> createdChildren,
            ProtectedQueue<EventID> createdEvents,
            ProtectedQueue<SoundID> createdSounds
    ) {
        this.gameTime = gameTime;
        this.gameState = gameState;
        this.difficulty = difficulty;
        this.score = score;
        this.playerHealth = playerHealth;
        this.scrollSpeed = scrollSpeed;
        this.isPaused = isPaused;
        this.isMuted = isMuted;
        this.playerDirection = playerDirection;
        this.playerSprite = playerSprite;
        this.createdChildren = createdChildren;
        this.createdEvents = createdEvents;
        this.createdSounds = createdSounds;
    }

    public GameTime getGameTime() {
        return gameTime;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void registerChild(Sprite childSprite) {
        createdChildren.push(childSprite);
        createEvent(EventID.SPRITE_SPAWNED);

    }

    public void createEvent(EventID eventID) {
        createdEvents.push(eventID);
    }

    public void createSound(SoundID soundID) {
        createdSounds.push(soundID);
    }
}
