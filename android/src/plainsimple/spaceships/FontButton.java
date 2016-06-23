package plainsimple.spaceships;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Button that can be configured to use a custom font
 */
public class FontButton extends Button { // todo: will need a FontTextView also
    // todo: constructor should take font as a parameter
    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontUtil.setCustomFont(this, context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontUtil.setCustomFont(this, context, attrs);
    }
}
