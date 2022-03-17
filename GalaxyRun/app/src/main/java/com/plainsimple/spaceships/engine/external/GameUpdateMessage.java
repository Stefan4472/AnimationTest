package com.plainsimple.spaceships.engine.external;

import com.plainsimple.spaceships.engine.EventID;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.util.FastQueue;

/**
 * Stores data created by a game update.
 */

public class GameUpdateMessage {
    private FastQueue<DrawParams> drawParams;
    private FastQueue<EventID> events;
    private FastQueue<SoundID> sounds;
    // FPS over the previous 100 frames
    public final double fps;

    public GameUpdateMessage(
            FastQueue<DrawParams> drawParams,
            FastQueue<EventID> events,
            FastQueue<SoundID> sounds,
            double fps
    ) {
        this.drawParams = drawParams;
        this.events = events;
        this.sounds = sounds;
        this.fps = fps;
    }

    public FastQueue<DrawParams> getDrawParams() {
        return drawParams;
    }

    public FastQueue<EventID> getEvents() {
        return events;
    }

    public FastQueue<SoundID> getSounds() {
        return sounds;
    }
}