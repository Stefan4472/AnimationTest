package com.plainsimple.spaceships.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Button that can support a custom font
 */
public class FontButton extends Button {

    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
//        FontUtil.setCustomFont(this, context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        FontUtil.setCustomFont(this, context, attrs);
    }
}
