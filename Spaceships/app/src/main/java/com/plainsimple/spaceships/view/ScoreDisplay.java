package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * Draws score in the top left of GameView
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
        startY = padding + TEXT_SIZE;
        Typeface tf = Typeface.createFromAsset(context.getAssets(),FONT);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE);
        paint.setTypeface(tf); // todo: bold?
    }

    // adds to the current score
    public void update(int newScore) {
        score = newScore;
    }

    public void draw(Canvas canvas) { // todo: animations
        canvas.drawText(Integer.toString(score), startX, startY, paint);
    }
}
