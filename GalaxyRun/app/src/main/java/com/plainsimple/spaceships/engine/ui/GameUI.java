package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameState;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.util.Pair;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import androidx.core.view.MotionEventCompat;

/*
Manage in-GameEngine user interface.

The UI generally uses the full screen dimensions, while the game
uses the game dimensions. This is because the HealthBar takes up
part of the bottom of the screen.
 */
public class GameUI {

    private final GameContext gameContext;

    private HashMap<Integer, Touch> currTouches;

    /* UI Elements */
    private final UIElement[] uiElements;
    private final HealthBar healthBar;
    private final ScoreDisplay scoreDisplay;
    private final PauseButton pauseButton;
    private final MuteButton muteButton;
    private final Controls controls;

    // TODO: a better way of doing this
    private boolean isGameOver;
    private boolean isPaused;

    // TODO: how to reset the UI? (e.g., on game restart?)
    public GameUI(GameContext gameContext) {
        this.gameContext = gameContext;
        currTouches = new HashMap<>();
        healthBar = new HealthBar(gameContext);
        scoreDisplay = new ScoreDisplay(gameContext);
        pauseButton = new PauseButton(gameContext);
        muteButton = new MuteButton(gameContext);
        controls = new Controls(gameContext);

        uiElements = new UIElement[] {
            healthBar,
            scoreDisplay,
            pauseButton,
            muteButton,
            controls,
        };
    }

    private enum MyTouchEvent {
        DOWN,
        MOVE,
        UP,
        CANCEL
    }

    public void handleMotionEvent(MotionEvent e) {
        // Read the docs: https://developer.android.com/reference/android/view/MotionEvent.html
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                assert(e.getPointerCount() == 1);
                int pointerId = e.getPointerId(0);
                updateTouchState(pointerId, MyTouchEvent.DOWN, e.getX(), e.getY());
                break;
            }
            case MotionEvent.ACTION_UP: {
                assert(e.getPointerCount() == 1);
                int pointerId = e.getPointerId(0);
                updateTouchState(pointerId, MyTouchEvent.UP, e.getX(), e.getY());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < e.getPointerCount(); i++) {
                    int pointerId = e.getPointerId(i);
                    int pointerIndex = e.findPointerIndex(pointerId);
                    float x = e.getX(pointerIndex);
                    float y = e.getY(pointerIndex);
                    updateTouchState(pointerId, MyTouchEvent.MOVE, x, y);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
//                assert(e.getPointerCount() == 1);
                int pointerId = e.getPointerId(0);
                updateTouchState(pointerId, MyTouchEvent.DOWN, e.getX(), e.getY());
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
//                assert(e.getPointerCount() == 1);
                int pointerId = e.getPointerId(0);
                updateTouchState(pointerId, MyTouchEvent.UP, e.getX(), e.getY());
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                updateTouchState(e.getPointerId(0), MyTouchEvent.CANCEL, e.getX(), e.getY());
                break;
            }
            default: {
                int action = e.getActionMasked();
                int index = e.getActionIndex();
                Log.d("GameUI", index + " " + action + " " + e.getPointerCount());
            }
        }
//
//        // Events not processed by other elements get registered as shooting
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                createdInput.add(UIInputId.START_SHOOTING);
//                break;
//            case MotionEvent.ACTION_UP:
//                createdInput.add(UIInputId.STOP_SHOOTING);
//                break;
//        }
//
//        // Restart game on button press when game is over
//        if (isGameOver && e.getAction() == MotionEvent.ACTION_DOWN) {
//            createdInput.add(UIInputId.RESTART_GAME);
//        }
    }

    private void updateTouchState(int pointerId, MyTouchEvent event, float x, float y) {
//        Log.d("GameUI", "updateTouchState(" + pointerId + ") = " + event.name() + " at " + x + ", " + y);
        // This is unfortunately complicated by the fact that the MotionEvents we get
        // from Android aren't always well-formed. Theoretically, there should always
        // be a MOTION_DOWN, followed by some number of MOTION_MOVE, followed by a single
        // MOTION_UP. This is not always the case!
        boolean alreadyExists = currTouches.containsKey(pointerId);
        UIElement touchedElement = getElementAt(x, y);

        switch (event) {
            case DOWN: {
                if (alreadyExists) {
                    // Ignore
                    Log.w("GameUI", "Got DOWN but alreadyExists = true");
                } else {
                    if (touchedElement == null) {
                        currTouches.put(pointerId, new Touch(null));
                    } else {
                        touchedElement.onTouchEnter(x, y);
                        currTouches.put(pointerId, new Touch(touchedElement));
                    }
                }
                break;
            }
            case MOVE: {
                if (alreadyExists) {
                    Touch currTouch = currTouches.get(pointerId);
                    if (touchedElement == currTouch.touchedElement && touchedElement != null) {
                        currTouch.touchedElement.onTouchMove(x, y);
                    } else {
                        if (currTouch.touchedElement != null) {
                            currTouch.touchedElement.onTouchLeave(x, y);
                        }
                        currTouch.touchedElement = touchedElement;
                        if (touchedElement != null) {
                            touchedElement.onTouchEnter(x, y);
                        }
                    }
                } else {
                    if (touchedElement == null) {
                        currTouches.put(pointerId, new Touch(null));
                    } else {
                        touchedElement.onTouchEnter(x, y);
                        currTouches.put(pointerId, new Touch(touchedElement));
                    }
                    Log.w("GameUI", "Got MOVE but alreadyExists = false");
                }
                break;
            }
            case UP: {
                if (alreadyExists) {
                    Touch currTouch = currTouches.get(pointerId);
                    if (currTouch.touchedElement != null) {
                        currTouch.touchedElement.onTouchLeave(x, y);
                    }
                    currTouches.remove(pointerId);
                } else {
                    Log.w("GameUI", "Got UP but alreadyExists = false");
                }
                break;
            }
            case CANCEL: {
                if (alreadyExists) {
                    Touch currTouch = currTouches.get(pointerId);
                    if (currTouch != null) {
                        currTouch.touchedElement.onTouchLeave(x, y);
                    }
                    currTouches.remove(pointerId);
                } else {
                    Log.w("GameUI", "Got CANCEL but alreadyExists = false");
                }
                break;
            }
        }
    }

    private UIElement getElementAt(float x, float y) {
        for (UIElement elem : uiElements) {
            if (elem.isInBounds(x, y)) {
                return elem;
            }
        }
        return null;
    }

    public Queue<UIInputId> pollAllInput() {
        Queue<UIInputId> createdInput = new ArrayDeque<>();
        for (UIElement elem : uiElements) {
            elem.pollAllInputs(createdInput);
        }
        return createdInput;
    }

    public void update(UpdateContext updateContext) {
        for (UIElement elem : uiElements) {
            elem.update(updateContext);
        }

        isGameOver = (updateContext.gameState == GameState.FINISHED);
        isPaused = updateContext.isPaused;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        for (UIElement elem : uiElements) {
            elem.getDrawParams(drawParams);
            // Draw bounds TODO: debugging flag
            DrawRect bounds = new DrawRect(Color.GREEN, Paint.Style.STROKE, 2.0f);
            bounds.setBounds(elem.bounds);
            drawParams.push(bounds);
        }

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
