package com.plainsimple.spaceships.engine.ui;

import android.view.MotionEvent;

import java.util.Queue;

public abstract class UIElement {
    public abstract boolean handleEvent(MotionEvent e, Queue<UIInputId> createdInput);
}
