package com.plainsimple.spaceships.helper;

import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Animates a sprite's healthbar fading in and out above a sprite.
 * Meant to be played when a sprite loses health.
 */
// TODO: THIS IS A WORK-IN-PROGRESS
public class HealthBarAnimation {

    // whether health bar is currently showing
    private boolean isShowing;
    // counts number of milliseconds elapsed
    private int timeShownMs;
    // x-offset from sprite x-coordinate
    private float offsetX;
    // y-offset from sprite y-coordinate
    private float offsetY;
    private int maxHealth;
    private int currentHealth;
    // calculated healthbar dimensions (px)
    private float healthBarWidth;
    private float healthBarHeight;
    // width of healthbar outline
    private float innerPadding;
    // define width, height, and elevation of health bar relative to sprite dimensions
    private static final float WIDTH_RATIO = 0.1f;
    private static final float HEIGHT_RATIO = 0.05f;
    private static final float ELEVATION_RATIO = 0;
    // number of frames to fade in and stay, respectively
    private static final int DURATION_FADE_MS = 500;
    private static final int DURATION_STAY_MS = 1000;
    private static final int TOTAL_DURATION_MS = DURATION_FADE_MS + DURATION_STAY_MS + DURATION_FADE_MS;
    // color of healthbar outline
    private static final int OUTLINE_COLOR = Color.GRAY;
    // rgb values of OUTLINE_COLOR broken down
    private final int outlineR = Color.red(OUTLINE_COLOR);
    private final int outlineG = Color.green(OUTLINE_COLOR);
    private final int outlineB = Color.blue(OUTLINE_COLOR);

    // TODO: SHOULD BE BASED ON GAME WIDTH AND HEIGHT
    public HealthBarAnimation(
            int gameWidthPx,
            int gameHeightPx,
            int spriteWidth,
            int spriteHeight,
            int spriteMaxHP
    ) {
        healthBarWidth = gameWidthPx * WIDTH_RATIO;
        healthBarHeight = gameHeightPx * HEIGHT_RATIO;
        offsetX = (spriteWidth - healthBarWidth) / 2;
        offsetY = spriteHeight * (ELEVATION_RATIO + HEIGHT_RATIO);
        maxHealth = spriteMaxHP;
        currentHealth = spriteMaxHP;
        innerPadding = healthBarHeight * 0.2f;
    }

    // signals the animation should start playing, or refresh if it is already playing
    public void start() {
        if (isShowing) {
            refresh();
        } else {
            isShowing = true;
            timeShownMs = 0;
        }
    }

    // refreshes the frameCount
    public void refresh() {
        if (timeShownMs >= 0 && timeShownMs < DURATION_FADE_MS) {
            // do nothing--keep fading in
        } else if (timeShownMs >= DURATION_FADE_MS && timeShownMs <= DURATION_FADE_MS + DURATION_STAY_MS) {
            // healthbar currently fully faded-in: reset counter to FRAMES_STAY
            timeShownMs = DURATION_FADE_MS;
        } else if (timeShownMs < TOTAL_DURATION_MS){ // animation was fading out: fade back in by inverting frame count
            timeShownMs = Math.abs(TOTAL_DURATION_MS - timeShownMs);
        } else {
            timeShownMs = 0;
        }
    }

    private DrawRect DRAW_OUTLINE = new DrawRect(Color.GRAY, Paint.Style.STROKE, innerPadding);
    private DrawRect DRAW_FILL = new DrawRect(Color.GREEN, Paint.Style.FILL, innerPadding);

    // TODO: MOVE MORE LOGIC INTO `UPDATE()`
    // updates the animation if it is playing, including shifting it to the given sprite coordinates.
    public void update(long milliseconds) {
        if (timeShownMs >= TOTAL_DURATION_MS) { // reset
            isShowing = false;
        } else if (isShowing) {
            timeShownMs += milliseconds;
        }
    }

    public void setHealth(int health) {
        this.currentHealth = health;
    }

    public void getDrawParams(
            double spriteX,
            double spriteY,
            int spriteHP,
            ProtectedQueue<DrawParams> drawQueue
    ) {
        if (isShowing) {
            // top-left drawing coordinates of healthbar
            double x0 = spriteX + offsetX;
            double y0 = spriteY - offsetY;
            int alpha = calculateAlpha();
            int outline_color = Color.argb(alpha, outlineR, outlineG, outlineB);

            // draw healthbar outline
            DRAW_OUTLINE.setBounds(new Rectangle(x0, y0, x0 + healthBarWidth, y0 + healthBarHeight));
            DRAW_OUTLINE.setColor(outline_color);
            drawQueue.push(DRAW_OUTLINE);

            // draw healthbar fill
            float width = (healthBarWidth - 2 * innerPadding) * ((float) currentHealth / maxHealth);
            DRAW_FILL.setBounds(new Rectangle(
                    x0 + innerPadding,
                    y0 + innerPadding,
                    x0 + innerPadding + width,
                    y0 + healthBarHeight - innerPadding
            ));
            DRAW_FILL.setColor(getFillColor(currentHealth, maxHealth, alpha));
            drawQueue.push(DRAW_FILL);
        }
    }

    // calculates alpha of healthbar (for fade-in animation)
    public int calculateAlpha() {
        // fading in: calculate alpha
        if (timeShownMs < DURATION_STAY_MS) {
            return (int) (255 * (timeShownMs / (double) DURATION_STAY_MS));
        } else if (timeShownMs > DURATION_FADE_MS + DURATION_STAY_MS) {
            return (int) (255 * (TOTAL_DURATION_MS - timeShownMs) / (double) DURATION_FADE_MS);
        } else {
            return 255;
        }
    }

    // calculates fill color of healthbar based on hp and pre-calculated alpha
    public int getFillColor(int currentHP, int maxHP, int alpha) {
        float ratio = (float) currentHP / maxHP;
        int color;
        if (ratio > 0.7f) {
            color = Color.GREEN;
        } else if (ratio > 0.3f) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public boolean isShowing() {
        return isShowing;
    }
}