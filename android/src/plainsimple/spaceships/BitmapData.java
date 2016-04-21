package plainsimple.spaceships;

/**
 * Stores ID and dimensions of a bitmap
 */
public class BitmapData {

    private int id;
    private int width;
    private int height;

    public BitmapData(int imageID, int imageWidth, int imageHeight) {
        this.id = imageID;
        this.width = imageWidth;
        this.height = imageHeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
