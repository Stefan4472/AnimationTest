package com.plainsimple.spaceships.view;

import android.view.MotionEvent;

/**
 * Event callbacks triggered by GameView.
 */

public interface IGameViewListener {
    void handleScreenTouch(MotionEvent motionEvent);
}
