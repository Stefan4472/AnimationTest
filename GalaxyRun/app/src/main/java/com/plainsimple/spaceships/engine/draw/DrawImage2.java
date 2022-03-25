package com.plainsimple.spaceships.engine.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;

public class DrawImage2 implements DrawParams {
    private final Bitmap bitmap;
    private final Rect src, dst;

    public DrawImage2(Bitmap bitmap, Rect src, Rect dst) {
        this.bitmap = bitmap;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public void draw(Canvas canvas, BitmapCache bitmapCache) {
        canvas.drawBitmap(bitmap, src, dst, null);
    }
}
