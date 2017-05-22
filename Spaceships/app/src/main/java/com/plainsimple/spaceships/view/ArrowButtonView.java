package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.ImageUtil;

import plainsimple.spaceships.R;

/**
 * The ArrowButton view is essentially a large button that displays two vertical arrows (one pointing
 * up, the other pointing down).
 */

public class ArrowButtonView extends View {

    // R.drawable id of the arrow to use. Should be oriented upwards
    private static final int UP_ARROW_IMG_ID = R.drawable.up_arrow;

    // possible states
    private enum State {
        UP_PRESSED, DOWN_PRESSED, NONE_PRESSED;
    }

    // bitmaps for the upArrow and downArrow
    private Bitmap upArrow, downArrow;
    // current state of ArrowButtons
    private State currentState;

    public ArrowButtonView(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // for desired width, we'd like 30% of the screen width. Desired height will be whatever
        // is offered (to fill the screen vertically)
        int desired_width = (int) (0.3 * getResources().getDisplayMetrics().widthPixels);
        int actual_width = resolveSize(desired_width, widthMeasureSpec);
        setMeasuredDimension(actual_width, heightMeasureSpec);
    }

    @Override // load and scale UP_ARROW_IMG_ID as upArrow, then rotate it 180 degrees to get downArrow
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            // todo: need to be scaled?
            upArrow = BitmapFactory.decodeResource(getResources(), UP_ARROW_IMG_ID);
//            upArrow = ImageUtil.decodeAndScaleTo(getContext(), UP_ARROW_IMG_ID, w / 5, h);
            downArrow = ImageUtil.rotate180(upArrow);
            Log.d("ArrowButtonView", "Size set to " + w + "," + h);
        }
    }

    @Override // handle user touching the view. Update currentState based on event
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // determine which arrow button user intended to press. This is done by checking which
            // half of the view was touched (top or bottom). Applies equally to ACTION_DOWN and
            // ACTION_MOVE (we'd want to detect if the user started pressing the other button)
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                State new_state;
                if (event.getY() > getHeight() / 2) {
                    new_state = State.DOWN_PRESSED;
                } else {
                    new_state = State.UP_PRESSED;
                }
                // check if new state is different. If so, redraw
                if (new_state != currentState) {
                    currentState = new_state;
                    invalidate();
                }
                break;
            // user stopped touching buttons: revert to NONE_PRESSED
            case MotionEvent.ACTION_UP: // end of touch
                currentState = State.NONE_PRESSED;
                break;
        }
        return true;
    }

    @Override // divides the view into two vertical halves. Draws the up arrow so it ends at the
    // vertical half. Draws the down arrow so it starts at the vertical half and goes downward
    public void onDraw(Canvas canvas) {
        int half_point = getHeight() / 2;
        if (currentState == State.DOWN_PRESSED) { // draw only down
            canvas.drawBitmap(downArrow, 0, half_point, null);
        } else if (currentState == State.UP_PRESSED) { // draw only up
            canvas.drawBitmap(upArrow, 0, half_point - upArrow.getHeight(), null);
        } else { // draw both
            canvas.drawBitmap(upArrow, 0, half_point - upArrow.getHeight(), null);
            canvas.drawBitmap(downArrow, 0, half_point, null);
        }
    }
}
