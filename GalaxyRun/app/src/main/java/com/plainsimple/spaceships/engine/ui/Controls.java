package com.plainsimple.spaceships.engine.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawImage3;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.util.ImageUtil;
import com.plainsimple.spaceships.util.ProtectedQueue;

import plainsimple.spaceships.R;

/**
 * The Controls is essentially a large button that displays two vertical arrows (one pointing
 * up, the other pointing down). It uses Spaceship.Direction values to keep track of its current
 * state.
 */

public class Controls extends UIElement {

    // Current control state
    private Spaceship.Direction currentDirection;
    // Bounding boxes for the arrows
    private final Rectangle boundingBoxUp, boundingBoxDown;
    // Bitmaps for the upArrow and downArrow
    private final Bitmap upArrow, downArrow;

    // Left margin as percentage of screen width
    private static final double MARGIN_LEFT_PCT = 0.03;
    // Width as percentage of screen width
    private static final double WIDTH_PCT = 0.25;
    // Height as percentage of screen height
    private static final double HEIGHT_PCT = 0.7;
    // Vertical margin of each arrow as percentage of screen height
    private static final double MARGIN_FROM_MIDDLE_PCT = 0.01;

    public Controls(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        currentDirection = Spaceship.Direction.NONE;

        // Calculate boxes for up arrow and down arrow
        double arrowMargin = MARGIN_FROM_MIDDLE_PCT * gameContext.gameHeightPx;
        double arrowHeight = bounds.getHeight() / 2 - arrowMargin;
        boundingBoxUp = new Rectangle(
                bounds.getX(),
                bounds.getY(),
                bounds.getWidth(),
                arrowHeight
        );
        boundingBoxDown = new Rectangle(
                bounds.getX(),
                gameContext.gameHeightPx / 2.0 + arrowMargin,
                bounds.getWidth(),
                arrowHeight
        );
        // Load and scale bitmaps based on height only
        Bitmap upOriginal = gameContext.bitmapCache.getBitmap(BitmapID.UP_ARROW);
        double scalingFactor = arrowHeight / upOriginal.getHeight();
        upArrow = Bitmap.createScaledBitmap(
                upOriginal,
                (int) (upOriginal.getWidth() * scalingFactor),
                (int) (upOriginal.getHeight() * scalingFactor),
                true
        );
        downArrow = ImageUtil.rotate180(upArrow);
    }

    public static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                gameContext.gameWidthPx * MARGIN_LEFT_PCT,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    @Override
    public void update(UpdateContext updateContext) {
        currentDirection = updateContext.playerDirection;
    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        DrawImage3 drawUp = new DrawImage3(
                upArrow, (int) boundingBoxUp.getX(), (int) boundingBoxUp.getY());
        DrawImage3 drawDown = new DrawImage3(
                downArrow, (int) boundingBoxDown.getX(), (int) boundingBoxDown.getY());

        if (currentDirection == Spaceship.Direction.DOWN) {
            drawParams.push(drawDown);
        } else if (currentDirection == Spaceship.Direction.UP) {
            drawParams.push(drawUp);
        } else {
            // No direction input: draw both
            drawParams.push(drawUp);
            drawParams.push(drawDown);
        }
    }

    @Override
    public void onTouchEnter(float x, float y) {
        Log.d("Controls", "onTouchEnter " + x + ", " + y);
        if (boundingBoxUp.isInBounds(x, y)) {
            createdInput.add(UIInputId.START_MOVING_UP);
        } else if (boundingBoxDown.isInBounds(x, y)) {
            createdInput.add(UIInputId.START_MOVING_DOWN);
        }
    }

    @Override
    public void onTouchMove(float x, float y) {
        Log.d("Controls", "onTouchMove " + x + ", " + y);
        if (boundingBoxUp.isInBounds(x, y)) {
            createdInput.add(UIInputId.START_MOVING_UP);
        } else if (boundingBoxDown.isInBounds(x, y)) {
            createdInput.add(UIInputId.START_MOVING_DOWN);
        }
    }

    @Override
    public void onTouchLeave(float x, float y) {
        Log.d("Controls", "onTouchLeave " + x + ", " + y);
        createdInput.add(UIInputId.STOP_MOVING);
    }
}
