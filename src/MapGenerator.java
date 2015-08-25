import java.util.Random;

/**
 * Created by Stefan on 8/25/2015.
 */
public class MapGenerator {

    // number of non-empty available tiles
    private static final int mapTileSize = 1;

    // difficulty determines:
    // type of obstacles (int(difficulty) determines range of tiles to use
    // frequency of obstacles
    // speed of oncoming obstacles
    // powerups and coins
    public static byte[][] generateTiles(int rows, int col, float difficulty) {
        Random random = new Random();
        byte[][] tiles = new byte[rows][col];

        for(int i = 0; i < tiles[0].length; i++) {
            byte tile = (byte) random.nextInt((int) difficulty + 1);
            tiles[(byte) random.nextInt(rows)][i] = tile;
            i += random.nextInt((int) (7 / difficulty));
        }
        return tiles;
    }
}
