package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameState;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.util.Pair;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/*
Manage in-GameEngine user interface.

The UI generally uses the full screen dimensions, while the game
uses the game dimensions. This is because the HealthBar takes up
part of the bottom of the screen.
 */
public class GameUI {

    private GameContext gameContext;

    /* UI Elements */
    private final HealthBar healthBar;
    private final ScoreDisplay scoreDisplay;
    private final PauseButton pauseButton;
    private final MuteButton muteButton;
    private final Controls controls;

    // TODO: a better way of doing this
    private boolean isGameOver;
    private boolean isPaused;

    // TODO: ProtectedQueue?
    private Queue<UIInputId> createdInput = new ArrayDeque<>();

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
//        Log.d("GameUI", String.format("Processing motion %s", e.toString()));
        if (healthBar.handleEvent(e, createdInput)) {
            return;
        }
        if (scoreDisplay.handleEvent(e, createdInput)) {
            return;
        }
        if (pauseButton.handleEvent(e, createdInput)) {
            return;
        }
        if (muteButton.handleEvent(e, createdInput)) {
            return;
        }
        if (controls.handleEvent(e, createdInput)) {
            return;
        }

        // Events not processed by other elements get registered as shooting
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createdInput.add(UIInputId.START_SHOOTING);
                break;
            case MotionEvent.ACTION_UP:
                createdInput.add(UIInputId.STOP_SHOOTING);
                break;
        }

        // Restart game on button press when game is over
        if (isGameOver && e.getAction() == MotionEvent.ACTION_DOWN) {
            createdInput.add(UIInputId.RESTART_GAME);
        }
    }

    public List<UIInputId> pollAllInput() {
        List<UIInputId> copied = new ArrayList<>();
        while (!createdInput.isEmpty()) {
            copied.add(createdInput.poll());
        }
        return copied;
    }

    public void update(UpdateContext updateContext) {
        healthBar.update(updateContext);
        scoreDisplay.update(updateContext);
        pauseButton.update(updateContext);
        muteButton.update(updateContext);
        controls.update(updateContext);
        isGameOver = (updateContext.gameState == GameState.FINISHED);
        isPaused = updateContext.isPaused;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        healthBar.getDrawParams(drawParams);
        scoreDisplay.getDrawParams(drawParams);
        pauseButton.getDrawParams(drawParams);
        muteButton.getDrawParams(drawParams);
        controls.getDrawParams(drawParams);

        // TODO: an actual dialog
        if (isGameOver) {
            drawParams.push(new DrawText(
                    "GAME OVER",
                    gameContext.gameWidthPx / 2.0f,
                    gameContext.gameHeightPx / 2.0f,
                    Color.YELLOW,
                    70
            ));
        }
        if (isPaused) {
            drawParams.push(new DrawText(
                    "PAUSED",
                    gameContext.gameWidthPx / 2.0f,
                    gameContext.gameHeightPx / 2.0f,
                    Color.YELLOW,
                    70
            ));
        }
    }

    public static Pair<Integer, Integer> calcGameDimensions(
            int screenWidthPx,
            int screenHeightPx) {
        return new Pair<>(screenWidthPx, (int) (screenHeightPx * 0.94));
    }
}
