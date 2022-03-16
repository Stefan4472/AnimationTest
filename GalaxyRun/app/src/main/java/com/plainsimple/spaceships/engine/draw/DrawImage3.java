package com.plainsimple.spaceships.engine.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.plainsimple.spaceships.helper.BitmapCache;

public class DrawImage3 implements DrawParams {
    private final Bitmap bitmap;
    private final float x, y;

    public DrawImage3(Bitmap bitmap, float x, float y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Canvas canvas, BitmapCache bitmapCache) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
