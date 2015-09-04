import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil  {

    // compares two BufferedImages and returns a BufferedImage where
    // only different pixels remain
    // aligns images at top left and returns a BufferedImage with the size
    // of whichever image was smaller
    public static BufferedImage getDiff(BufferedImage model, BufferedImage compared) { // todo: has not been tested yet
        int width, height;
        if(model.getWidth() > compared.getWidth()) {
            width = compared.getWidth();
        } else {
            width = model.getWidth();
        }
        if(model.getHeight() > compared.getHeight()) {
            height = compared.getHeight();
        } else {
            height = model.getHeight();
        }
        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int compared_rgb;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                compared_rgb = compared.getRGB(i, j);
                if(compared_rgb != model.getRGB(i, j)) {
                    diff.setRGB(i, j, compared_rgb);
                }
            }
        }
        return diff;
    }
}
