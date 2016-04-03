package plainsimple.spaceships;

import android.graphics.*;
import android.util.Log;
import plainsimple.galaxydraw.DrawSpace;

/**
 * Draws the background of the game.
 * // todo: explanation
 */
public class Background {

    // rendered space background tiles
    private Bitmap[] imageTiles;
    // number of pixels scrolled
    private int pixelsScrolled;
    // width of rendered background tiles (px)
    private static final int TILE_WIDTH = 60;
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
    // index of element in backgroundColors being transitioned from
    private int fromElement;
    // index of element in backgroundColors being transitioned to
    private int toElement;


    public int getPixelsScrolled() {
        return pixelsScrolled;
    }

    // index of tile that will be left-most on the screen
    private int getStartTile() {
        return (pixelsScrolled / TILE_WIDTH) % imageTiles.length;
    }

    // offset of left-most tile on the screen from origin of canvas
    private int getOffset() {
        return -(pixelsScrolled % TILE_WIDTH);
    }

    public Background(int screenW, int screenH) {
        // todo: this renders an image longer than actual screen, could be optimized
        this.tileHeight = screenH;
        imageTiles = new Bitmap[screenW / TILE_WIDTH + 2]; // todo: what if screenW is a multiple of TILE_WIDTH?
        pixelsScrolled = 0;
        backgroundColors = new int[] { Color.BLACK, Color.WHITE };
        transitionDurations = new double[] { 10, 10 };
        fromElement = 0;
        toElement = 1;
        transitionCounter = 0;

        drawSpace = new DrawSpace();
        drawSpace.setAntiAlias(true);
        drawSpace.setVariance(0.2);
        drawSpace.setDensity(0); // was 3
        drawSpace.setStarSize(8);
        drawSpace.setUseGradient(true);
        for (int i = 0; i < imageTiles.length; i++) {
            imageTiles[i] = Bitmap.createBitmap(TILE_WIDTH, tileHeight, Bitmap.Config.ARGB_8888);
            drawNextTile(imageTiles[i]);
        }
    }

    // draws imageTiles[] onto canvas in correct locations
    public void draw(Canvas canvas) {
        int start_tile = getStartTile();
        int end_tile = (start_tile == 0 ? imageTiles.length - 1 : start_tile - 1);
        // draw space on the end tile every time a full tile has rotated through the screen
        if (getOffset() == 0) {
            drawNextTile(imageTiles[end_tile]);
        }
        for (int i = 0; i < imageTiles.length; i++) {
            canvas.drawBitmap(imageTiles[(start_tile + i) % imageTiles.length], getOffset() + i * TILE_WIDTH, 0, null);
        }
    }

    // draws space on next tile, incrementing values
    private void drawNextTile(Bitmap tile) {
        // transition has finished. Adjust fromElement and toElement and reset transitionCounter
        if (transitionCounter == transitionDurations[toElement]) {
            fromElement = toElement;
            //toElement = (toElement == backgroundColors.length - 1 ? 0 : toElement++);
            if (toElement == backgroundColors.length - 1) {
                toElement = 0;
            } else {
                toElement++;
            }
            transitionCounter = 0;
        }
        // color transitioning to
        int to_color = backgroundColors[toElement];
        // color transitioning from
        int from_color = backgroundColors[fromElement];
        // calculate argb of color that should be on the left of the gradient based on the difference between the
        // colors being transitioned, transitionDuration, and transitionCounter
        int left_color = Color.argb(
                Color.alpha(from_color) + (int) ((Color.alpha(to_color) - Color.alpha(from_color)) / transitionDurations[toElement] * transitionCounter),
                Color.red(from_color) + (int) ((Color.red(to_color) - Color.red(from_color)) / transitionDurations[toElement] * transitionCounter),
                Color.green(from_color) + (int) ((Color.green(to_color) - Color.green(from_color)) / transitionDurations[toElement] * transitionCounter),
                Color.blue(from_color) + (int) ((Color.blue(to_color) - Color.blue(from_color)) / transitionDurations[toElement] * transitionCounter)
        );
        // calculate argb of color that should be on the right of the gradient
        int right_color = Color.argb(
                Color.alpha(from_color) + (int) ((Color.alpha(to_color) - Color.alpha(from_color)) / transitionDurations[toElement] * (transitionCounter + 1)),
                Color.red(from_color) + (int) ((Color.red(to_color) - Color.red(from_color)) / transitionDurations[toElement] * (transitionCounter + 1)),
                Color.green(from_color) + (int) ((Color.green(to_color) - Color.green(from_color)) / transitionDurations[toElement] * (transitionCounter + 1)),
                Color.blue(from_color) + (int) ((Color.blue(to_color) - Color.blue(from_color)) / transitionDurations[toElement] * (transitionCounter + 1))
        );
        Log.d("Background Class", "Left is " + colorToString(left_color) + " Right is " + colorToString(right_color) + " counter = " + transitionCounter);
        drawSpace.setBackgroundGradient(new LinearGradient(0, 0, TILE_WIDTH, 0, left_color, right_color, Shader.TileMode.CLAMP));
        drawSpace.drawSpace(tile);
        /*Canvas c = new Canvas(tile);
        Paint p = new Paint();
        p.setColor(left_color);
        c.drawRect(0, 0, tile.getWidth(), tile.getHeight(), p);*/
        transitionCounter++;
    }

    private static String colorToString(int color) {
        return "A:" + Color.alpha(color) + " R:" + Color.red(color) + " G:" + Color.green(color) + " B:" + Color.blue(color);
    }

    // increases scroll counter by x
    public void scroll(int x) {
        this.pixelsScrolled += x;
    }
}
