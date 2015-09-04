import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil  {

    // draws overlay onto g starting at pixel (w, h)
    // ignores fully transparent pixels
    public static void layer(Graphics2D g, BufferedImage overlay, int w, int h) { // todo: optimizations (WritableRaster?)
        int height = overlay.getHeight();
        int width = overlay.getWidth();
        Color pixel_color;

        int rgb;
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                rgb = overlay.getRGB(i, j);
                if ((rgb >> 24 & 0xff) != 0) {
                     // only overlay non-transparent pixels
                }
            }
        }
    }

    public static void layer(BufferedImage b, BufferedImage overlay) {
        //layer(b.createGraphics(), overlay);
    }
}
