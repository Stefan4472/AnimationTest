package com.plainsimple.spaceships.engine.ui;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.audio.SoundID;
import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class UIElement {
    protected final GameContext gameContext;
    protected final Rectangle bounds;
    protected Queue<UIInputId> createdInput;
    protected Queue<SoundID> createdSounds;
    protected boolean isVisible = true;
    protected boolean isTouchable = true;

    public boolean getIsVisible() {
        return isVisible;
    }

    public boolean getIsTouchable() {
        return isTouchable;
    }

    public UIElement(GameContext gameContext, Rectangle bounds) {
        this.gameContext = gameContext;
        this.bounds = bounds;
        createdInput = new ArrayDeque<>();
        createdSounds = new ArrayDeque<>();
    }

    public boolean isInBounds(float x, float y) {
        return bounds.isInBounds(x, y);
    }

    public abstract void update(UpdateContext updateContext);

    public abstract void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions);

    public abstract void onTouchEnter(float x, float y);

    public abstract void onTouchMove(float x, float y);

    public abstract void onTouchLeave(float x, float y);

    public void pollAllInputs(Queue<UIInputId> input) {
        input.addAll(createdInput);
        createdInput.clear();
    }

    public void pollAllSounds(Queue<SoundID> sounds) {
        sounds.addAll(createdSounds);
        createdSounds.clear();
    }
}
