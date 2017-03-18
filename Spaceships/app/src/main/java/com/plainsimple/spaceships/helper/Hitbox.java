package com.plainsimple.spaceships.helper;

import android.graphics.Rect;

/**
 * Wrapper for a Rect that stores coordinates as floats
 */

public class Hitbox {

    // coordinates
    float x;
    float y;
    float width;
    float height;

    public Hitbox(float x, float y, float x1, float y1) {
        this.x = x;
        this.y = y;
        this.width = x1 - x;
        this.height = y1 - y;
    }

    // shifts coordinates by dx and dy
    public void offset(float dx, float dy) {
        x += dx;
        y += dy;
    }

    // reset fields
    public void reset(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // returns point containing coordinates of hitbox center
    public Point2D getCenter() {
        return new Point2D((int) (x + width / 2.0f), (int) (y + height / 2.0f));
    }

    public boolean intersects(Hitbox h) {
        return x + width >= h.x && h.x + h.width >= x && y + height >= h.y && h.y + h.height >= y;
    }

    // returns a Rect with hitbox coordinates (basically a conversion)
    public Rect toRect() {
        return new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
