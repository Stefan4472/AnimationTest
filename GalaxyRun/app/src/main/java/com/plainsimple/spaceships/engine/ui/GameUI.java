package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.GameState;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.util.Pair;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

/*
Manage in-GameEngine user interface.

The UI generally uses the full screen dimensions, while the game
uses the game dimensions. This is because the HealthBar takes up
part of the bottom of the screen.
 */
public class GameUI {

    private final GameContext gameContext;
    // Note: order of elements is also the order that touches are checked.
    // Elements are drawn in reverse order.
    private final UIElement[] uiElements;
    private HashMap<Integer, Touch> currTouches;

    private GameOverOverlay gameoverOverlay;
    private PauseOverlay pauseOverlay;

    // TODO: how to reset the UI? (e.g., on game restart?)
    public GameUI(GameContext gameContext) {
        this.gameContext = gameContext;
        currTouches = new HashMap<>();

        gameoverOverlay = new GameOverOverlay(gameContext);
        pauseOverlay = new PauseOverlay(gameContext);

        uiElements = new UIElement[] {
                gameoverOverlay,
            pauseOverlay,
            new PauseButton(gameContext),
            new MuteButton(gameContext),
            new Controls(gameContext),
            new ShootButton(gameContext),
            new HealthBar(gameContext),
            new ScoreDisplay(gameContext),
        };
    }

    private enum MyTouchEvent {
        DOWN,
        MOVE,
        UP,
        CANCEL
    }

    public void handleMotionEvent(MotionEvent e) {
        // Handle all pointers
        // Read the docs: https://developer.android.com/reference/android/view/MotionEvent.html
        for (int i = 0; i < e.getPointerCount(); i++) {
            int pointerId = e.getPointerId(i);
            int pointerIndex = e.findPointerIndex(pointerId);
            float x = e.getX(pointerIndex);
            float y = e.getY(pointerIndex);

            // Convert MotionEvent actions into `MyTouchEvent`
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    updateTouchState(pointerId, MyTouchEvent.DOWN, x, y);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP: {
                    updateTouchState(pointerId, MyTouchEvent.UP, x, y);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    updateTouchState(pointerId, MyTouchEvent.MOVE, x, y);
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    updateTouchState(e.getPointerId(0), MyTouchEvent.CANCEL, e.getX(), e.getY());
                    break;
                }
                default: {
                    int action = e.getActionMasked();
                    Log.w("GameUI", "Unhandled action: " + MotionEvent.actionToString(action));
                }
            }
        }

//        // Restart game on button press when game is over
//        if (isGameOver && e.getAction() == MotionEvent.ACTION_DOWN) {
//            createdInput.add(UIInputId.RESTART_GAME);
//        }
    }

    private void updateTouchState(int pointerId, MyTouchEvent event, float x, float y) {
        // Note: MotionEvents we get from Android aren't always well-formed.
        // We may receive a MOTION_MOVE without first receiving a MOTION_DOWN.
        // We may receive two MOTION_UPs in a row.
        // Therefore we have to be careful when handling these events.
        switch (event) {
            case DOWN: {
                addTouch(pointerId, x, y);
                break;
            }
            case MOVE: {
                if (currTouches.containsKey(pointerId)) {
                    updateTouch(pointerId, x, y);
                } else {
                    // MOVE without a DOWN
                    addTouch(pointerId, x, y);
                }
                break;
            }
            case UP:
            case CANCEL: {
                removeTouch(pointerId, x, y);
                break;
            }
        }
    }

    private void addTouch(int pointerId, float x, float y) {
        if (currTouches.containsKey(pointerId)) {
            // Likely a double DOWN
            Log.w("GameUI", "Want to add a touch that already exists");
        } else {
            UIElement touchedElement = getElementAt(x, y);
            currTouches.put(pointerId, new Touch(touchedElement));
            if (touchedElement != null) {
                touchedElement.onTouchEnter(x, y);
            }
        }
    }

    private void updateTouch(int pointerId, float x, float y) {
        Touch currTouch = currTouches.get(pointerId);
        assert(currTouch != null);
        UIElement touchedElement = getElementAt(x, y);

        if (touchedElement == null && currTouch.touchedElement == null) {
            // Do nothing
        } else if (touchedElement == null && currTouch.touchedElement != null) {
            // Touch was previously in an element, now not anymore
            currTouch.touchedElement.onTouchLeave(x, y);
            currTouch.touchedElement = null;
        } else if (touchedElement != null && currTouch.touchedElement == null) {
            // Move onto an element
            touchedElement.onTouchEnter(x, y);
            currTouch.touchedElement = touchedElement;
        } else if (touchedElement != null && currTouch.touchedElement != null) {
            // Was on an element before, and is on an element now
            if (touchedElement == currTouch.touchedElement) {
                // Same element
                currTouch.touchedElement.onTouchMove(x, y);
            } else {
                // Different element
                currTouch.touchedElement.onTouchLeave(x, y);
                touchedElement.onTouchEnter(x, y);
                currTouch.touchedElement = touchedElement;
            }
        }
    }

    private void removeTouch(int pointerId, float x, float y) {
        if (currTouches.containsKey(pointerId)) {
            Touch currTouch = currTouches.get(pointerId);
            if (currTouch.touchedElement != null) {
                currTouch.touchedElement.onTouchLeave(x, y);
            }
            currTouches.remove(pointerId);
        } else {
            // Likely a double UP
            Log.w("GameUI", "Want to remove a touch that doesn't exist");
        }
    }

    private UIElement getElementAt(float x, float y) {
        for (UIElement elem : uiElements) {
            if (elem.getIsTouchable() && elem.isInBounds(x, y)) {
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

        if (updateContext.isPaused) {
            pauseOverlay.show();
        } else {
            pauseOverlay.hide();
        }
        if (updateContext.gameState == GameState.FINISHED) {
            gameoverOverlay.show();
        } else {
            gameoverOverlay.hide();
        }
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        // Draw in reverse order
        for (int i = uiElements.length - 1; i >= 0; i--) {
            UIElement elem = uiElements[i];
            if (elem.getIsVisible()) {
                elem.getDrawInstructions(drawInstructions);
                // Draw bounds TODO: debugging flag
                DrawRect bounds = new DrawRect(Color.GREEN, Paint.Style.STROKE, 2.0f);
                bounds.setBounds(elem.bounds);
                drawInstructions.push(bounds);
            }
        }
    }

    public static Pair<Integer, Integer> calcGameDimensions(
            int screenWidthPx,
            int screenHeightPx) {
        return new Pair<>(screenWidthPx, (int) (screenHeightPx * 0.94));
    }
}
