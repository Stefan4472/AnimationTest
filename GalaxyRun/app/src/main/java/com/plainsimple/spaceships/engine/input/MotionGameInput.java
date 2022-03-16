package com.plainsimple.spaceships.engine.input;

import android.view.MotionEvent;

public class MotionGameInput extends GameInput {
    public final MotionEvent motion;
    public MotionGameInput(MotionEvent motion) {
        this.motion = motion;
    }
}
