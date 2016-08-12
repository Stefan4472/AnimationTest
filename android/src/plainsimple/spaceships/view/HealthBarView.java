package plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import plainsimple.spaceships.R;

/**
 * Displays Spaceship's health bar along lower screen edge
 */
public class HealthBarView extends View {

    // current health level
    int currentHealth;
    // full health level
    int fullHealth;
    // width of health bar on screen
    int width;
    // height of health bar on screen
    int height;
    // used to draw the health bar
    Paint paint;

    public HealthBarView(Context context, int fullHealth, int currentHealth) {
        super(context);
        this.fullHealth = fullHealth;
        this.currentHealth = currentHealth;
        paint = new Paint();
    }

    public HealthBarView(Context context, AttributeSet attrs, int fullHealth, int currentHealth) { // todo: use xml attributes
        super(context, attrs);
        this.fullHealth = fullHealth;
        this.currentHealth = currentHealth;
        paint = new Paint();
    }

    public void setCurrentHealth(int currentHealth) { // todo: animated change
        this.currentHealth = currentHealth;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferred_width = MeasureSpec.getSize(widthMeasureSpec);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // preferred height is one tenth of screen height
        int preferred_height = (int) (metrics.heightPixels * 0.1);
        setMeasuredDimension(preferred_width, decideMeasurement(heightMeasureSpec, preferred_height));
    }

    // logic for deciding on the measured height of the view
    public int decideMeasurement(int measureSpec, int preferred) {
        int specified_size = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY: // height of the view must be the one given
                return specified_size;
            case MeasureSpec.AT_MOST: // height of the view cannot be larger than the one given
                return Math.min(preferred, specified_size);
            default: // no restrictions
                return preferred;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
    }

    @Override
    public void onDraw(Canvas canvas) {
        // draw bounding rectangle of health bar
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(height * 0.1f);
        canvas.drawRect(0, 0, width, height, paint);
        // draw the filling of the health bar
        paint.setColor(getHealthBarColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(height * 0.1f, height * 0.1f, height * 0.9f, height * 0.9f, paint);
    }

    // calculates what color the health bar should be using the ratio of
    // currentHealth to fullHealth
    private int getHealthBarColor() {
        double ratio = currentHealth / fullHealth;
        if (ratio > 0.7) {
            return Color.GREEN;
        } else if (ratio > 0.3) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }
}
