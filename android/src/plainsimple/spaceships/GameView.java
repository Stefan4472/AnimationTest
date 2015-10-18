package plainsimple.spaceships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameView extends View {

    private Context context;
    private int scaledSpaceShipW;
    private int scaledSpaceShipH;

    public GameView(Context context) {
        super(context);
        this.context = context;
    }

    private void initResources() {

    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    public boolean onTouchEvent(MotionEvent event) {
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
