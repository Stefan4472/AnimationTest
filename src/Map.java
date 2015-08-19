import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Auto-generation of sprites
 */
public class Map {

    // available tiles. Element index is Tile ID
    private BufferedImage[] tiles;

    // grid of tile ID's instructing how to display background
    private byte[][] background;

    private BufferedImage currentImage;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private int x;
    private int y;

    // dimensions of tiles
    private int tileWidth;
    private int tileHeight;

    private final Random random;

    // construct tiles using names of tile files
    public Map(String[] tiles) {
        this.tiles = new BufferedImage[tiles.length];
        for(int i = 0; i < tiles.length; i++) {
            try {
                this.tiles[i] = ImageIO.read(new File(tiles[i]));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        this.x = 0;
        this.y = 0;

        tileWidth = this.tiles[0].getWidth(null);
        tileHeight = this.tiles[0].getHeight(null);

        currentImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        random = new Random();

        background = new byte[6][24];

        // fill background randomly with space tiles
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 24; j++) {
                background[i][j] = (byte) random.nextInt(4);
            }
        };

        renderCurrentImage();
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    // renders current background to display and refreshes currentImage
    public void renderCurrentImage() {
        Graphics2D g = currentImage.createGraphics();

        int w_offset = getWOffset();
        int h_offset = getHOffset();

        for(int i = 0; i < 6; i++) { // rows
            for(int j = 0; j < 13; j++) { // columns
                int loc_x = j * tileWidth - w_offset;
                int loc_y = i * tileHeight - h_offset;

                g.drawImage(tiles[background[i + getHTile()][(j + getWTile()) % 24]], loc_x, loc_y, null);
            }
        }
    }

    public int getX() { return x; }

    // current horizontal and vertical tile
    private int getWTile() { return x / tileWidth; }
    private int getHTile() { return y / tileHeight; }

    // distance, in pixels, from top left of current tile
    private int getWOffset() { return x % tileWidth; }
    private int getHOffset() { return y % tileHeight; }

    // moves background x units left and y units down, giving the
    // appearance of forward motion
    // redraws and returns background
    public void scroll(int x, int y) {
        this.x += x;
        this.y += y;
        renderCurrentImage();
    }

    public ArrayList<Sprite> getObstacles() {
        return null;
    }
}
