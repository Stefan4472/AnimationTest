package plainsimple.spaceships;

import android.graphics.Rect;

/**
 * Created by Stefan on 8/24/2015.
 */
public class Hitbox {

    private Rect rect;

    // dimensions of Rect
    private int width;
    private int height;

    // offset of hitbox from sprite's point of drawing (top left)
    private int offsetX;
    private int offsetY;

    public Rect getRect() { return rect; }
    public void setRect(Rect rect) { this.rect = rect; }
    public int getX() { return rect.left; }
    public int getY() { return rect.top; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() {
        return offsetY;
    }
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setOffsets(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void setDimensions(int width, int height) { // todo: test hitboxes
        rect.right = rect.left + width;
        rect.bottom = rect.top - height;
    }

    public Hitbox() {
        rect = new Rect(0, 0, 0, 0);
        width = 0;
        height = 0;
        offsetX = 0;
        offsetY = 0;
    }

    public Hitbox(int left, int top, int width, int height, int offsetX, int offsetY) {
        rect = new Rect(top - height, left, left + width, top);
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    // resets hitbox to new coordinates, keeping width and height
    public void updateCoordinates(int newX, int newY) {
        rect.top = newY;
        rect.left = newX;
        rect.bottom = newY - height;
        rect.right = newX + width;
    }

    // returns whether hitboxes intersect
    public boolean intersects(Hitbox hitbox) {
        return Rect.intersects(rect, hitbox.getRect());
    }
}
