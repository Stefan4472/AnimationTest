package plainsimple.spaceships;

import android.graphics.Rect;

/**
 * Created by Stefan on 8/24/2015.
 */
public class Hitbox {

    private Rect rect;

    // sprite's point of drawing
    private int x;
    private int y;

    // dimensions of hitbox
    private int width;
    private int height;

    // offset of hitbox from sprite's point of drawing (top left)
    private int offsetX;
    private int offsetY;

    public Rect getRect() { return rect; }
    public void setRect(Rect rect) { this.rect = rect; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsets(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Hitbox() {
        rect = new Rect(0, 0, 0, 0);
        width = 0;
        height = 0;
        offsetX = 0;
        offsetY = 0;
    }

    // resets hitbox to new coordinates, keeping width and height
    public void updateCoordinates(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        rect.top = y + offsetY;
        rect.left = x + offsetX;
        rect.bottom = y + offsetY + height;
        rect.right = x + offsetX + width;
    }

    // returns whether hitboxes intersect
    public boolean intersects(Hitbox hitbox) {
        return Rect.intersects(rect, hitbox.getRect());
    }
}
