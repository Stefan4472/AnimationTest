package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import plainsimple.imagetransition.FadeInTransition;

/**
 * Created by Stefan on 6/28/2016.
 */
public class MainMenuBackground extends View {

    private Bitmap backgroundImg;
    private FadeInTransition transition;
    private int width;
    private int height;

    public MainMenuBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) { // todo: may require invalidate() to force animation update
        canvas.drawBitmap(transition.nextFrame(), 0, 0, null);
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        backgroundImg = BitmapFactory.decodeResource(this.getResources(), R.drawable.space_background);
        backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
        transition = new FadeInTransition(null, backgroundImg, 100);
    }
}
