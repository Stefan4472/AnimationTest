package plainsimple.spaceships.util;

/**
 * Created by Stefan on 10/18/2015.
 */
public class Point2D {

    private int x;
    private int y;

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
