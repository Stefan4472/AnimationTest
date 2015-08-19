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

    // grid of tile ID's instructing how to display map
    private byte[][] map;

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

        tileWidth = this.tiles[1].getWidth(null);
        tileHeight = this.tiles[1].getHeight(null);

        random = new Random();

        map = new byte[6][24];

        // fill map randomly with tiles
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 24; j++) {
                map[i][j] = (byte) random.nextInt(this.tiles.length);
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        };
    }

    // renders current map to display and refreshes currentImage
    public Image render() {
        BufferedImage map = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = map.createGraphics();

        int w_offset = getWOffset();

        for(int i = 0; i < 6; i++) { // rows
            for(int j = 0; j < 13; j++) { // columns
                if(this.map[i][(j + getWTile()) % 24] != 0){
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight;
                    g.drawImage(tiles[this.map[i][(j + getWTile()) % 24]], loc_x, loc_y, null);
                }
            }
        }
        return map;
    }

    public int getX() { return x; }

    // current horizontal and vertical tile
    private int getWTile() { return x / tileWidth; }

    // distance, in pixels, from top left of current tile
    private int getWOffset() { return x % tileWidth; }

    // moves map x units left and y units down, giving the
    // appearance of forward motion
    // redraws and returns map
    public void scroll(int x) {
        this.x += x;
        render();
    }

    public ArrayList<Sprite> getObstacles() {
        return null;
    }
}
