package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Stefan on 6/22/2016.
 */
public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        // attempts to get the font from the Hashtable
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            // loads in the font if it hasn't already been loaded
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}
