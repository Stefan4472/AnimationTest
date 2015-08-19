import javax.imageio.ImageIO;
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
        // element zero must be left empty
        this.tiles = new BufferedImage[tiles.length + 1];
        for(int i = this.tiles.length - 1; i > 0; i--) {
            try {
                this.tiles[i] = ImageIO.read(new File(tiles[i - 1]));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        this.x = 0;
        this.y = 0;

        tileWidth = this.tiles[1].getWidth(null);
        tileHeight = this.tiles[1].getHeight(null);

        currentImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        random = new Random();

        background = new byte[6][24];

        // fill background randomly with space tiles
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 24; j++) {
                background[i][j] = (byte) random.nextInt(this.tiles.length);
                System.out.print(background[i][j] + "\t");
            }
            System.out.println();
        };

        render();
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    // renders current background to display and refreshes currentImage
    public Image render() {
        Graphics2D g = currentImage.createGraphics();

        int w_offset = getWOffset();
        int h_offset = getHOffset();

        for(int i = 0; i < 6; i++) { // rows
            for(int j = 0; j < 13; j++) { // columns
                if(background[i + getHTile()][(j + getWTile()) % 24] != 0){
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight - h_offset;
                    System.out.println("Drawing tile " + (background[i + getHTile()][(j + getWTile()) % 24]) + " " + (i + getHTile()) + "," + ((j + getWTile()) % 24) + " at " + loc_x + "," + loc_y);
                    g.drawImage(tiles[background[i + getHTile()][(j + getWTile()) % 24]], loc_x, loc_y, null);
                }
            }
        }
        return currentImage;
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
        render();
    }

    public ArrayList<Sprite> getObstacles() {
        return null;
    }
}
