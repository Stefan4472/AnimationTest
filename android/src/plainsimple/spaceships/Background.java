package plainsimple.spaceships;

import android.graphics.*;
import android.util.Log;
import plainsimple.galaxydraw.DrawSpace;

import java.util.Random;

/**
 * Created by Stefan on 8/18/2015.
 */
public class Background {

    // rendered space background tiles
    private Bitmap[] imageTiles;
    // number of pixels scrolled
    private int scrollCounter;
    // width of rendered background tiles (px)
    private static final int TILE_WIDTH = 50;
    // height of rendered background tiles (px)
    private int tileHeight;
    // used to render space background
    private DrawSpace drawSpace;

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
        imageTiles = new Bitmap[screenW / TILE_WIDTH + 1]; // todo: what if screenW is a multiple of TILE_WIDTH?
        scrollCounter = 0;

        drawSpace = new DrawSpace();
        //drawSpace.setAntiAlias(false);
        drawSpace.setVariance(0.2);
        drawSpace.setDensity(3);
        drawSpace.setStarSize(5);
        //drawSpace.setUseGradient(true);
        //drawSpace.setBackgroundGradient(new LinearGradient(0, 0, screenW, 0, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP));
        for (int i = 0; i < imageTiles.length; i++) {
            imageTiles[i] = drawSpace.drawSpace(TILE_WIDTH, tileHeight);
        }
    }

    // draws background on canvas
    public void draw(Canvas canvas) {
        int start_tile = getStartTile();
        int end_tile = (start_tile == 0 ? imageTiles.length - 1 : start_tile - 1);
        if (getOffset() == 0) {
            drawSpace.drawSpace(imageTiles[end_tile]);
        }
        for (int i = 0; i < imageTiles.length; i++) {
            canvas.drawBitmap(imageTiles[(start_tile + i) % imageTiles.length], getOffset() + i * TILE_WIDTH, 0, null);
        }
    }

    // increases scroll counter by x
    public void scroll(int x) {
        this.scrollCounter += x;
    }
}
