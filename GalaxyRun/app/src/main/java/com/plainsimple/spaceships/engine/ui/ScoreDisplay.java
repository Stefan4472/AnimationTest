package com.plainsimple.spaceships.engine.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Queue;

/**
 * Displays the game score in the top left of the screen.
 * There are two components:
 * -The text drawn, which is the current game score and can be updated
 * -The color and magnification of the text. These are animated to transition from
 * DEFAULT_COLOR to SCORING_COLOR and the default magnification (1.0) to MAX_MAGNIFICATION (1.5)
 * based on *the change in score,* where changes in score closer to maxScore lead to bigger
 * animated changes. Positive changes are made in a non-linear fashion, whereas negative changes
 * happen one step at a time in a linear fashion.
 *
 * TODO: custom font
 */
public class ScoreDisplay extends UIElement {

    // score to display
    private int score;
    // x-coordinates to start drawing score
    private final float startX;
    // y-coordinate to start drawing score
    private final float startY;
    // current change in score being animated
    private float currentScoreChange;
    // change in score animation is moving toward
    private float movingToScoreChange;
    // whether an animation upwards is occurring
    private boolean animatingUp = false;

    // default color
    private static final int DEFAULT_COLOR = Color.WHITE;
    // color transitioned to when points are rapidly scored
    private static final int SCORING_COLOR = Color.parseColor("#ffb30f");
    // the change in score that creates the largest animated change
    // changes greater than this are bound to this value
    private static final float MAX_SCORE_CHANGE = 30.0f;
    // max magnifying effect for when points are rapidly scored
    private static final float MAX_MAGNIFICATION = 1.5f;
    // left and top padding (dp)
    private static final float PADDING_PCT = 0.05f;
    // path to font file to use
    private static final String FONT = "fonts/galax___.ttf";
    // text size (percentage of screen height)
    private static final float BASE_TEXT_SIZE_PCT = 0.10f;

    public ScoreDisplay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        startX = (float) bounds.getX();
        startY = (float) bounds.getY();
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        return new Rectangle(
                gameContext.screenWidthPx * PADDING_PCT,
                gameContext.screenWidthPx * PADDING_PCT,
                gameContext.screenWidthPx * BASE_TEXT_SIZE_PCT,
                gameContext.screenWidthPx * BASE_TEXT_SIZE_PCT
        );
    }

    /*
    Update animations.
     */
    public void update(UpdateContext updateContext) {
        float dscore = Math.min(updateContext.score - score, MAX_SCORE_CHANGE);
        if (dscore > movingToScoreChange) {
            // Start animating up
            movingToScoreChange = dscore;
            animatingUp = true;
        } else if (movingToScoreChange > dscore && !animatingUp) {
            // current animating score change is greater than the current change:
            // score is falling one step per frame
            movingToScoreChange--;
        }
        score = updateContext.score;
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        String scoreString = Integer.toString(score);
        int textSize = (int) (BASE_TEXT_SIZE_PCT * gameContext.screenHeightPx * getMagnification());
        // TODO: support fonts
        DrawText text = new DrawText(scoreString, startX, startY + textSize, calculateColor(), textSize);
        drawQueue.push(text);
    }

    // calculated "steps" -- change in color value for each unit of scoreChange
    private final float R_STEP = (Color.red(SCORING_COLOR) - Color.red(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;
    private final float G_STEP = (Color.green(SCORING_COLOR) - Color.green(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;
    private final float B_STEP = (Color.blue(SCORING_COLOR) - Color.blue(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;

    /*
    Calculates RGB color to use when drawing ScoreDisplay.
     */
    private int calculateColor() {
        // calculate current rgb values
        float r_current = Color.red(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * R_STEP;
        float g_current = Color.green(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * G_STEP;
        float b_current = Color.blue(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * B_STEP;
        // non-linear color change up
        if (animatingUp) {
            // calculate number of "steps" to take toward movingToScoreChange. Calculate rgb based off that
            float dsteps = (movingToScoreChange - currentScoreChange) / 1.4f;
            currentScoreChange += dsteps;
            if ((int) dsteps == 0) {
                animatingUp = false;
            }
            return Color.rgb((int) (r_current + R_STEP * dsteps), (int) (g_current + G_STEP * dsteps),
                    (int) (b_current + B_STEP * dsteps));
        } else { // calculate rgb for movingToScoreChange (which is decreasing linearly)
            return Color.rgb((int) (R_STEP * movingToScoreChange), (int) (G_STEP * movingToScoreChange),
                    (int) (B_STEP * movingToScoreChange));
        }
    }

    // returns magnification factor of ScoreDisplay text
    private float getMagnification() {
        return 1 + ((MAX_MAGNIFICATION - 1) / MAX_SCORE_CHANGE) * currentScoreChange;
    }

    public void reset() {
        score = 0;
        currentScoreChange = 0;
        movingToScoreChange = 0;
        animatingUp = false;
    }

    private static String colorToString(int color) {
        return "A:" + Color.alpha(color) + " R:" + Color.red(color) + " G:" + Color.green(color) + " B:" + Color.blue(color);
    }

    public void onTouchEnter(float x, float y) {
        Log.d("ScoreDisplay", "onTouchEnter " + x + ", " + y);
    }

    public void onTouchMove(float x, float y) {
        Log.d("ScoreDisplay", "onTouchMove " + x + ", " + y);
    }

    public void onTouchLeave(float x, float y) {
        Log.d("ScoreDisplay", "onTouchLeave " + x + ", " + y);
    }
}
