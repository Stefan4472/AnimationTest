package com.galaxyrun.engine.background;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

import androidx.annotation.ColorInt;

/**
 * Generates "panels" of background that transition from `START_COLOR` to `END_COLOR`
 * over the course of `NUM_PANELS_TRANSITION` panels.
 */
public class BackgroundGenerator {
    private final int panelWidthPx;
    private final int panelHeightPx;
    // Used to render a starry "galaxy" background
    private final GalaxyDrawer galaxyDrawer;
    // The number of panels that have been generated so far.
    private int count;

    // Color of the left on the first panel.
    @ColorInt
    private static final int START_COLOR = Color.BLACK;
    // Color of the left on the final panel.
    @ColorInt
    private static final int END_COLOR = Color.BLUE;
    // Number of panels over which the transition will occur.
    private static final int NUM_PANELS_TRANSITION = 10;

    BackgroundGenerator(int panelWidthPx, int panelHeightPx, Random rand) {
        this.panelWidthPx = panelWidthPx;
        this.panelHeightPx = panelHeightPx;
        galaxyDrawer = new GalaxyDrawer(rand);
    }

    Bitmap nextPanel() {
        int dA = (Color.alpha(END_COLOR) - Color.alpha(END_COLOR)) / NUM_PANELS_TRANSITION;
        int dR = (Color.red(END_COLOR) - Color.red(START_COLOR)) / NUM_PANELS_TRANSITION;
        int dG = (Color.green(END_COLOR) - Color.green(START_COLOR)) / NUM_PANELS_TRANSITION;
        int dB = (Color.blue(END_COLOR) - Color.blue(START_COLOR)) / NUM_PANELS_TRANSITION;

        int currA = Color.alpha(START_COLOR) + count * dA;
        int currR = Color.red(START_COLOR) + count * dR;
        int currG = Color.green(START_COLOR) + count * dG;
        int currB = Color.blue(START_COLOR) + count * dB;

        if (count < NUM_PANELS_TRANSITION) {
            ++count;
        }

        GalaxyDrawOptions options = new GalaxyDrawOptions(
                /*startColor=*/Color.argb(currA, currR, currG, currB),
                /*endColor=*/Color.argb(currA + dA, currR + dR, currG + dG, currB + dB),
                /*starDensity=*/2,
                /*starColor=*/Color.argb(200, 255, 255, 238),
                /*starSize=*/2,
                /*sizeVariance=*/0.5f,
                /*colorVariance=*/0.3f
        );
        return galaxyDrawer.drawGalaxy(this.panelWidthPx, this.panelHeightPx, options);
    }
}
