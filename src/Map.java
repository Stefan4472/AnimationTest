import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Auto-generation of sprites
 */
public class Map {

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    // number of rows of tiles that fit in map
    private int rows;

    // number of tiles elapsed since last map was generated
    private int mapTileCounter;

    // keeps track of tile spaceship was on last time scroll() was called
    private long lastTile;

    // speed for "stationary" tiles to scroll across the map
    private float scrollSpeed;

    // generated sprites
    private ArrayList<Sprite> tiles = new ArrayList<>();

    // board upon which Map lies
    private Board board;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private long x = 0;

    // dimensions of basic mapTiles
    private int tileWidth; // todo: what about bigger/smaller sprites?
    private int tileHeight;

    private Random random = new Random();

    public ArrayList<Sprite> getTiles(){ return tiles; }
    public float getScrollSpeed() { return scrollSpeed; }
    public void setBoard(Board board) { this.board = board; }

    public ArrayList<Sprite> getProjectiles() {
        return (ArrayList<Sprite>) tiles.stream()
                .filter(s -> s.getClass().equals(Alien.class))
                .map(s -> ((Alien) s).getProjectiles())
                .flatMap(b -> b.stream())
                .collect(Collectors.toList());
    }

    public Map(Board board) {
        this.board = board;
        initMap();
    }

