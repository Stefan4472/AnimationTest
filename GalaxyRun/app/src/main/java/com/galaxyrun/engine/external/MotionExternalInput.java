package com.galaxyrun.engine.external;

import android.view.MotionEvent;

public class MotionExternalInput extends ExternalInput {
    public final MotionEvent motion;
    public MotionExternalInput(MotionEvent motion) {
        this.motion = motion;
    }
}
