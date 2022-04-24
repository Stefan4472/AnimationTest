package com.galaxyrun.engine.ui;

import android.graphics.Bitmap;

import com.galaxyrun.engine.GameContext;
import com.galaxyrun.engine.UpdateContext;
import com.galaxyrun.engine.draw.DrawImage;
import com.galaxyrun.engine.draw.DrawInstruction;
import com.galaxyrun.helper.BitmapID;
import com.galaxyrun.helper.Rectangle;
import com.galaxyrun.sprite.Spaceship;
import com.galaxyrun.util.ImageUtil;
import com.galaxyrun.util.ProtectedQueue;

/**
 * The Controls is essentially a large button that displays two vertical arrows (one pointing
 * up, the other pointing down). It uses Spaceship.Direction values to keep track of its current
 * state.
 */

public class Controls extends UIElement {

    // Current control state
    private Spaceship.Direction currDirection;
    // Current input
    private Spaceship.Direction currInput;
    // Bounding boxes for the arrows
    private final Rectangle boundingBoxUp, boundingBoxDown;
    // Bitmaps for the upArrow and downArrow
    private final Bitmap upArrow, downArrow;

    // Left margin as percentage of screen width
    private static final double MARGIN_LEFT_PCT = 0.03;
    // Width as percentage of screen width
    private static final double WIDTH_PCT = 0.3;
    // Height as percentage of screen height
    private static final double HEIGHT_PCT = 1.0;
    // Vertical margin of each arrow as percentage of screen height
    private static final double MARGIN_FROM_MIDDLE_PCT = 0.01;

    public Controls(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        currDirection = Spaceship.Direction.NONE;
        currInput = null;
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
        currDirection = updateContext.playerDirection;
        if (currInput == Spaceship.Direction.UP) {
            createdInput.add(UIInputId.MOVE_UP);
        } else if (currInput == Spaceship.Direction.DOWN) {
            createdInput.add(UIInputId.MOVE_DOWN);
        }
    }

    @Override
    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawInstructions) {
        DrawImage drawUp = new DrawImage(
                upArrow, (int) boundingBoxUp.getX(), (int) boundingBoxUp.getY());
        DrawImage drawDown = new DrawImage(
                downArrow, (int) boundingBoxDown.getX(), (int) boundingBoxDown.getY());

        if (currDirection == Spaceship.Direction.DOWN) {
            drawInstructions.push(drawDown);
        } else if (currDirection == Spaceship.Direction.UP) {
            drawInstructions.push(drawUp);
        } else {
            // No direction input: draw both
            drawInstructions.push(drawUp);
            drawInstructions.push(drawDown);
        }
    }

    @Override
    public void onTouchEnter(float x, float y) {
//        Log.d("Controls", "onTouchEnter " + x + ", " + y);
        if (boundingBoxUp.isInBounds(x, y)) {
            currInput = Spaceship.Direction.UP;
        } else if (boundingBoxDown.isInBounds(x, y)) {
            currInput = Spaceship.Direction.DOWN;
        } else {
            currInput = Spaceship.Direction.NONE;
        }
    }

    @Override
    public void onTouchMove(float x, float y) {
//        Log.d("Controls", "onTouchMove " + x + ", " + y);
        if (boundingBoxUp.isInBounds(x, y)) {
            currInput = Spaceship.Direction.UP;
        } else if (boundingBoxDown.isInBounds(x, y)) {
            currInput = Spaceship.Direction.DOWN;
        } else {
            currInput = Spaceship.Direction.NONE;
        }
    }

    @Override
    public void onTouchLeave(float x, float y) {
//        Log.d("Controls", "onTouchLeave " + x + ", " + y);
        currInput = Spaceship.Direction.NONE;
    }
}
