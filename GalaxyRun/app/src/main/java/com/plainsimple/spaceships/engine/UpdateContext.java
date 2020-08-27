package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Passed to Sprites to update themselves. Meant for internal
 * GameEngine use only!
 * TODO: NOT SURE IF THIS IS GOING TO BE CONTINUED.
 */

public class UpdateContext {
    private int msSincePrevUpdate;
    private double difficulty;
    // TODO: MAKE PRIVATE AND ADD `ADD()` METHODS FOR EACH
    public ProtectedQueue<Sprite> createdChildren;
    public ProtectedQueue<EventID> createdEvents;
    public ProtectedQueue<SoundID> createdSounds;

    public UpdateContext(
            int msSincePrevUpdate,
            double difficulty,
            ProtectedQueue<Sprite> createdChildren,
            ProtectedQueue<EventID> createdEvents,
            ProtectedQueue<SoundID> createdSounds
    ) {
        this.msSincePrevUpdate = msSincePrevUpdate;
        this.difficulty = difficulty;
        this.createdChildren = createdChildren;
        this.createdEvents = createdEvents;
        this.createdSounds = createdSounds;
    }

    public int getMsSincePrevUpdate() {
        return msSincePrevUpdate;
    }

    public double getDifficulty() {
        return difficulty;
    }
}
