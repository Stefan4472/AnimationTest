package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.plainsimple.spaceships.util.ImageUtil;

import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Bitmap cache for R.drawables. Retrieved using BitmapID
 */
public class BitmapCache {

    // stores bitmaps
    private static Hashtable<BitmapID, Bitmap> bmpCache = new Hashtable<>();
    // stores bitmap data
    private static Hashtable<BitmapID, BitmapData> bmpData = new Hashtable<>();

    private static float scalingFactor = 1.0f;

    public static void setScalingFactor(float scalingFactor) {
        BitmapCache.scalingFactor = scalingFactor;
    }

    public static Bitmap getBitmap(BitmapID key, Context context) throws NoSuchElementException {
        // attempts to get the bitmap from the Hashtable
        Bitmap bmp = bmpCache.get(key);
        if (bmp == null) {
            // loads in the bitmap if it hasn't already been loaded
            try {
                bmp = BitmapFactory.decodeResource(context.getResources(), key.getrId());
                bmp = Bitmap.createScaledBitmap(bmp,
                        (int) (bmp.getWidth() * scalingFactor),
                        (int) (bmp.getHeight() * scalingFactor), true);
            } catch (Exception e) {
                throw e;
            }
            if (bmp == null) {
                throw new NoSuchElementException("No bitmap found with given key " + key);
            } else {
                bmpCache.put(key, bmp);
//                Log.d("Bitmap Cache", "Width is " + bmp.getWidth() + " and height is " + bmp.getHeight());
                bmpData.put(key, new BitmapData(key, bmp.getWidth(), bmp.getHeight()));
            }
        }
        return bmp;
    }

    public static BitmapData getData(BitmapID key, Context context) throws NoSuchElementException {
        // attempts to get the data from the Hashtable
        BitmapData data = bmpData.get(key);
        if (data == null) {
            // loads in the bitmap if it hasn't already been loaded
            try {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), key.getrId());
                bmp = Bitmap.createScaledBitmap(bmp,
                        (int) (bmp.getWidth() * scalingFactor),
                        (int) (bmp.getHeight() * scalingFactor), true);
                data = new BitmapData(key, bmp.getWidth(), bmp.getHeight());
                bmpData.put(key, data);
                bmpCache.put(key, bmp);
            } catch (Exception e) {
                throw new NoSuchElementException("Error with the given key: " + key);
            }
        }
        return data;
    }

    public static void destroyBitmaps() {
        for (BitmapID key : bmpCache.keySet()) {
            bmpCache.remove(key);
        }
    }
}