package plainsimple.spaceships;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Stefan on 10/17/2015.
 */
public class TitleView extends View {

    private Bitmap titleGraphic;
    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private boolean playButtonPressed = false;
    private int screenW;
    private int screenH;
    private Context context;

    private ImageButton playButton = (ImageButton) findViewById(R.id.playbutton);
    private Button storeButton = (Button) findViewById(R.id.storebutton);
    private Button statsButton = (Button) findViewById(R.id.statsbutton);


    public TitleView(Context context) {
        super(context);
        this.context = context;
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);
        playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
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
                (screenH - titleGraphic.getHeight()) / 2, null);
        if(playButtonPressed) {
            canvas.drawBitmap(playButtonDown,
                    (screenW - playButtonDown.getWidth()) / 2,
                    (int) (screenH * 0.7), null);
        } else {
            canvas.drawBitmap(playButtonUp,
                    (screenW - playButtonUp.getWidth()) / 2,
                    (int) (screenH * 0.7), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //int event_action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // check play button
                if (x > (screenW - playButtonUp.getWidth()) / 2 &&
                        x < ((screenW - playButtonUp.getWidth()) / 2) + playButtonUp.getWidth() &&
                        y > (int) (screenH * 0.7) &&
                        y < (int) (screenH * 0.7) + playButtonUp.getHeight()) {
                    playButtonPressed = true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                playButtonPressed = false;
                launchGameIntent();
                break;
        }
        invalidate();
        return true;
    }

    // handles play button pressed
    private void onPlayPressed(View view) {
        Log.d("TitleView Class", "Play");
    }

    // handles store button pressed
    private void onStorePressed(View view) {

    }

    // handles stats button pressed
    private void onStatsPressed(View view) {

    }

    // starts the game activity
    private void launchGameIntent() {
        Intent game_intent = new Intent(context, GameActivity.class);
        context.startActivity(game_intent);
    }
}
