package plainsimple.spaceships.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Stefan on 1/23/2016.
 */
public class ScoreDisplay {

    // score to display
    private int score = 0;
    // Paint used for drawText on canvas
    private Paint paint;
    // coordinates on canvas to start drawing score
    private int startX;
    private int startY;

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStartXY(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public ScoreDisplay(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(44);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    public void draw(Canvas canvas) {
        canvas.drawText(formatNumber(score), startX, startY, paint);
    }

    public String formatNumber(int toFormat) {
        return Integer.toString(toFormat);
    }
}
