package plainsimple.spaceships;

/**
 * Stores parameters for drawing portions of bitmaps.
 */
public class DrawParams {

    // ID of bitmap to be drawn
    private BitmapResource bitmapID;
    // x-coordinate where drawing begins on canvas
    private int canvasX0;
    // y-coordinate where drawing begins on canvas
    private int canvasY0;
    // starting x-coordinate // todo: x- and y- coordinates to begin drawing on canvas
    private int x0;
    // starting y-coordinate
    private int y0;
    // ending x-coordinate
    private int x1;
    // ending y-coordinate
    private int y1;

    public DrawParams(BitmapResource bitmapID, int canvasX0, int canvasY0, int x0, int y0, int x1, int y1) { // todo: setParams method to set all params at once
        this.bitmapID = bitmapID;
        this.canvasX0 = canvasX0;
        this.canvasY0 = canvasY0;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public BitmapResource getBitmapID() {
        return bitmapID;
    }

    public void setBitmapID(BitmapResource bitmapID) {
        this.bitmapID = bitmapID;
    }

    public int getCanvasX0() {
        return canvasX0;
    }

    public void setCanvasX0(int canvasX0) {
        this.canvasX0 = canvasX0;
    }

    public int getCanvasY0() {
        return canvasY0;
    }

    public void setCanvasY0(int canvasY0) {
        this.canvasY0 = canvasY0;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }
}
