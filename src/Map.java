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

    // number of tiles elapsed since last map was generated
    private int mapTileCounter;

    // keeps track of tile spaceship was on last time scroll() was called
    private int lastTile;

    // speed for "stationary" tiles to scroll across the map
    private float scrollSpeed;

    // starting scroll speed
    private final float startingScrollSpeed = -4.0f;

    // generated sprites
    private ArrayList<Sprite> tiles;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // difficulty level
    private float difficulty;

    // amount to increment difficulty by each frame
    private final float difficultyIncrement = 0.001f;

    // coordinates of upper-left of "window" being shown
    private int x;

    // dimensions of mapTiles
    private int tileWidth;
    private int tileHeight;

    private Random random;

    public ArrayList<Sprite> getTiles(){
        return tiles;
    }

    public float getScrollSpeed() { return scrollSpeed; }

    // construct mapTiles using names of tile files
    public Map(Sprite[] mapTiles) {
        // element zero must be left empty
        this.mapTiles = new Sprite[mapTiles.length + 1];
        System.arraycopy(mapTiles, 0, this.mapTiles, 1, this.mapTiles.length - 1);

        initMap();
    }

    private void initMap() {
        this.x = 0;
        tiles = new ArrayList<>();
        scrollSpeed = startingScrollSpeed;
        difficulty = 0.0f;

        tileWidth = this.mapTiles[1].getCurrentImage().getWidth(null);
        tileHeight = this.mapTiles[1].getCurrentImage().getHeight(null);

        random = new Random();

        // generate 14 tiles of empty background to start
        map = generateEmptyBackground(6);
        mapTileCounter = 0;
        lastTile = 0;
    }

    // current horizontal tile
    private int getWTile() { return x / tileWidth; }

    // adds any new tiles and generates a new set of tiles if needed
    public void update() {
        this.x += scrollSpeed;
        difficulty += difficultyIncrement;
        // todo: update scrollSpeed

        // perform rendering if spaceship has changed tiles
        if(getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty tiles in the current row at the edge of the screen
                if (map[i][mapTileCounter] != 0) {
                    addTile(getMapTile(map[i][mapTileCounter]), SCREEN_WIDTH, i * tileWidth);
                }
            }
            mapTileCounter++;

            // generate more tiles
            if (mapTileCounter == map[0].length) {
                map = MapGenerator.generateTiles(6, 10, difficulty);
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    //
    private Sprite getMapTile(int index) {
        // todo: better way of setting speed. Shouldn't be a combined function
        Sprite tile = mapTiles[index];
        if(tile instanceof Obstacle) {
            tile = new Obstacle("obstacle_tile.png");
            tile.setSpeedX(scrollSpeed);
            return tile;
        }
        return null;
    }

    // adds sprite to arraylist and sets specified coordinates
    private void addTile(Sprite s, int x, int y) {
        s.setX(x);
        s.setY(y);
        tiles.add(s);
    }

    // generates tiles of empty background for length tiles ahead
    private byte[][] generateEmptyBackground(int length) {
        // init Byte array to zero by default
        return new byte[SCREEN_HEIGHT / tileHeight][length];
    }
}
