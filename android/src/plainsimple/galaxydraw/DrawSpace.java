package plainsimple.galaxydraw;

import android.graphics.*;
import android.util.Log;

import java.util.Random;

/**
 * Renders an image of a starry sky based on several parameters.
 * Copyright(C) Plain Simple Apps 2015
 * See github.com/Plain-Simple/GalaxyDraw for more information.
 * Licensed under GPL GNU Version 3 (see license.txt)
 */
public class DrawSpace {

    // color of stars
    private int starColor;
    // color of background
    private int backgroundColor;
    // whether or not to use gradient
    private boolean useGradient;
    // gradient to use, if backgroundGradient = true
    private LinearGradient backgroundGradient;
    // stars per 2500 px (50*50 square)
    private double density;
    // radius of stars, in px
    private int starSize;
    // whether or not to use anti-alias when drawing stars
    private boolean antiAlias;
    // random variance from given values
    private double variance;
    // used for random number generation
    private Random random;
    // used for drawing
    private Paint paint;

    public int getStarColor() {
        return starColor;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getStarSize() {
        return starSize;
    }

    public void setStarSize(int starSize) {
        this.starSize = starSize;
    }

    public boolean isAntiAliased() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public boolean usesGradient() {
        return useGradient;
    }

    public void setUseGradient(boolean useGradient) {
        this.useGradient = useGradient;
    }

    public LinearGradient getBackgroundGradient() {
        return backgroundGradient;
    }

    public void setBackgroundGradient(LinearGradient backgroundGradient) {
        this.backgroundGradient = backgroundGradient;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    // init with default values
    public DrawSpace() {
        density = 5;
        starSize = 3;
        variance = 0.4;
        starColor = Color.argb(190, 255, 255, 238);
        backgroundColor = Color.BLACK;
        antiAlias = true;
        useGradient = false;
        random = new Random();
        paint = new Paint();
    }

    // creates Bitmap of given dimensions and renders space on it
    public Bitmap drawSpace(int imgWidth, int imgHeight) {
        Bitmap generated = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        drawSpace(new Canvas(generated));
        return generated;
    }

    // renders space on given Bitmap
    public void drawSpace(Bitmap bitmap) {
        drawSpace(new Canvas(bitmap));
    }

    // draws space on given canvas
    public void drawSpace(Canvas canvas) {
        drawBackground(canvas, canvas.getWidth(), canvas.getHeight());
        paint = new Paint();
        paint.setAntiAlias(antiAlias);
        int num_stars = (int) (canvas.getWidth() * canvas.getHeight() / 2500.0 * density);
        int brightness = Color.alpha(starColor);
        for (int i = 0; i < num_stars; i++) {
            drawStar(canvas, random.nextInt(canvas.getWidth()), random.nextInt(canvas.getHeight()),
                    varyBrightness(brightness, variance), varySize(starSize, variance));
        }
    }

    private void drawBackground(Canvas canvas, int imgWidth, int imgHeight) {
        if (useGradient) {
            paint.setShader(backgroundGradient);
            canvas.drawRect(0, 0, imgWidth, imgHeight, paint);
        } else {
            paint.setColor(backgroundColor);
            canvas.drawRect(0, 0, imgWidth, imgHeight, paint);
        }
    }

    private void drawStar(Canvas canvas, int x, int y, int brightness, int size) {
        paint.setColor(Color.argb(brightness, Color.red(starColor), Color.green(starColor), Color.blue(starColor)));
        Log.d("DrawSpace", "Color = " + paint.getAlpha() + "," + Color.red(starColor) + Color.green(starColor) + Color.blue(starColor));
        canvas.drawRect(x, y, x + size, y + size, paint);
    }

    private int varyBrightness(int value, double variance) {
        if (variance == 0) {
            return value;
        } else {
            int varied = value + (random.nextInt(2) == 0 ? 1 : -1) * random.nextInt((int) (value * variance * 100)) / 100;
            if (varied > 255) {
                return 255;
            } else {
                return varied;
            }
        }
    }

    private int varySize(int value, double variance) {
        if (variance == 0) {
            return value;
        } else {
            return value + (random.nextInt(2) == 0 ? 1 : -1) * random.nextInt((int) (value * variance * 100)) / 100;
        }
    }
}
