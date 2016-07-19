package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Hashtable;

/**
 * Bitmap cache for R.drawables. Retrieved using BitmapResource
 */
public class BitmapCache {

    private static Hashtable<BitmapResource, Bitmap> bmpCache = new Hashtable<BitmapResource, Bitmap>();

    private static float scalingFactor = 1.0f;

    public static void setScalingFactor(float scalingFactor) {
        BitmapCache.scalingFactor = scalingFactor;
    }

    public static Bitmap get(BitmapResource key, Context context) {
        // attempts to get the bitmap from the Hashtable
        Bitmap bmp = bmpCache.get(key);
        if (bmp == null) {
            // loads in the bitmap if it hasn't already been loaded
            try {
                bmp = BitmapFactory.decodeResource(context.getResources(), EnumUtil.getID(key));
                bmp = Bitmap.createScaledBitmap(bmp,
                        (int) (bmp.getWidth() * scalingFactor),
                        (int) (bmp.getHeight() * scalingFactor), true);
            } catch (Exception e) {
                return null;
            }
            bmpCache.put(key, bmp);
        }
        return bmp;
    }
}
