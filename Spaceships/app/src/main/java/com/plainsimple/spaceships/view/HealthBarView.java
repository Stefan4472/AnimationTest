package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import plainsimple.spaceships.R;

/**
 * Displays Spaceship's health bar along lower screen edge
 */
public class HealthBarView extends View {

    // current health level
    int currentHealth = 0;
    // full health level
    int fullHealth = 0;
    // health level the bar is transitioning to
    int movingToHealth;
    // width of health bar on screen
    int width;
    // height of health bar on screen
    int height;
    // used to getDrawParams the health bar
    Paint paint;
    // used to determine lower and upper bounds of rgb values
    private int blue = 0;
    private int lowerGreen = 0;
    private int upperGreen = 255;
    private int lowerRed = 0;
    private int upperRed = 255;

    public HealthBarView(Context context) {
        super(context);
        paint = new Paint();
    }

    public HealthBarView(Context context, AttributeSet attrs) { // todo: use xml attributes
        super(context, attrs);
        paint = new Paint();
    }

    // no animated change
    public void setCurrentHealth(int currentHealth) { // todo: animated change
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        this.currentHealth = currentHealth;
        movingToHealth = currentHealth;
        invalidate();
    }

    // animates change
    public void setMovingToHealth(int movingToHealth) {
        if (movingToHealth < 0) {
            movingToHealth = 0;
        }
        this.movingToHealth = movingToHealth;
        invalidate();
    }

    public void setFullHealth(int fullHealth) {
        if (fullHealth < 0) {
            fullHealth = 0;
        }
        this.fullHealth = fullHealth;
        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferred_width = MeasureSpec.getSize(widthMeasureSpec);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // preferred height is one tenth of screen height
        int preferred_height = (int) (metrics.heightPixels * 0.03);
        setMeasuredDimension(preferred_width, decideMeasurement(heightMeasureSpec, preferred_height));
    }

    // logic for deciding on the measured height of the view
    private int decideMeasurement(int measureSpec, int preferred) {
        int specified_size = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY: // height of the view must be the one given
                return specified_size;
            case MeasureSpec.AT_MOST: // height of the view cannot be larger than the one given
                return Math.min(preferred, specified_size);
            default: // no restrictions
                return preferred;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        Log.d("HealthBarView.java", "Size changed to " + width + "," + height);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // getDrawParams bounding rectangle of health bar
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(height * 0.1f);
        canvas.drawRect(0, 0, width, height, paint);
        // make sure movingToHealth > 0
        // check to see if the health bar should be transitioning to a new value
        if (currentHealth != movingToHealth) {
            // transition down 20% of the remaining distance between currentHealth and movingToHealth
            currentHealth -= (double) (currentHealth - movingToHealth) * 0.2;
            invalidate();
        }
        // getDrawParams the filling of the health bar
        paint.setColor(getHealthBarColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(height * 0.1f, height * 0.1f, height * 0.1f + (currentHealth / (float) fullHealth) * (width - height * 0.1f), height * 0.9f, paint);
        // getDrawParams label on hitbar showing current health vs. full health
        paint.setColor(Color.GRAY);
        paint.setTextSize(height * 0.8f);
        // todo: better calculate where to position text
        canvas.drawText(currentHealth + "/" + fullHealth, width * 0.9f, height * 0.85f, paint);
    }

    // calculates what color the health bar should be using the ratio of
    // currentHealth to fullHealth
    private int getHealthBarColor() {
        double ratio = currentHealth / (double) fullHealth;
        // green: 0, 255, blue (or lowerRed, upperGreen, blue)
        // yellow: 255, 255, blue (or upperRed, upperGreen, blue)
        // red: 255, 0, blue (or upperRed, lowerGreen, blue)
        double red = (upperRed - lowerRed) / 2.0 - (ratio - 0.5) * (upperRed - lowerRed);
        double green = (upperGreen - lowerGreen) / 2.0 + (ratio - 0.5) * (upperGreen - lowerGreen);
        return Color.rgb((int) red, (int) green, blue);
    }
}
