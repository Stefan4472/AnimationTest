package plainsimple.spaceships.util;

/**
 * Stores ID and dimensions of a bitmap
 */
public class BitmapData {

    private BitmapResource id;
    private int width;
    private int height;

    public BitmapData(BitmapResource imageID, int imageWidth, int imageHeight) {
        this.id = imageID;
        this.width = imageWidth;
        this.height = imageHeight;
    }

    public BitmapResource getId() {
        return id;
    }

    public void setId(BitmapResource id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
