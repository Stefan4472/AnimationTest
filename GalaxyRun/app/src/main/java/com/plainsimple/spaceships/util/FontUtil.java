package com.plainsimple.spaceships.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.plainsimple.spaceships.helper.FontCache;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 6/22/2016.
 */
public class FontUtil {

    // Sets a font on a textview based on the custom font attribute
    // If the custom font attribute isn't found in the attributes nothing happens
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
//        String font = a.getString(R.styleable.CustomFont_font);
//        setCustomFont(textview, font, context);
//        a.recycle();
    }

    // Sets a font on a TextView
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }
}
