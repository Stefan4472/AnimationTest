package plainsimple.spaceships;

/*import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;*/

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil {

    // draws specified Bitmap onto canvas using given drawing parameters
    public static void drawBitmap(Canvas canvas, Bitmap toDraw, DrawParams params) {
        Rect source = new Rect(params.getX0(), params.getY0(), params.getX1(), params.getY1());
        Rect destination = new Rect(params.getCanvasX0(), params.getCanvasY0(),
                params.getCanvasX0() + source.width(), params.getCanvasY0() + source.height());
        if (params.getBitmapID().equals(BitmapResource.COIN_SPIN)) {
            Log.d("ImageUtil Class", "Source = " + source.flattenToString() + " and destination = " + destination.flattenToString());
        }
        canvas.drawBitmap(toDraw, source, destination, null);
    }

    // takes a spriteSheet and the base image (model)
    // for each frame on spriteSheet, compares pixels with base image
    // returns a BufferedImage of spritesheet containing only different pixels (basically a diff)
    // this allows for layering without lost pixels
    /*public static BufferedImage getSpriteSheetDiff(BufferedImage model, BufferedImage spriteSheet) {
        BufferedImage diff = new BufferedImage(spriteSheet.getWidth(), spriteSheet.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int frames_width = spriteSheet.getWidth() / model.getWidth();
        int frames_height = spriteSheet.getHeight() / model.getHeight();
        int compared_rgb, spriteSheet_x, spriteSheet_y;

        for (int i = 0; i < frames_width; i++) {
            for (int j = 0; j < frames_height; j++) {
                for (int w = 0; w < model.getWidth(); w++) {
                    for (int h = 0; h < model.getHeight(); h++) {
                        spriteSheet_x = i * model.getWidth() + w;
                        spriteSheet_y = j * model.getHeight() + h;
                        compared_rgb = spriteSheet.getRGB(spriteSheet_x, spriteSheet_y);
                        if (compared_rgb != model.getRGB(w % 50, h % 50)) {
                            diff.setRGB(spriteSheet_x, spriteSheet_y, compared_rgb);
                        }
                    }
                }
            }
        }

        return diff;
    }

    public static void getSpriteSheetDiff(String modelPath, String spriteSheetPath, String diffPath) {
        try {
            BufferedImage model = ImageIO.read(new File(modelPath));
            System.out.println((new File(spriteSheetPath).exists()));
            BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));
            BufferedImage diff = getSpriteSheetDiff(model, spriteSheet);
            ImageIO.write(diff, "png", new File(diffPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
