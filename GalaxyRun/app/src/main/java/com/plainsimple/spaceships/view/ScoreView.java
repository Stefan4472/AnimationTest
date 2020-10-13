package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Displays the game score.
 * There are two components:
 * -The text drawn, which is the current game score and can be updated
 * -The color and magnification of the text. These are animated to transition from
 * DEFAULT_COLOR to SCORING_COLOR and the default magnification (1.0) to MAX_MAGNIFICATION (1.5)
 * based on *the change in score,* where changes in score closer to maxScore lead to bigger
 * animated changes. Positive changes are made in a non-linear fashion, whereas negative changes
 * happen one step at a time in a linear fashion.
 */

public class ScoreView extends FontTextView {

    // score to display
    private int score;
    // used to draw the health bar
//    private Paint paint;

    public ScoreView(Context context) {
        super(context);
        setTextColor(Color.WHITE);
        setTextSize(30);
//        paint = new Paint();
    }

    public ScoreView(Context context, AttributeSet attrs) { // todo: use xml attributes
        super(context, attrs);
        setTextColor(Color.WHITE);
        setTextSize(30);
//        paint = new Paint();
    }

    // animates change
    public void updateScore(int newScore) {
        this.score = newScore;
        setText(Integer.toString(newScore));
        //invalidate();
    }
}
