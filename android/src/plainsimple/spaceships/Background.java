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
    // coordinates of upper-left of "window" being shown
    private int x = 0;
    // tile side length (square)
    private int tileWidth;
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
    public Background(int screenW, int screenH, float scaleH, Bitmap[] tiles) {
        this.tiles = tiles;
        scaleResources(scaleH);

        background = new byte[(int) Math.ceil(screenH / tileWidth)][(int) Math.ceil(screenW / tileWidth)];

        // fill background randomly with space tiles
        for (int i = 0; i < background.length; i++) {
            for (int j = 0; j < background[i].length; j++) {
                background[i][j] = (byte) random.nextInt(tiles.length);
            }
        }
    }

    // scales tiles to proper size
    private void scaleResources(float scaleH) {
        tileWidth = (int) (tiles[0].getHeight() * scaleH);
        for(int i = 0; i < tiles.length; i++) {
            tiles[i] = Bitmap.createScaledBitmap(tiles[i], tileWidth, tileWidth, true);
        }
    }

    // shifts screen x units left, draws background to canvas
    public void draw(Canvas canvas) {
        int w_offset = getWOffset();

        for (int i = 0; i < background.length; i++) { // rows // todo: fix or simplify
            for (int j = 0; j < background[1].length; j++) { // columns
                int loc_x = j * tileWidth - w_offset;
                int loc_y = i * tileWidth;
                //Log.d("Background Class", i + "," + j + ",(" + loc_x + "," + loc_y + ")" + background.length + "," + background[0].length);
                canvas.drawBitmap(tiles[background[i][(j + getWTile()) % background[0].length]], loc_x, loc_y, null);
            }
        }
    }

    // moves background x units left giving the
    // appearance of forward motion
    public void scroll(int x) {
        this.x += x;
    }
}
