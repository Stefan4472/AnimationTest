package plainsimple.spaceships;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stefan on 8/18/2015.
 */
public class Background {

    // available tiles. Element index is Tile ID
    private BufferedImage[] tiles;
    private int numTiles;

    // grid of tile ID's instructing how to display background
    private byte[][] background;

    // whether to re-render background
    private boolean render;

    // last-rendered image
    private BufferedImage renderedImage;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private int x;

    // dimensions of tiles
    private int tileWidth;
    private int tileHeight;

    private final Random random;

    // construct tiles using names of tile files
    public Background(String[] tiles) {
        this.tiles = new BufferedImage[tiles.length];
        numTiles = tiles.length;
        for(int i = 0; i < tiles.length; i++) {
            try {
                this.tiles[i] = ImageIO.read(new File(tiles[i]));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        this.x = 0;

        tileWidth = this.tiles[0].getWidth(null);
        tileHeight = this.tiles[0].getHeight(null);

        random = new Random();

        background = new byte[SCREEN_HEIGHT / tileHeight][SCREEN_WIDTH / tileWidth + 1];
        render = true;
        renderedImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);

        // fill background randomly with space tiles
        for(int i = 0; i < background.length; i++) {
            for(int j = 0; j < background[i].length; j++) {
                background[i][j] = (byte) random.nextInt(numTiles);
            }
        };
    }

    // shifts screen x units left, renders and returns current background
    public BufferedImage render() {
        // only redraws background if it has been scrolled.
        // otherwise returns last-rendered background
        if(render) {
            Graphics2D g = renderedImage.createGraphics();
            int w_offset = getWOffset();

            for (int i = 0; i < 6; i++) { // rows
                for (int j = 0; j < 13; j++) { // columns
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight;

                    g.drawImage(tiles[background[i][(j + getWTile()) % 13]], loc_x, loc_y, null);
                }
            }
            render = false;
        }
        return renderedImage;
    }

    public int getX() { return x; }

    // current horizontal tile
    private int getWTile() { return x / tileWidth; }

    // distance, in pixels, from top left of current tile
    private int getWOffset() { return x % tileWidth; }

    // moves background x units left giving the
    // appearance of forward motion
    public void scroll(int x) {
        this.x += x;
        render = true;
    }
}
