package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.plainsimple.spaceships.util.ImageUtil;

import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Bitmap cache for R.drawables. Retrieved using BitmapID.toString()
 */
public class BitmapCache {

    // stores bitmaps
    private static Hashtable<String, Bitmap> bmpCache = new Hashtable<>();
    // stores bitmap data
    private static Hashtable<String, BitmapData> bmpData = new Hashtable<>();

    private static float scalingFactor = 1.0f;

    public static void setScalingFactor(float scalingFactor) {
        BitmapCache.scalingFactor = scalingFactor;
    }

    // looks up the Bitmap with the given BitmapID. Will attempt to load the corresponding Bitmap
    // from storage if not found (using the BitmapID's rId field)
    public static Bitmap getBitmap(BitmapID key, Context context) throws NoSuchElementException {
        // attempts to get the bitmap from the Hashtable
        Bitmap bmp = bmpCache.get(key.toString());
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
                bmpCache.put(key.toString(), bmp);
//                Log.d("Bitmap Cache", "Width is " + bmp.getWidth() + " and height is " + bmp.getHeight());
                bmpData.put(key.toString(), new BitmapData(key, bmp.getWidth(), bmp.getHeight()));
            }
        }
        return bmp;
    }

    // looks up the BitmapData for the given BitmapID. Will attempt to load the corresponding Bitmap
    // from storage if not found (using the BitmapID's rId field)
    public static BitmapData getData(BitmapID key, Context context) throws NoSuchElementException {
        // attempts to get the data from the Hashtable
        BitmapData data = bmpData.get(key.toString());
        if (data == null) {
            // loads in the bitmap if it hasn't already been loaded
            try {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), key.getrId());
                bmp = Bitmap.createScaledBitmap(bmp,
                        (int) (bmp.getWidth() * scalingFactor),
                        (int) (bmp.getHeight() * scalingFactor), true);
                data = new BitmapData(key, bmp.getWidth(), bmp.getHeight());
                bmpData.put(key.toString(), data);
                bmpCache.put(key.toString(), bmp);
            } catch (Exception e) {
                throw new NoSuchElementException("Error with the given key: " + key);
            }
        }
        return data;
    }

    // looks up and returns the Bitmap stored under the given key. Throws NoSuchElementException
    // if not found. Makes no attempt to load the image
    public static Bitmap getBitmap(String key) throws NoSuchElementException {
        if (!bmpCache.containsKey(key)) {
            throw new NoSuchElementException("Error: Key not found (\"" + key + "\")");
        } else {
            return bmpCache.get(key);
        }
    }

    // looks up and returns the BitmapData stored under the given key. Throws NoSuchElementException
    // if not found. Makes no attempt to load the image
    public static BitmapData getData(String key) throws NoSuchElementException {
        if (!bmpData.containsKey(key)) {
            throw new NoSuchElementException("Error: Key not found (\"" + key + "\")");
        } else {
            return bmpData.get(key);
        }
    }

    // puts the given Bitmap under the given key for future use
    public static void putBitmap(String key, Bitmap bitmap) {
        bmpCache.put(key, bitmap);
        bmpData.put(key, new BitmapData(null, bitmap.getWidth(), bitmap.getHeight()));
    }

    public static void destroyBitmaps() {
        for (String key : bmpCache.keySet()) {
            bmpCache.remove(key);
        }
    }
}