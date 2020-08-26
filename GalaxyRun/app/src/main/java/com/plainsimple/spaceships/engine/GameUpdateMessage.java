package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.util.FastQueue;

/**
 * Stores data created by a game update.
 *
 * TODO: WOULD BE NICE TO HAVE A WAY TO RECYCLE THESE
 */

public class GameUpdateMessage {
    public FastQueue<DrawParams> drawParams;

    GameUpdateMessage() {
        drawParams = new FastQueue<>();
    }
}
