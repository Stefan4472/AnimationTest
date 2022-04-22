package com.galaxyrun.engine;

import android.view.MotionEvent;

/**
 * Interface for controlling the game externally (i.e., from the Activity).
 */

public interface IExternalGameController {
    void inputExternalStartGame();
    void inputExternalPauseGame();
    void inputExternalMotionEvent(MotionEvent e);
}
