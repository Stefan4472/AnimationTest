import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Random;

/**
 * Auto-generation of sprites
 */
public class Map {

    // available mapTiles. Element index is Tile ID
    private Sprite[] mapTiles;

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    // generated sprites
    private ArrayList<Sprite> tiles;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private int x;

    // dimensions of mapTiles
    private int tileWidth;
    private int tileHeight;

    private Random random;

    // construct mapTiles using names of tile files
    public Map(Sprite[] mapTiles) {
        // element zero must be left empty
        this.mapTiles = new Sprite[mapTiles.length + 1];
        for (int i = this.mapTiles.length - 1; i > 0; i--) {
            this.mapTiles[i] = mapTiles[i - 1];
        }
        initMap();
    }

    private void initMap() {
        this.x = 0;
        tiles = new ArrayList<>();

        tileWidth = this.mapTiles[1].getCurrentImage().getWidth(null);
        tileHeight = this.mapTiles[1].getCurrentImage().getHeight(null);

        random = new Random();

        map = new byte[6][24];

        map[2][5] = 1;
        addTile(mapTiles[1], 250, 100);
    }

    // renders current map to display onto g and keeps track of sprites
    public void render(Graphics2D g, ImageObserver o) { // todo: adds sprites that are generated
        int w_offset = getWOffset();

        for(int i = 0; i < 6; i++) { // rows
            for(int j = 0; j < 13; j++) { // columns
                if(this.map[i][(j + getWTile()) % 24] != 0){
                    int loc_x = j * tileWidth - w_offset;
                    int loc_y = i * tileHeight;
                    //g.drawImage(mapTiles[this.map[i][(j + getWTile()) % 24]].getCurrentImage(), loc_x, loc_y, o);
                    //addTile(mapTiles[this.map[i][(j + getWTile()) % 24]], loc_x, loc_y); // each tile can only be added once
                }
            }
        }
    }

    // current horizontal and vertical tile
    private int getWTile() { return x / tileWidth; }

    // distance, in pixels, from top left of current tile
    private int getWOffset() { return x % tileWidth; }

    // moves map x units left and y units down, giving the
    // appearance of forward motion
    public void scroll(int x) {
        this.x += x;
        for(Sprite t : tiles) {
            t.setSpeedX(x);
        }
    }

    public ArrayList<Sprite> getTiles(){
        return tiles;
    }

    // adds sprite to arraylist and sets specified coordinates
    private void addTile(Sprite s, int x, int y) {
        s.setX(x);
        s.setY(y);
        tiles.add(s);
    }
}
