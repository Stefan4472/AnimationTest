package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.Queue;

/**
 * Draws player's health.
 */

public class HealthBar extends UIElement {

    private GameContext gameContext;

    // current health level
    int currentHealth;
    // full health level
    int fullHealth;
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
    // Specify upper and lower RGB values
    private final int BLUE = 0;
    private final int LOWER_GREEN = 0;
    private final int UPPER_GREEN = 255;
    private final int LOWER_RED = 0;
    private final int UPPER_RED = 255;

    // viewWidth and Height will be used to determine size and location of healthbar
    // startHealth and fullHealth configure healthbar values
    public HealthBar(GameContext gameContext) {
        this.gameContext = gameContext;

        DisplayMetrics metrics = gameContext.appContext.getResources().getDisplayMetrics();
        float padding = PADDING * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        width = gameContext.screenWidthPx - 2 * padding;
        height = gameContext.screenHeightPx * 0.03f;
        startX = padding;
        startY = gameContext.screenHeightPx - height - padding; // todo: padding?
        Log.d("HealthBar", "Coordinates are " + startX + "," + startY + " with w/h " + width + "," + height);

        setFullHealth(gameContext.fullHealth);
        setCurrentHealth(gameContext.fullHealth);
    }

    // sets currentHealth to given value without triggering animated change
    public void setCurrentHealth(int currentHealth) {
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

    public void update(UpdateContext updateContext) {
        setMovingToHealth(updateContext.playerHealth);
    }

    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // Draw outline
        DrawRect outline = new DrawRect(Color.GRAY, Paint.Style.STROKE, height * 0.1f);
        outline.setBounds(startX, startY, startX + width, startY + height);
        drawQueue.push(outline);

        if (currentHealth != movingToHealth) {
            // Transition down 20% of the remaining distance between
            // currentHealth and movingToHealth
            currentHealth -= (double) (currentHealth - movingToHealth) * 0.2;
        }

        // Draw fill
        float inner_padding = height * 0.1f;
        float pctHealth = currentHealth / (float) fullHealth;
        DrawRect fill = new DrawRect(getHealthBarColor(), Paint.Style.FILL, 0);
        fill.setBounds(
                startX + inner_padding,
                startY + inner_padding,
                startX + inner_padding + pctHealth * (width - height * 0.1f),
                startY + height - inner_padding
        );
        drawQueue.push(fill);

        // Draw number
        String hpString = currentHealth + "/" + fullHealth;
        DrawText text = new DrawText(
                hpString,
                startX + width * 0.9f, startY + height * 0.85f,
                Color.GRAY,
                (int) (height * 0.8f)
        );
        drawQueue.push(text);
    }

    /*
    Calculates what color the health bar should be using the ratio of
    currentHealth to fullHealth
     */
    private int getHealthBarColor() {
        double ratio = currentHealth / (double) fullHealth;
        // green: 0, 255, blue (or lowerRed, upperGreen, blue)
        // yellow: 255, 255, blue (or upperRed, upperGreen, blue)
        // red: 255, 0, blue (or upperRed, lowerGreen, blue)
        double red = (UPPER_RED - LOWER_RED) / 2.0 - (ratio - 0.5) * (UPPER_RED - LOWER_RED);
        double green = (UPPER_GREEN - LOWER_GREEN) / 2.0 + (ratio - 0.5) * (UPPER_GREEN - LOWER_GREEN);
        return Color.rgb((int) red, (int) green, BLUE);
    }

    @Override
    public boolean handleEvent(MotionEvent e, Queue<UIInputId> createdInput) {
        // This element is not interactable
        return false;
    }
}
