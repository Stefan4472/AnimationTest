package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.ImageUtil;

import plainsimple.spaceships.R;

/**
 * The ArrowButton view is essentially a large button that displays two vertical arrows (one pointing
 * up, the other pointing down). It uses Spaceship.Direction values to keep track of its current
 * state.
 */

public class ArrowButtonView extends View {

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
    // receives events when direction is changed
    private OnDirectionChangedListener listener;

    // interface to send a callback when state of view is changed
    public interface OnDirectionChangedListener {
        void onDirectionChanged(Spaceship.Direction newDirection);
    }

    public void setOnDirectionChangedListener(OnDirectionChangedListener listener) {
        this.listener = listener;
    }

    public ArrowButtonView(Context context) {
        super(context);
        currentDirection = Spaceship.Direction.NONE;
    }

    public ArrowButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentDirection = Spaceship.Direction.NONE;
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
            // calculate dimensions excluding padding
            int actual_width = w - getPaddingLeft() - getPaddingRight();
            int actual_height = h - getPaddingTop() - getPaddingBottom();

            // load upArrow and downArrow todo: need to be scaled?
            upArrow = BitmapFactory.decodeResource(getResources(), UP_ARROW_IMG_ID);
            downArrow = ImageUtil.rotate180(upArrow);
            Log.d("ArrowButtonView", "Size set to " + w + "," + h);

            // calculate top-left for upArrow and downArrow. We want them mirrored about vertical
            // center, with a small offset of DP_FROM_CENTER pixels
            int y_offset = (int) (DP_FROM_CENTER * getResources().getDisplayMetrics().density);
            upArrowTop = getPaddingTop() + actual_height / 2 - upArrow.getHeight() - y_offset;
            downArrowTop = getPaddingTop() + actual_height / 2 + y_offset;
            paddingLeft = getPaddingLeft();
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
                Spaceship.Direction new_direction;
                if (event.getY() > getHeight() / 2) {
                    new_direction = Spaceship.Direction.DOWN;
                } else {
                    new_direction = Spaceship.Direction.UP;
                }
                // check if new state is different. If so, fire OnDirectionChanged and redraw
                if (new_direction != currentDirection) {
                    currentDirection = new_direction;
                    listener.onDirectionChanged(currentDirection);
                    Log.d("ArrowButtonView", "Input Direction changed to " + currentDirection);
                    invalidate();
                }
                break;
            // user stopped touching buttons: revert to Direction.NONE and call method to redraw
            case MotionEvent.ACTION_UP: // end of touch
                currentDirection = Spaceship.Direction.NONE;
                listener.onDirectionChanged(currentDirection);
                Log.d("ArrowButtonView", "Direction changed to None");
                invalidate();
                break;
        }
        return true;
    }

    @Override // divides the view into two vertical halves. Draws the up arrow so it ends at the
    // vertical half. Draws the down arrow so it starts at the vertical half and goes downward
    public void onDraw(Canvas canvas) {
        if (currentDirection == Spaceship.Direction.DOWN) { // draw only down
            canvas.drawBitmap(downArrow, paddingLeft, downArrowTop, null);
        } else if (currentDirection == Spaceship.Direction.UP) { // draw only up
            canvas.drawBitmap(upArrow, paddingLeft, upArrowTop, null);
        } else { // draw both
            canvas.drawBitmap(upArrow, paddingLeft, upArrowTop, null);
            canvas.drawBitmap(downArrow, paddingLeft, downArrowTop, null);
        }
    }
}
