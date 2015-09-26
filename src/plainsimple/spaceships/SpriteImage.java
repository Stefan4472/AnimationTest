package plainsimple.spaceships;

import java.awt.image.BufferedImage;

/**
 * Created by Stefan on 8/24/2015.
 */
public class SpriteImage extends BufferedImage {

    private int offsetX;
    private int offsetY;

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }

    public void setOffsetX(int offsetX) { this.offsetX = offsetX; }
    public void setOffsetY(int offsetY) { this.offsetY = offsetY; }

    public SpriteImage(int width, int height, int imageType) {
        super(width, height, imageType);
        initSpriteImage();
    }

    private void initSpriteImage() {
        offsetX = 0;
        offsetY = 0;
    }
}
