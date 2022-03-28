package com.plainsimple.spaceships.helper;

import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * Draws a HealthBar for a limited amount of time above a Sprite.
 *
 * Call `triggerShow()` to trigger showing the HealthBar.
 */
public class HealthBarAnimation {

    // Coordinates of top-left of HealthBar
    private double x, y;
    // Current health being shown
    private int health;
    // Remaining time to show the HealthBar
    private long remainingShowTimeMs;

    // Offsets from sprite coordinates (px)
    private final double offsetX, offsetY;
    // Max health the sprite can have
    private final int maxHealth;
    // Calculated dimensions (px)
    private final double healthBarWidth, healthBarHeight;
    // Width of HealthBar outline
    private final int innerPadding;

    // Duration to show the HealthBar after `triggerShow()`
    private static final int DURATION_SHOW_MS = 1500;
    // Define width, height, and elevation of health bar relative to sprite dimensions
    private static final double WIDTH_RATIO = 1.0;
    private static final double HEIGHT_RATIO = 0.12;
    private static final double ELEVATION_RATIO = 0.05;
    // Color of HealthBar outline
    private static final int OUTLINE_COLOR = Color.GRAY;

    public HealthBarAnimation(Sprite sprite) {
        healthBarWidth = sprite.getWidth() * WIDTH_RATIO;
        healthBarHeight = sprite.getHeight() * HEIGHT_RATIO;
        offsetX = (sprite.getWidth() - healthBarWidth) / 2;
        offsetY = sprite.getHeight() * (ELEVATION_RATIO + HEIGHT_RATIO) * -1;
        maxHealth = sprite.getHealth();
        innerPadding = (int) (healthBarHeight * 0.2);
    }

    /*
    Trigger the HealthBar to show, or prolong it if it is already showing.
     */
    public void triggerShow() {
        remainingShowTimeMs = DURATION_SHOW_MS;
    }

    /*
    Update by `ms` milliseconds.
     */
    public void update(Sprite sprite, long ms) {
        x = sprite.getX() + offsetX;
        y = sprite.getY() + offsetY;
        health = sprite.getHealth();
        remainingShowTimeMs = remainingShowTimeMs > ms ? remainingShowTimeMs - ms : 0;
    }

    public void getDrawInstructions(ProtectedQueue<DrawInstruction> drawQueue) {
        if (remainingShowTimeMs > 0) {
            // Draw outline
            DrawRect drawOutline = new DrawRect(OUTLINE_COLOR, Paint.Style.STROKE, innerPadding);
            drawOutline.setBounds(new Rectangle(x, y, healthBarWidth, healthBarHeight));
            drawQueue.push(drawOutline);

            // Draw fill
            int fillColor = calcFillColor(health, maxHealth);
            double fillWidth = (healthBarWidth - 2 * innerPadding) * (1.0 * health / maxHealth);
            double fillHeight = (healthBarHeight - 2 * innerPadding);
            DrawRect drawFill = new DrawRect(fillColor, Paint.Style.FILL, 0);
            drawFill.setBounds(new Rectangle(
                    x + innerPadding,
                    y + innerPadding,
                    fillWidth,
                    fillHeight
            ));
            drawQueue.push(drawFill);
        }
    }

    /*
    Calculate fill color of the HealthBar based on ratio of health to maxHealth.
     */
    public static int calcFillColor(int health, int maxHealth) {
        double ratio = health * 1.0 / maxHealth;
        if (ratio > 0.7f) {
            return Color.GREEN;
        } else if (ratio > 0.3f) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }
}