package plainsimple.spaceships;

import android.graphics.*;
import android.util.Log;
import plainsimple.galaxydraw.DrawSpace;

/**
 * Created by Stefan on 8/18/2015.
 */
public class Background {

    // rendered space background tiles
    private Bitmap[] imageTiles;
    // number of pixels scrolled
    private int scrollCounter;
    // width of rendered background tiles (px)
    private static final int TILE_WIDTH = 200;
    // height of rendered background tiles (px)
    private int tileHeight;
    // used to render space background
    private DrawSpace drawSpace;
    // set of pre-defined colors to transition between
    private int[] backgroundColors;
    // number of tiles it takes to transition from one color to the next
    // for each element of backgroundColors. Must have same number of
    // elements as backgroundColors
    private double[] transitionDurations;
    // keeps track of how many tiles have been drawn during this transition
    private int transitionCounter;
    // current color on the left of the gradient
    private int currentColor;
    // index of element in backgroundColors being transitioned to
    private int currentElement;


    public int getScrollCounter() {
        return scrollCounter;
    }

    // index of tile that will be left-most on the screen
    private int getStartTile() {
        return (scrollCounter / TILE_WIDTH) % imageTiles.length;
    }

    // offset of left-most tile on the screen from origin of canvas
    private int getOffset() {
        return -(scrollCounter % TILE_WIDTH);
    }

    public Background(int screenW, int screenH) {
        // todo: this renders an image longer than actual screen, could be optimized
        this.tileHeight = screenH;
        imageTiles = new Bitmap[screenW / TILE_WIDTH + 2]; // todo: what if screenW is a multiple of TILE_WIDTH?
        scrollCounter = 0;
        backgroundColors = new int[] { Color.BLACK, Color.BLUE, Color.WHITE };
        transitionDurations = new double[] { 10, 10, 10 };
        currentColor = backgroundColors[0];
        currentElement = 1;
        transitionCounter = 0;

        drawSpace = new DrawSpace();
        drawSpace.setAntiAlias(true);
        drawSpace.setVariance(0.2);
        drawSpace.setDensity(3);
        drawSpace.setStarSize(8);
        drawSpace.setUseGradient(true);
        for (int i = 0; i < imageTiles.length; i++) {
            imageTiles[i] = Bitmap.createBitmap(TILE_WIDTH, tileHeight, Bitmap.Config.ARGB_8888);
            drawNextTile(imageTiles[i]);
        }
    }

    // draws background on canvas
    public void draw(Canvas canvas) {
        int start_tile = getStartTile();
        int end_tile = (start_tile == 0 ? imageTiles.length - 1 : start_tile - 1);
        // draw space on the end tile
        if (getOffset() == 0) {
            drawNextTile(imageTiles[end_tile]);
        }
        for (int i = 0; i < imageTiles.length; i++) {
            canvas.drawBitmap(imageTiles[(start_tile + i) % imageTiles.length], getOffset() + i * TILE_WIDTH, 0, null);
        }
    }

    // returns index of last element of backgroundColors used
    private int getLastBackgroundElement() {
        return currentElement == 0 ? backgroundColors.length - 1 : currentElement - 1;
    }

    // draws space on next tile, incrementing values
    private void drawNextTile(Bitmap tile) {
        Log.d("Background Class", "Transition Counter = " + transitionCounter + " Current Element " + currentElement + " Last Element " + getLastBackgroundElement());
        Log.d("Background Class", "Current is " + colorToString(currentColor));
        Log.d("Background Class", "Last is " + colorToString(backgroundColors[getLastBackgroundElement()]));
        Log.d("Background Class", "Moving to " + colorToString(backgroundColors[currentElement]));
        // transition has finished
        if (transitionCounter == transitionDurations[currentElement]) {
            currentElement = (currentElement == backgroundColors.length - 1 ? 0 : currentElement++);
            transitionCounter = 0;
        }
        // color transitioning to
        int to_color = backgroundColors[currentElement];
        // color transitioning from
        int from_color = backgroundColors[getLastBackgroundElement()];
        int left_color = currentColor;
        currentColor = Color.argb(
                (int) ((Color.alpha(to_color) - Color.alpha(from_color)) / transitionDurations[currentElement] * transitionCounter),
                (int) ((Color.red(to_color) - Color.red(from_color)) / transitionDurations[currentElement] * transitionCounter),
                (int) ((Color.green(to_color) - Color.green(from_color)) / transitionDurations[currentElement] * transitionCounter),
                (int) ((Color.blue(to_color) - Color.blue(from_color)) / transitionDurations[currentElement] * transitionCounter)
        );
        drawSpace.setBackgroundGradient(new LinearGradient(0, 0, TILE_WIDTH, 0, left_color, currentColor, Shader.TileMode.CLAMP));
        drawSpace.drawSpace(tile);
        transitionCounter++;
    }

    private static String colorToString(int color) {
        return "A:" + Color.alpha(color) + " R:" + Color.red(color) + " G:" + Color.green(color) + " B:" + Color.blue(color);
    }

    // increases scroll counter by x
    public void scroll(int x) {
        this.scrollCounter += x;
    }
}
