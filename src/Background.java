import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Stefan on 8/14/2015.
 */
public class Background {

    // full background, which can be larger than screen
    private BufferedImage defaultImage;

    // dimensions of defaultImage
    private int width;
    private int height;

    // current portion of background being displayed
    private Image currentImage;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 400;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private int x;
    private int y;


    public Background(BufferedImage defaultImage, int x, int y) throws IndexOutOfBoundsException {
        this.defaultImage = defaultImage;
        width = defaultImage.getWidth();
        height = defaultImage.getHeight();
        currentImage = defaultImage.getSubimage(x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    // returns current part of background being displayed
    public Image getCurrentImage() {
        return currentImage;
    }

    // shifts window and updates currentImage
    // will shift as far as possible without going past edge of defaultImage
    // if window is at the edge of defaultImage, does nothing
    public Image scroll(int x, int y) {
        this.x += x;
        this.y += y;

        // keep scrolling in bounds
        if(x + SCREEN_WIDTH > width)
            x = width - SCREEN_WIDTH;
        else if(x - SCREEN_WIDTH < 0)
            x = 0;

        if(y + SCREEN_HEIGHT > height)
            y = height - SCREEN_HEIGHT;
        else if(y - SCREEN_HEIGHT < 0)
            y = 0;

        currentImage = defaultImage.getSubimage(x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
        return currentImage;
    }
}
