package com.plainsimple.spaceships.helper;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

/**
 * Animates a sprite's healthbar fading in and out above a sprite.
 * Meant to be played when a sprite loses health.
 */

public class HealthBarAnimation {

    // whether health bar is currently showing
    private boolean isShowing;
    // frame count on animation (0 if not in progress)
    private int frameCounter;
    // x-offset from sprite x-coordinate
    private float offsetX;
    // y-offset from sprite y-coordinate
    private float offsetY;
    private int maxHP;
    private int currentHP;
    // calculated healthbar dimensions (px)
    private float healthBarWidth;
    private float healthBarHeight;
    // width of healthbar outline
    private float innerPadding;
    // define width, height, and elevation of health bar relative to sprite demensions
    private static final float WIDTH_RATIO = 0.8f;
    private static final float HEIGHT_RATIO = 0.1f;
    private static final float ELEVATION_RATIO = 0;
    // number of frames to fade in and stay, respectively
    private static final int FRAMES_FADE = 6;
    private static final int FRAMES_STAY = 15;
    private static final int TOTAL_FRAMES = FRAMES_STAY + 2 * FRAMES_FADE;
    // color of healthbar outline
    private static final int OUTLINE_COLOR = Color.GRAY;
    // rgb values of OUTLINE_COLOR broken down
    private final int outlineR = Color.red(OUTLINE_COLOR);
    private final int outlineG = Color.green(OUTLINE_COLOR);
    private final int outlineB = Color.blue(OUTLINE_COLOR);

    public HealthBarAnimation(float spriteWidth, float spriteHeight, int spriteMaxHP) {
        healthBarWidth = spriteWidth * WIDTH_RATIO;
        healthBarHeight = spriteHeight * HEIGHT_RATIO;
        offsetX = (spriteWidth - healthBarWidth) / 2;
        offsetY = spriteHeight * (ELEVATION_RATIO + HEIGHT_RATIO);
        maxHP = spriteMaxHP;
        currentHP = spriteMaxHP;
        innerPadding = healthBarHeight * 0.2f;
    }

    // signals the animation should start playing, or refresh if it is already playing
    public void start() {
        if (isShowing) {
            refresh();
        }
        isShowing = true;
    }

    // refreshes the frameCount
    public void refresh() {
        if (frameCounter >= 0 && frameCounter < FRAMES_FADE) {
            // do nothing--keep fading in
        } else if (frameCounter >= FRAMES_FADE && frameCounter <= FRAMES_FADE + FRAMES_STAY) {
            // healthbar currently fully faded-in: reset counter to FRAMES_STAY
            frameCounter = FRAMES_FADE;
        } else if (frameCounter < TOTAL_FRAMES){ // animation was fading out: fade back in by inverting frame count
            frameCounter = Math.abs(TOTAL_FRAMES - frameCounter);
        } else {
            frameCounter = 0;
        }
    }

    // updates the animation if it is playing, including shifting it to the given sprite coordinates.
    // Adds the animation's DrawParams to the given list.
    public void updateAndDraw(float spriteX, float spriteY, int spriteHP, List<DrawParams> spriteParams) {
        if (frameCounter == TOTAL_FRAMES) { // reset
            frameCounter = 0;
            isShowing = false;
        } else if (isShowing) {
            frameCounter++;
            this.currentHP = spriteHP;
            // top-left drawing coordinates of healthbar
            float x0 = spriteX + offsetX;
            float y0 = spriteY - offsetY;
            int alpha = calculateAlpha();
            int outline_color = Color.argb(alpha, outlineR, outlineG, outlineB);
            // draw healthbar outline
            spriteParams.add(new DrawRect(x0, y0, x0 + healthBarWidth, y0 + healthBarHeight,
                    outline_color, Paint.Style.STROKE, innerPadding));
            // draw healthbar fill
            float width = (healthBarWidth - 2 * innerPadding) * ((float) currentHP / maxHP);
            int fill_color = getFillColor(currentHP, maxHP, alpha);
            spriteParams.add(new DrawRect(x0 + innerPadding, y0 + innerPadding,
                    x0 + innerPadding + width, y0 + healthBarHeight - innerPadding,
                    fill_color, Paint.Style.FILL, innerPadding));
        }
    }

    // calculates alpha of healthbar (for fade-in animation)
    public int calculateAlpha() {
        // fading in: calculate alpha
        if (frameCounter < FRAMES_STAY) {
            return (int) (frameCounter / (float) FRAMES_STAY * 255);
        } else if (frameCounter > FRAMES_STAY + FRAMES_FADE) {
            return (int) ((TOTAL_FRAMES - frameCounter) * (255.0f / FRAMES_FADE));
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