package com.plainsimple.spaceships.engine.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage2;
import com.plainsimple.spaceships.engine.draw.DrawImage3;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.ImageUtil;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Queue;

import plainsimple.spaceships.R;

/**
 * The Controls is essentially a large button that displays two vertical arrows (one pointing
 * up, the other pointing down). It uses Spaceship.Direction values to keep track of its current
 * state.
 */

public class Controls extends UIElement {

    private GameContext gameContext;

    // R.drawable id of the arrow to use. Should be oriented upwards
    private static final int UP_ARROW_IMG_ID = R.drawable.up_arrow;
    // dp each arrow should be from the vertical center of the view
    private static final int DP_FROM_CENTER = 5;

    // bitmaps for the upArrow and downArrow
    private Bitmap upArrow, downArrow;
    // calculated top- and left-coordinate values for drawing upArrow and downArrow
    private int upArrowTop, downArrowTop, paddingLeft;
    // current state of ArrowButtons, using a Spaceship.Direction value
    private Spaceship.Direction currentDirection;

    private final int widthPx, heightPx;
    private Rectangle boundingBoxUp, boundingBoxDown;

    public Controls(GameContext gameContext) {
        this.gameContext = gameContext;
        currentDirection = Spaceship.Direction.NONE;

        // Set width to 30% of game width and height to full game height
        widthPx = (int) (0.3 * gameContext.gameWidthPx);
        heightPx = gameContext.gameHeightPx;

        // load upArrow and downArrow todo: need to be scaled?
        upArrow = BitmapFactory.decodeResource(
                gameContext.getAppContext().getResources(), UP_ARROW_IMG_ID);
        downArrow = ImageUtil.rotate180(upArrow);
        Log.d("ArrowButtonView", "Size set to " + widthPx + "," + heightPx);

        // calculate top-left for upArrow and downArrow. We want them mirrored about vertical
        // center, with a small offset of DP_FROM_CENTER pixels
        int y_offset = (int) (DP_FROM_CENTER * gameContext.getAppContext().getResources().getDisplayMetrics().density);
        upArrowTop = /*getPaddingTop()*/ 0 + heightPx / 2 - upArrow.getHeight() - y_offset;
        downArrowTop = /*getPaddingTop()*/ 0 + heightPx / 2 + y_offset;
        paddingLeft = 0;

        boundingBoxUp = new Rectangle(0, upArrowTop, widthPx, upArrow.getHeight() - y_offset);
        boundingBoxDown = new Rectangle(0, downArrowTop, widthPx, downArrow.getHeight());
    }

//    @Override // handle user touching the view. Update currentState based on event
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            // determine which arrow button user intended to press. This is done by checking which
//            // half of the view was touched (top or bottom). Applies equally to ACTION_DOWN and
//            // ACTION_MOVE (we'd want to detect if the user started pressing the other button)
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//                Spaceship.Direction new_direction;
//                if (event.getY() > getHeight() / 2) {
//                    new_direction = Spaceship.Direction.DOWN;
//                } else {
//                    new_direction = Spaceship.Direction.UP;
//                }
//                // check if new state is different. If so, fire OnDirectionChanged and redraw
//                if (new_direction != currentDirection) {
//                    currentDirection = new_direction;
//                    listener.onDirectionChanged(currentDirection);
//                    Log.d("ArrowButtonView", "Input Direction changed to " + currentDirection);
//                    invalidate();
//                }
//                break;
//            // user stopped touching buttons: revert to Direction.NONE and call method to redraw
//            case MotionEvent.ACTION_UP: // end of touch
//                currentDirection = Spaceship.Direction.NONE;
//                listener.onDirectionChanged(currentDirection);
//                Log.d("ArrowButtonView", "Direction changed to None");
//                invalidate();
//                break;
//        }
//        return true;
//    }

    public void update(UpdateContext updateContext) {
        currentDirection = updateContext.playerDirection;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        DrawImage3 drawDown = new DrawImage3(downArrow, paddingLeft, downArrowTop);
        DrawImage3 drawUp = new DrawImage3(upArrow, paddingLeft, upArrowTop);

        if (currentDirection == Spaceship.Direction.DOWN) {
            drawParams.push(drawDown);
        } else if (currentDirection == Spaceship.Direction.UP) {
            drawParams.push(drawUp);
        } else {
            // Draw both
            drawParams.push(drawUp);
            drawParams.push(drawDown);
        }

        DrawRect upHitbox = new DrawRect(Color.GREEN, Paint.Style.STROKE, 2.0f);
        upHitbox.setBounds(boundingBoxUp);
        DrawRect downHitbox = new DrawRect(Color.BLUE, Paint.Style.STROKE, 2.0f);
        downHitbox.setBounds(boundingBoxDown);
        drawParams.push(upHitbox);
        drawParams.push(downHitbox);
    }

    @Override
    public boolean handleEvent(MotionEvent e, Queue<UIInputId> createdInput) {
        boolean inUp = boundingBoxUp.isInBounds(e.getX(), e.getY());
        boolean inDown = boundingBoxDown.isInBounds(e.getX(), e.getY());

        switch (e.getAction()) {
            case (MotionEvent.ACTION_DOWN): {
                if (inUp) {
                    createdInput.add(UIInputId.START_MOVING_UP);
                    return true;
                } else if (inDown) {
                    createdInput.add(UIInputId.START_MOVING_DOWN);
                    return true;
                }
            }
            case (MotionEvent.ACTION_UP): {
                // Stop direction input, regardless of where on the screen
                // is being released
                createdInput.add(UIInputId.STOP_MOVING);
                // Consume input if within a bounding box
                return inUp || inDown;
            }
            default: {
                return false;
            }
        }
    }
}
