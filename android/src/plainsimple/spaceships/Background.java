package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by Stefan on 8/18/2015.
 */
public class Background {

    private Context context;
    // available tiles. Element index is Tile ID
    private Bitmap[] tiles;
    // grid of tile ID's instructing how to display background
    private byte[][] background;
    // whether to re-draw background
    private boolean render;
    // last-rendered image
    private Bitmap renderedImage;
    // dimensions of screen display
    private int screenW;
    private int screenH;
    // coordinates of upper-left of "window" being shown
    private int x = 0;
    // dimensions of tiles
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
    public Background(int screenW, int screenH, float scaleW, float scaleH, Context context) {
        this.screenW = screenW;
        this.screenH = screenH;
        initResources(scaleW, scaleH);

        background = new byte[screenH / tileHeight][screenW / tileWidth + 1];
        render = true;
        renderedImage = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);


        // fill background randomly with space tiles
        for (int i = 0; i < background.length; i++) {
            for (int j = 0; j < background[i].length; j++) {
                background[i][j] = (byte) random.nextInt(tiles.length);
            }
        }
    }

    private void initResources(float scaleW, float scaleH) {
        tiles = new Bitmap[4];
        tiles[0] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.space1_tile);
        tiles[1] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.space2_tile);
        tiles[2] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.space3_tile);
        tiles[3] = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.space4_tile);

        tileWidth = (int) (tiles[0].getWidth() * scaleW);
        tileHeight = (int) (tiles[0].getHeight() * scaleH);

        tiles[0] = Bitmap.createScaledBitmap(tiles[0], (int) (tiles[0].getWidth() * scaleW),
                (int) (tiles[0].getHeight() * scaleH), true);
        tiles[1] = Bitmap.createScaledBitmap(tiles[1], (int) (tiles[1].getWidth() * scaleW),
                (int) (tiles[1].getHeight() * scaleH), true);
        tiles[2] = Bitmap.createScaledBitmap(tiles[2], (int) (tiles[2].getWidth() * scaleW),
                (int) (tiles[2].getHeight() * scaleH), true);
        tiles[3] = Bitmap.createScaledBitmap(tiles[3], (int) (tiles[3].getWidth() * scaleW),
                (int) (tiles[3].getHeight() * scaleH), true);
    }

    // shifts screen x units left, renders and returns current background
    public Bitmap getBitmap() {
        // only redraws background if it has been scrolled.
        // otherwise returns last-rendered background
        if (render) {
            Canvas canvas = new Canvas(renderedImage);
            int w_offset = getWOffset();

            for (int i = 0; i < background[0].length; i++) { // rows
                for (int j = 0; j < background[1].length; j++) { // columns
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight;

                    canvas.drawBitmap(tiles[background[i][(j + getWTile()) % 13]], loc_x, loc_y, null);
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