    private void initMap() {
        tileWidth = 50;
        tileHeight = 50;
        rows = SCREEN_HEIGHT / tileHeight;

        map = new byte[][] {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0}
        };
        mapTileCounter = 0;
        lastTile = 0;
    }

    // current horizontal tile
    private long getWTile() { return x / tileWidth; }

    // number of pixels from start of current tile
    private int getWOffset() { return (int) x % tileWidth; }

    // adds any new tiles and generates a new set of tiles if needed
    public void update() {
        scrollSpeed = updateScrollSpeed();
        this.x += (int) scrollSpeed;

        // perform rendering if spaceship has changed tiles
        if(getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty tiles in the current column at the edge of the screen
                if (map[i][mapTileCounter] != 0) {
                    addTile(getMapTile(map[i][mapTileCounter], SCREEN_WIDTH + getWOffset(),  i * tileWidth),
                        (int) scrollSpeed, 0, board);
                }
            }
            mapTileCounter++;

            // generate more tiles
            if (mapTileCounter == map[0].length) {
                generateTiles();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    // calculates scrollspeed based on difficulty
    // difficulty starts at 0 and increases by 0.01/frame,
    // or 1 per second
    public float updateScrollSpeed() {
        scrollSpeed = (float) (-4.0f - board.getDifficulty() / 20);
        if(scrollSpeed < -20) {
            scrollSpeed = -20;
        }
        return scrollSpeed;
    }

    // returns sprite initialized to coordinates (x,y) given tileID
    private Sprite getMapTile(int tileID, double x, double y) throws IndexOutOfBoundsException {
        switch(tileID) {
            case 1:
                return new Obstacle("obstacle_tile.png", x, y, board);
            case 2:
                Sprite tile = new Obstacle("obstacle_tile.png", x, y, board);
                tile.setCollides(false);
                return tile;
            case 3:
                return new Coin("coin_tile.png", x, y, board);
            case 4:
                return new Alien("alien.png", x, y, 1, board);
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    // sets specified fields and adds sprite to arraylist
    private void addTile(Sprite s, double speedX, double speedY, Board board) {
        s.setSpeedX(speedX);
        s.setSpeedY(speedY);
        s.setBoard(board);
        tiles.add(s);
    }

    // difficulty determines:
    // type of obstacles (int(difficulty) determines range of tiles to use
    // frequency of obstacles
    // speed of oncoming obstacles
    // powerups and coins
    public void generateTiles() {
        if(getP(getPTile())) {
            if(getP(getPTunnel())) {
                map = generateTunnel();
            } else {
                map = generateObstacles();
            }
        } else {
            //if(getP(0.5)) {
                if(getP(getPAlienSwarm())) {
                    map = generateAlienSwarm();
                } else {
                    map = generateAlien();
                }
            //} else {
            //    map = generateAsteroid();
            //}
        }
    }

    // generates map cluster of simple obstacle
    private byte[][] generateObstacles() {
        int size = 10 + random.nextInt(5);
        byte[][] generated = new byte[rows][size + 2];
        for(int i = 0; i < size; i++) {
            if(getP(0.2f)) {
                int row = random.nextInt(rows);
                generated[row][i] = 1;
                if(getP(0.5) && i + 1 < size) {
                    generated[row][i + 1] = 1;
                }
                if(getP(0.3) && row + 1 < rows) {
                    generated[row + 1][i] = 1;
                }
                if(getP(0.2) && row > 0) {
                    generated[row - 1][i] = 1;
                }
            }
        }
        return generated;
    }

    // generates tunnel
    private byte[][] generateTunnel() {
        int tunnel_length = 15 + random.nextInt(10);
        byte[][] generated = new byte[rows][tunnel_length + 3];
        int row = 1 + random.nextInt(5);
        float change_path = 0.0f;
        // generate first column
        for(int i = 0; i < rows; i++) {
            if(i != row)
                generated[i][0] = 1;
        }
        for(int i = 1; i < tunnel_length; i++) {
            if(getP(change_path) && i < tunnel_length - 1) {
                change_path = -0.1f;
                int direction;
                if(getP(0.5f)) {
                    if(row < map.length - 1)
                        direction = 1;
                    else
                        direction = -1;
                } else {
                    if(row > 0)
                        direction = -1;
                    else
                        direction = 1;
                }
                for(int j = 0; j < rows; j++) {
                    if(j != row && j != row + direction) {
                        generated[j][i] = 1;
                        generated[j][i + 1] = 1;
                    }
                }
                i++;
                row += direction;
            } else {
                for(int j = 0; j < rows; j++) {
                    if(j < row - 1 || j > row + 1) {
                        generated[j][i] = 2;
                    } else if(j != row) {
                        generated[j][i] = 1;
                    }
                }
                change_path += 0.05f;
            }
        }
        return generated;
    }

    private byte[][] generateAlien() {
        int size = 4 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        generated[random.nextInt(6)][size - 1] = 4;
        return generated;
    }

    private byte[][] generateAlienSwarm() {
        int num_aliens = 2 + random.nextInt(3);
        int size = num_aliens * 8 + 1;
        byte[][] generated = new byte[rows][size];
        for(int i = 0; i < num_aliens; i++) {
            generated[random.nextInt(6)][8 * (i + 1)] = 4;
        }
        return generated;
    }

    // generates a coin trail on map
    private void generateCoins(byte[][] map) {
        int col = random.nextInt(map[0].length / 2);
        int end_col = map[0].length - random.nextInt(map[0].length / 4);
        // establish empty row to place first coin,
        // prioritizing rows closer to the middle
        int  row = 2 + random.nextInt(2);
        boolean row_found = false;
        for(int i = 0; i < 4  && !row_found; i++) {
            if(map[row + i][col] == 0 && row < 6) {
                row += i;
                row_found = true;
            } else if(map[row - i][col] == 0 && row > -1) {
                row -= i;
                row_found = true;
            }
        }
        for(int i = col; i < end_col; i++) {
            if(map[row][col] == 0) {
                map[row][col] = 3;
            }
        }

    }

    // give probability of an event occurring
    // uses random numbers and will return if event should
    // occur or not
    private boolean getP(double probability) {
        return random.nextInt(100) + 1 <= probability * 100;
    }

    // calculates and returns probability of a tile-based obstacle
    private double getPTile() {
        if(110 - board.getDifficulty() >= 50) {
            return (110 - board.getDifficulty()) / 100;
        } else {
            return 0.5;
        }
    }

    private double getPTunnel() {
        if(-10 + board.getDifficulty() < 50) {
            return (-10 + board.getDifficulty()) / 100;
        } else {
            return 0.5;
        }
    }

    // calculates and returns probability of aliens appearing in a swarm
    private double getPAlienSwarm() {
        return (-30 + board.getDifficulty()) / 100;
    }
}
