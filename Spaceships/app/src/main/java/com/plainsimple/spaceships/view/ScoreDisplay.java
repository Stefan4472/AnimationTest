package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Displays the game score in the top left of the screen.
 * There are two components:
 * -The text drawn, which is the current game score and can be updated
 * -The color and magnification of the text. These are animated to transition from
 * DEFAULT_COLOR to SCORING_COLOR and the default magnification (1.0) to MAX_MAGNIFICATION (1.5)
 * based on *the change in score,* where changes in score closer to maxScore lead to bigger
 * animated changes. Positive changes are made in a non-linear fashion, whereas negative changes
 * happen one step at a time in a linear fashion.
 */
public class ScoreDisplay {

    // score to display
    private int score;
    // Paint used for drawText on canvas
    private Paint paint;
    // x-coordinates to start drawing score
    private float startX;
    // y-coordinate to start drawing score
    private float startY;
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
    private static final int PADDING = 10;
    // path to font file to use
    private static final String FONT = "fonts/galax___.ttf";
    // text size
    private static final int TEXT_SIZE = 50;

    public ScoreDisplay(Context context, int startScore) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float padding = PADDING * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        startX = padding;
        startY = padding;
        Typeface tf = Typeface.createFromAsset(context.getAssets(), FONT);
        paint = new Paint();
        //paint.setColor(Color.WHITE);
        //paint.setTextSize(TEXT_SIZE);
        paint.setTypeface(tf); // todo: bold?
    }

    // adds to the current score and configures animation
    public void update(int newScore) {
        float dscore = newScore - score;
        dscore = (dscore > MAX_SCORE_CHANGE ? MAX_SCORE_CHANGE : dscore);
        if (dscore > movingToScoreChange) { // start animating up
            movingToScoreChange = dscore;
            animatingUp = true;
        } else if (movingToScoreChange > dscore && !animatingUp) {
            // current animating score change is greater than the current change:
            // score is falling one step per frame
            movingToScoreChange--;
        }
        score = newScore;
    }

    public void draw(Canvas canvas) {
        paint.setColor(getColor());
        paint.setTextSize(TEXT_SIZE * getMagnification());
        //Log.d("ScoreDisplay.java", "Color set to " + colorToString(paint.getColor()) + " Magnification at " + getMagnification());
        canvas.drawText(Integer.toString(score), startX, startY + paint.getTextSize(), paint);
    }

    // calculated "steps" -- change in color value for each unit of scoreChange
    private float rStep = (Color.red(SCORING_COLOR) - Color.red(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;
    private float gStep = (Color.green(SCORING_COLOR) - Color.green(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;
    private float bStep = (Color.blue(SCORING_COLOR) - Color.blue(DEFAULT_COLOR)) / MAX_SCORE_CHANGE;

    // returns color to use when drawing ScoreDisplay text
    private int getColor() {
        // calculate current rgb values
        float r_current = Color.red(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * rStep;
        float g_current = Color.green(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * gStep;
        float b_current = Color.blue(DEFAULT_COLOR) + (currentScoreChange / MAX_SCORE_CHANGE) * bStep;
        // non-linear color change up
        if (animatingUp) {
            // calculate number of "steps" to take toward movingToScoreChange. Calculate rgb based off that
            float dsteps = (movingToScoreChange - currentScoreChange) / 1.4f;
            currentScoreChange += dsteps;
            if ((int) dsteps == 0) {
                animatingUp = false;
            }
            return Color.rgb((int) (r_current + rStep * dsteps), (int) (g_current + gStep * dsteps),
                    (int) (b_current + bStep * dsteps));
        } else { // calculate rgb for movingToScoreChange (which is decreasing linearly)
            return Color.rgb((int) (rStep * movingToScoreChange), (int) (gStep * movingToScoreChange),
                    (int) (bStep * movingToScoreChange));
        }
    }

    // returns magnification factor of ScoreDisplay text
    private float getMagnification() {
        return (MAX_MAGNIFICATION / MAX_SCORE_CHANGE) * currentScoreChange;
    }

    public void reset() {
        score = 0;
    }

    private static String colorToString(int color) {
        return "A:" + Color.alpha(color) + " R:" + Color.red(color) + " G:" + Color.green(color) + " B:" + Color.blue(color);
    }
}
