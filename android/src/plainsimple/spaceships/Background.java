package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

/**
 * Created by Stefan on 8/18/2015.
 */
public class Background {

    // available tiles. Element index is Tile ID
    private Bitmap[] tiles;
    // grid of tile ID's instructing how to display background
    private byte[][] background;
    // whether to re-draw background
    private boolean render = true;
    // last-rendered image
    private Bitmap renderedImage;
    // dimensions of screen display
    private int screenW;
    private int screenH;
    // coordinates of upper-left of "window" being shown
    private int x = 0;
    // dimension of tile (square)
    private int tileWidth;
    private int tileHeight;
    private static final Random random = new Random();

    public int getX() {
        return x;
    }

    // current horizontal tile
    private int getWTile() {
        return x / tileWidth;
    }

    // distance, in pixels, from top left of current tile
    private int getWOffset() {
        return x % tileWidth;
    }

    // construct tiles using names of tile files
    public Background(int screenW, int screenH, float scaleW, float scaleH, Bitmap[] tiles) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.tiles = tiles;
        scaleResources(scaleW, scaleH);

        background = new byte[screenH / tileHeight][screenW / tileWidth + 1];
        renderedImage = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);


        // fill background randomly with space tiles
        for (int i = 0; i < background.length; i++) {
            for (int j = 0; j < background[i].length; j++) {
                background[i][j] = (byte) random.nextInt(tiles.length);
            }
        }
    }

    // scales tiles to proper size
    private void scaleResources(float scaleW, float scaleH) {
        tileWidth = (int) (tiles[0].getWidth() * scaleW);
        tileHeight = (int) (tiles[0].getHeight() * scaleH);
        for(int i = 0; i < tiles.length; i++) {
            tiles[i] = Bitmap.createScaledBitmap(tiles[i], (int) (tiles[i].getWidth() * scaleW),
                    (int) (tiles[i].getHeight() * scaleH), true);
        }
    }

    // shifts screen x units left, renders and returns current background
    public Bitmap getBitmap() {
        // only redraws background if it has been scrolled.
        // otherwise returns last-rendered background
        if (render) {
            Canvas canvas = new Canvas(renderedImage);
            int w_offset = getWOffset();

            for (int i = 0; i < background.length; i++) { // rows
                for (int j = 0; j < background[1].length; j++) { // columns
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight;
                    //Log.d("Background Class", i + "," + j + ",(" + loc_x + "," + loc_y + ")" + background.length + "," + background[0].length);
                    canvas.drawBitmap(tiles[background[i][(j + getWTile()) % background[0].length]], loc_x, loc_y, null);
                }
            }
            render = false;
        }
        return renderedImage;
    }

    // moves background x units left giving the
    // appearance of forward motion
    public void scroll(int x) {
        this.x += x;
        render = true;
    }
}
