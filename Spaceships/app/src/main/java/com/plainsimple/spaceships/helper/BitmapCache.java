package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Used for centralized storage/retrieval of Bitmaps. Bitmaps are stored in a Hashtable
 * as String-Bitmap pairs.
 */
public class BitmapCache {

    // stores bitmaps
    private static Hashtable<String, Bitmap> bmpCache = new Hashtable<>();
    // stores bitmap data
    private static Hashtable<String, BitmapData> bmpData = new Hashtable<>();
    // factor to scale in x- and y- for each bitmap loaded
    // does not apply to bitmaps added later
    private static float scalingFactor = 1.0f;

    // sets scaling factor for use in Bitmaps loaded from memory
    public static void setScalingFactor(float scalingFactor) {
        BitmapCache.scalingFactor = scalingFactor;
    }

    // loads and scales every Bitmap specified in the given array
    public static void loadBitmapsFromMemory(BitmapID[] requiredBitmaps, Context context) {
        Bitmap loaded;
        // load in required bitmaps and save their data in bmpData as well
        for (BitmapID id : requiredBitmaps) {
            loaded = loadFromMemory(id.getrId(), context);
            loaded = scaleBitmap(loaded, scalingFactor);
            bmpCache.put(id.toString(), loaded);
            bmpData.put(id.toString(), new BitmapData(id, loaded.getWidth(), loaded.getHeight()));
        }
    }

    // loads the bitmap with the specified R.drawable and returns it. // todo: make public static?
    // *does not apply scaling factors*
    public static Bitmap loadFromMemory(int rDrawable, Context context) throws NoSuchElementException {
        try {
            return BitmapFactory.decodeResource(context.getResources(), rDrawable);
        } catch (Exception e) {
            throw new NoSuchElementException("Given R.drawable ID \"" + rDrawable + "\" is invalid.");
        }
    }

    // scales given bitmap in x- and y- by scalingFactor
    public static Bitmap scaleBitmap(Bitmap toScale, float scalingFactor) {
        return Bitmap.createScaledBitmap(toScale,
                (int) (toScale.getWidth() * scalingFactor),
                (int) (toScale.getHeight() * scalingFactor), true);
    }

    // puts the specified bitmap into the cache. May overwrite another entry
    public static void putBitmap(String key, Bitmap toPut) {
        bmpCache.put(key, toPut);
        bmpData.put(key, new BitmapData(null, toPut.getWidth(), toPut.getHeight())); // todo: putBitmap in with a null value?
    }

    // puts the specified bitmap into the cache, using the BitmapID's toString() method
    // as the key. May overwrite another entry
    public static void putBitmap(BitmapID key, Bitmap toPut) {
        Log.d("BitmapCache", "Putting under " + key.toString() + ". Size is " + bmpCache.size());
        bmpCache.put(key.toString(), toPut);
        bmpData.put(key.toString(), new BitmapData(key, toPut.getWidth(), toPut.getHeight()));
        Log.d("BitmapCache", "Size is now " + bmpCache.size() + " and " + bmpData.size());
    }

    // gets the Bitmap with the specified key. Uses context to retrieve the Bitmap from memory
    // if it is not already in the cache. Throws NoSuchElementException if the specified
    // Bitmap can not be found
    public static Bitmap getBitmap(BitmapID key, Context context) throws NoSuchElementException {
        Log.d("BitmapCache", "Looking for " + key);
        // return bitmap if it already exists in the cache
        if (bmpCache.containsKey(key.toString())) {
            Log.d("BitmapCache", "Returning bitmap for " + key);
            if (bmpCache.get(key.toString()) == null) {
                Log.d("BitmapCache", "Found null");
            }
            return bmpCache.get(key.toString());
        } else {
            try {
                // load and scale bitmap, add it to both hashtables and return
                Bitmap loaded = loadFromMemory(key.getrId(), context);
                loaded = scaleBitmap(loaded, scalingFactor);
                putBitmap(key.toString(), loaded);
                return loaded;
            } catch (Exception e) {
                throw new NoSuchElementException("Given R.drawable ID \"" + key.getrId() + "\" is invalid.");
            }
        }
    }

    // gets the Bitmap with the specified key. Only works if the Bitmap has previously been
    // loaded. Throws exception if this is not the case
    public static Bitmap getBitmap(String key) throws NoSuchElementException {
        // return bitmap if it already exists in the cache
        if (bmpCache.containsKey(key)) {
            return bmpCache.get(key);
        } else {
            throw new NoSuchElementException("Given key \"" + key + "\" is invalid.");
        }
    }

    public static BitmapData getData(BitmapID key, Context context) throws NoSuchElementException {
        // attempts to get the data from the Hashtable
        BitmapData data = bmpData.get(key);
        Log.d("BitmapCache", "Got data for " + key + " and it " + (bmpData.get(key) == null ? " is " : " isn't ") + "null");
        if (data == null) {
            try { // loads in the bitmap if it hasn't already been loaded
                Bitmap loaded = loadFromMemory(key.getrId(), context);
                loaded = scaleBitmap(loaded, scalingFactor);
                putBitmap(key, loaded);
                return bmpData.get(key);
            } catch (Exception e) {
                throw new NoSuchElementException("Error with the given key: " + key);
            }
        }
        return data;
    }

    public static BitmapData getData(String key) throws NoSuchElementException {
        if (bmpData.containsKey(key)) {
            return bmpData.get(key);
        } else {
            throw new NoSuchElementException("No Bitmap with given key \"" + key + "\"");
        }
    }
}
