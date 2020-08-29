package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.util.FastQueue;

/**
 * Stores data created by a game update.
 *
 * TODO: WOULD BE NICE TO HAVE A WAY TO RECYCLE THESE
 */

public class GameUpdateMessage {
    private FastQueue<DrawParams> drawParams;
    private FastQueue<EventID> events;
    private FastQueue<SoundID> sounds;
    // TODO: PROVIDE SCORE, HEALTH, AND SCROLLSPEED


    public GameUpdateMessage() {
        this.drawParams = new FastQueue<>();
        this.events = new FastQueue<>();
        this.sounds = new FastQueue<>();
    }

    public GameUpdateMessage(
            FastQueue<DrawParams> drawParams,
            FastQueue<EventID> events,
            FastQueue<SoundID> sounds
    ) {
        this.drawParams = drawParams;
        this.events = events;
        this.sounds = sounds;
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