package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * Draws healthbar onto GameView
 */

public class HealthBar {

    // current health level
    int currentHealth = 0;
    // full health level
    int fullHealth = 0;
    // health level the bar is transitioning to
    int movingToHealth;
    // width of health bar on screen
    float width;
    // height of health bar on screen
    float height;
    // starting x-coordinate
    float startX;
    // starting y-coordinate
    float startY;
    // left, right, and bottom padding (dp)
    private static final int PADDING = 10;
    // used to getDrawParams the health bar
    Paint paint;
    // used to determine lower and upper bounds of rgb values
    private int blue = 0;
    private int lowerGreen = 0;
    private int upperGreen = 255;
    private int lowerRed = 0;
    private int upperRed = 255;

    // viewWidth and Height will be used to determine size and location of healthbar
    // startHealth and fullHealth configure healthbar values
    public HealthBar(Context context, int viewWidth, int viewHeight, int startHealth, int fullHealth) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float padding = PADDING * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        width = viewWidth - 2 * padding;
        height = viewHeight * 0.03f;
        startX = padding;
        startY = viewHeight - height - padding; // todo: padding?
        Log.d("HealthBar", "Coordinates are " + startX + "," + startY + " with w/h " + width + "," + height);
        paint = new Paint();
        setFullHealth(fullHealth);
        setCurrentHealth(startHealth);
    }

    // sets currentHealth to given value without triggering animated change
    public void setCurrentHealth(int currentHealth) { // todo: animated change
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        this.currentHealth = currentHealth;
        movingToHealth = currentHealth;
    }

    // triggers animated change to given value
    public void setMovingToHealth(int movingToHealth) {
        if (movingToHealth < 0) {
            movingToHealth = 0;
        }
        this.movingToHealth = movingToHealth;
    }

    public void setFullHealth(int fullHealth) {
        if (fullHealth < 0) {
            fullHealth = 0;
        }
        this.fullHealth = fullHealth;
    }

    /*@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferred_width = View.MeasureSpec.getSize(widthMeasureSpec);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // preferred height is one tenth of screen height
        int preferred_height = (int) (metrics.heightPixels * 0.03);
        setMeasuredDimension(preferred_width, decideMeasurement(heightMeasureSpec, preferred_height));
    }

    // logic for deciding on the measured height of the view
    public int decideMeasurement(int measureSpec, int preferred) {
        int specified_size = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case View.MeasureSpec.EXACTLY: // height of the view must be the one given
                return specified_size;
            case View.MeasureSpec.AT_MOST: // height of the view cannot be larger than the one given
                return Math.min(preferred, specified_size);
            default: // no restrictions
                return preferred;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
    }*/

    public void draw(Canvas canvas) {
        // getDrawParams bounding rectangle of health bar
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(height * 0.1f);
        canvas.drawRect(startX, startY, startX + width, startY + height, paint);
        // make sure movingToHealth > 0
        // check to see if the health bar should be transitioning to a new value
        if (currentHealth != movingToHealth) {
            // transition down 20% of the remaining distance between currentHealth and movingToHealth
            currentHealth -= (double) (currentHealth - movingToHealth) * 0.2;
            //Log.d("HealthBarView", "Currenthealth now at " + currentHealth + " and moving to " + movingToHealth);
        }
        // getDrawParams the filling of the health bar
        paint.setColor(getHealthBarColor());
        paint.setStyle(Paint.Style.FILL);
        float inner_padding = height * 0.1f;
        canvas.drawRect(startX + inner_padding, startY + inner_padding,
                startX + inner_padding + (currentHealth / (float) fullHealth) * (width - height * 0.1f),
                startY + height - inner_padding, paint);
        // getDrawParams label on hitbar showing current health vs. full health
        paint.setColor(Color.GRAY);
        paint.setTextSize(height * 0.8f);
        // todo: better calculate where to position text
        canvas.drawText(currentHealth + "/" + fullHealth, startX + width * 0.9f, startY + height * 0.85f, paint);
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
