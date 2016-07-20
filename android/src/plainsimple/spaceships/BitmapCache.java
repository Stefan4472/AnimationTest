package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Hashtable;

/**
 * Bitmap cache for R.drawables. Retrieved using BitmapResource
 */
public class BitmapCache {

    // stores bitmaps
    private static Hashtable<BitmapResource, Bitmap> bmpCache = new Hashtable<>();
    // stores bitmap data
    private static Hashtable<BitmapResource, BitmapData> bmpData = new Hashtable<>();

    private static float scalingFactor = 1.0f;

    public static void setScalingFactor(float scalingFactor) {
        BitmapCache.scalingFactor = scalingFactor;
    }

    public static Bitmap getImage(BitmapResource key, Context context) {
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
            bmpData.put(key, new BitmapData(key, bmp.getWidth(), bmp.getHeight()));
        }
        return bmp;
    }

    public static BitmapData getData(BitmapResource key, Context context) {
        // attempts to get the data from the Hashtable
        BitmapData data = bmpData.get(key);
        if (data == null) {
            Log.d("BitmapCache", "Loading in bitmap");
            // loads in the bitmap if it hasn't already been loaded
            try {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), EnumUtil.getID(key));
                bmp = Bitmap.createScaledBitmap(bmp,
                        (int) (bmp.getWidth() * scalingFactor),
                        (int) (bmp.getHeight() * scalingFactor), true);
                data = new BitmapData(key, bmp.getWidth(), bmp.getHeight());
                bmpData.put(key, data);
                bmpCache.put(key, bmp);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }
}
