import java.awt.*;

/**
 * Created by Stefan on 8/24/2015.
 */
public class Hitbox extends Rectangle.Double {

    // offset of hitbox from sprite's point of drawing (top left)
    private int offsetX;
    private int offsetY;

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }

    public void setOffsetX(int offsetX) { this.offsetX = offsetX; }
    public void setOffsetY(int offsetY) { this.offsetY = offsetY; }

    public void setOffsets(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Hitbox() {
        super();
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public Hitbox(int x, int y, int width, int height, int offsetX, int offsetY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    // resets hitbox to new coordinates
    public void updateCoordinates(int newX, int newY) {
        this.x = newX + offsetX;
        this.y = newY + offsetY;
    }
}
