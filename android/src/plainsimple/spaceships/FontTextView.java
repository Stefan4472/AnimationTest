package plainsimple.spaceships;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView that can support a custom font.
 */
public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontUtil.setCustomFont(this, context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontUtil.setCustomFont(this, context, attrs);
    }
}
