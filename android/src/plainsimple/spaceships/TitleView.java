package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Stefan on 10/17/2015.
 */
public class TitleView extends View {

    private Bitmap titleGraphic;
    private int screenW;
    private int screenH;

    public TitleView(Context context) {
        super(context);
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenW = w;
        screenH = h;
    }

    // draws titleGraphic centered on screen
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(titleGraphic,
                (screenW - titleGraphic.getWidth()) / 2,
                (screenH - titleGraphic.getHeight()) / 2,
                null);

    }

    public boolean onTouchEvent(MotionEvent event) {
        //int event_action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }
}
