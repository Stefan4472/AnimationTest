package com.galaxyrun.engine.background;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

import androidx.annotation.ColorInt;

/**
 * Generates images ("panels") to be used for the game background.
 * Panels are rendered using GalaxyDraw and transition the background color
 * over time.
 */
public class BackgroundGenerator {
    // Width of panels to generate.
    private final int panelWidthPx;
    // Height of panels to generate.
    private final int panelHeightPx;
    // Random number generator used when generating panels.
    private final Random rand;
    // Used to render a starry "galaxy" background.
    private final GalaxyDrawer galaxyDrawer;
    // A sequence of colors to use for the background of the next N panels.
    // Each panel starts with color `i` and transitions to the color at `i+1`.
    // For example, the first panel to be generated will use `startColor=colors[0]`,
    // `endColor=colors[1]`. For a `colors` array of size N, we can generate N-1
    // panels. Once we reach the end of the colors array, we generate the next one.
    private @ColorInt int[] colors;
    // The number of panels that have been generated using the current `colors` array.
    private int count;

    @ColorInt
    private static final int STANDARD_COLOR = Color.BLACK;
    // The number of panels of the `STANDARD_COLOR` to generate at the start of the game.
    private static final int NUM_STANDARD_AT_START = 2;
    // Number of panels over which the transition will occur.
    private static final int NUM_PANELS_TRANSITION = 2;
    // Number of panels to stay at the chosen color before transitioning
    // back to `STANDARD_COLOR`.
    private static final int NUM_PANELS_STAY = 1;

    BackgroundGenerator(int panelWidthPx, int panelHeightPx, Random rand) {
        this.panelWidthPx = panelWidthPx;
        this.panelHeightPx = panelHeightPx;
        this.rand = rand;
        galaxyDrawer = new GalaxyDrawer(this.rand);
        colors = ColorGenerator.makeSolidColor(STANDARD_COLOR, NUM_STANDARD_AT_START+1);
    }

    public Bitmap nextPanel() {
        if (count == colors.length - 1) {
            colors = generateColorTransition(Color.BLACK, Color.BLUE);
            count = 0;
        }
        // Transition from `colors[count]` to `colors[count+1]`.
        GalaxyDrawOptions options = new GalaxyDrawOptions(
                /*startColor=*/colors[count],
                /*endColor=*/colors[count+1],
                /*starDensity=*/2,
                /*starColor=*/Color.argb(200, 255, 255, 238),
                /*starSize=*/2,
                /*sizeVariance=*/0.5f,
                /*colorVariance=*/0.3f
        );
        ++count;
        return galaxyDrawer.drawGalaxy(this.panelWidthPx, this.panelHeightPx, options);
    }

    // Generates colors for a color transition.
    // Transitions from `startColor` to `toColor` over `NUM_PANELS_TRANSITION` panels.
    // Then stays at `toColor` for NUM_PANELS_STAY.
    // Then transitions back to `startColor` over `NUM_PANELS_TRANSITION` panels.
    @ColorInt
    private int[] generateColorTransition(@ColorInt int startColor, @ColorInt int toColor) {
        int[] transition =
                ColorGenerator.makeTransition(startColor, toColor, NUM_PANELS_TRANSITION+1);
        int[] stay = ColorGenerator.makeSolidColor(toColor, NUM_PANELS_STAY);
        int[] returnTransition =
                ColorGenerator.makeTransition(toColor, startColor, NUM_PANELS_TRANSITION+1);

        // Merge the three arrays in sequence.
        int[] merged = new int[transition.length + stay.length + returnTransition.length];
        System.arraycopy(transition, 0, merged, 0, transition.length);
        System.arraycopy(stay, 0, merged, transition.length, stay.length);
        System.arraycopy(returnTransition, 0, merged, transition.length + stay.length, returnTransition.length);
        return merged;
    }
}
