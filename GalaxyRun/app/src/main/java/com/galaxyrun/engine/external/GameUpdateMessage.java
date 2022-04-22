package com.galaxyrun.engine.external;

import com.galaxyrun.engine.EventID;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.engine.audio.SoundID;
import com.galaxyrun.util.FastQueue;

/**
 * Stores data created by a game update.
 */

public class GameUpdateMessage {
    private FastQueue<DrawInstruction> drawInstructions;
    private FastQueue<EventID> events;
    private FastQueue<SoundID> sounds;
    public final long frameNumber;
    // FPS over the previous 100 frames
    public final double fps;

    public GameUpdateMessage(
            FastQueue<DrawInstruction> drawInstructions,
            FastQueue<EventID> events,
            FastQueue<SoundID> sounds,
            long frameNumber,
            double fps
    ) {
        this.drawInstructions = drawInstructions;
        this.events = events;
        this.sounds = sounds;
        this.frameNumber = frameNumber;
        this.fps = fps;
    }

    public FastQueue<DrawInstruction> getDrawInstructions() {
        return drawInstructions;
    }

    public FastQueue<EventID> getEvents() {
        return events;
    }

    public FastQueue<SoundID> getSounds() {
        return sounds;
    }
}