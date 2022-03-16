package com.plainsimple.spaceships.engine.ui;

import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/*
Manage in-GameEngine user interface.
 */
public class GameUI {

    private GameContext gameContext;

    /* UI Elements */
    private final HealthBar healthBar;
    private final ScoreDisplay scoreDisplay;
    private final PauseButton pauseButton;
    private final MuteButton muteButton;
    private final Controls controls;

    private Queue<UIInputId> createdInput = new LinkedBlockingQueue<>();

    // TODO: how to reset the UI? (e.g., on game restart?)
    public GameUI(GameContext gameContext) {
        this.gameContext = gameContext;
        healthBar = new HealthBar(gameContext);
        scoreDisplay = new ScoreDisplay(gameContext);
        pauseButton = new PauseButton(gameContext);
        muteButton = new MuteButton(gameContext);
        controls = new Controls(gameContext);
    }

    public void handleMotionEvent(MotionEvent e) {
        Log.d("GameUI", String.format("Processing motion %s", e.toString()));
        // TODO: offer to other elements first

        // Events not processed by other elements get registered as shooting
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createdInput.add(UIInputId.START_SHOOTING);
                break;
            case MotionEvent.ACTION_UP:
                createdInput.add(UIInputId.STOP_SHOOTING);
                break;
        }
    }

    public List<UIInputId> pollAllInput() {
        List<UIInputId> input = new ArrayList<>();
        while (!createdInput.isEmpty()) {
            input.add(createdInput.poll());
        }
        return input;
    }

    public void update(UpdateContext updateContext) {
        healthBar.update(updateContext);
        scoreDisplay.update(updateContext);
        pauseButton.update(updateContext);
        muteButton.update(updateContext);
        controls.update(updateContext);
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        healthBar.getDrawParams(drawParams);
        scoreDisplay.getDrawParams(drawParams);
        pauseButton.getDrawParams(drawParams);
        muteButton.getDrawParams(drawParams);
        controls.getDrawParams(drawParams);
    }
}
