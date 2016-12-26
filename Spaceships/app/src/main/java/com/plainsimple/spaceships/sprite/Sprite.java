package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.view.GameView;

import java.util.*;

/**
 * Sprite parent class.
 */
public abstract class Sprite { // todo: figure out public vs. protected

    // coordinates of sprite
    protected int x;
    protected int y;

    // intended movement in x and y directions each frame
    protected double speedX;
    protected double speedY;

    // damage sprite can withstand
    protected int hp;
    // damage sprite inflicts on contact with another sprite
    protected int damage;

    // whether or not sprite can collide with other sprites
    protected boolean collides;
    // whether or not sprite is currently moving
    protected boolean moving; // todo: remove?
    // whether or not sprite should be removed from game
    protected boolean terminate;

    // hitbox for collision detection todo: complex shapes
    protected Rect hitBox;

    // data concerning sprite's default Bitmap
    protected BitmapData bitmapData;

    // random number generator
    protected Random random;

    public Sprite(BitmapData bitmapData, int x, int y) {
        this.bitmapData = bitmapData;
        this.x = x;
        this.y = y;
        initSprite();
    }
    // todo: constructor that also takes speedX and speedY as parameters
    private void initSprite() {
        moving = true;
        collides = true;
        terminate = false;
        speedX = 0.0f;
        speedY = 0.0f;
        hitBox = new Rect();
        random = new Random();
        damage = 0;
        hp = 0;
    }

    // update/handle any actions sprite takes
    public abstract void updateActions();

    // update speedX and speedY
    public abstract void updateSpeeds();

    // start/stop/update any animations the sprite may play
    public abstract void updateAnimations();

    // handles collision with another sprite
    public abstract void handleCollision(Sprite s);

    // returns an ArrayList specifying Bitmaps to be drawn for the sprite
    public abstract ArrayList<DrawParams> getDrawParams();

    // moves sprite using speedX and speedY, updates hitbox,
    // and checks if sprite is still visible
    public void move() {
        x += (int) (GameView.screenW * speedX);
        y += (int) (GameView.screenH * speedY);
        hitBox.offset((int) (GameView.screenW * speedX), (int) (GameView.screenH * speedY));
    }

    // checks whether sprite's image is withing the screen bounds
    // keep in mind sprites are generated past the screen
    public boolean isInBounds() {
        if (x > GameView.screenW + bitmapData.getWidth() || x < -bitmapData.getWidth()) {
            return false;
        } else if (y > GameView.screenH || y < -bitmapData.getHeight()) {
            return false;
        } else {
            return true;
        }
    }

    // returns whether hitbox of this sprite intersects hitbox of specified sprite // todo: some methods could be made static or put in a SpriteUtil or GameEngineUtil class
    public boolean collidesWith(Sprite s) {
        if (!collides || !s.collides) {
            return false;
        } else {
            return Rect.intersects(hitBox, s.hitBox);
        }
    }

    // returns distance between centers of sprite hitboxes, as portion of screen width
    public double distanceTo(Sprite s) {
        return Math.sqrt(Math.pow((s.getHitboxCenter().getX() - getHitboxCenter().getX()), 2)
                + Math.pow((s.getHitboxCenter().getY() - getHitboxCenter().getY()), 2)) / GameView.screenW;
    }

    // returns coordinates of center of sprite's hitbox
    // as a Point2D object
    public Point2D getHitboxCenter() {
        return new Point2D(hitBox.left + hitBox.width() / 2, hitBox.right + hitBox.height() / 2);
    }

    public boolean getP(double probability) {
        return random.nextInt(1_000) + 1 <= probability * 1_000;
    } //todo: make static

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // returns width of bitmap
    public int getWidth() {
        return bitmapData.getWidth();
    }

    // returns height of bitmap
    public int getHeight() {
        return bitmapData.getHeight();
    }

    // changes x-coordinate and updates hitbox as well
    public void setX(int x) {
        int dx = x - this.x;
        this.x = x;
        hitBox.offset(dx, 0);
    }

    // changes y-coordinate and updates hitbox as well
    public void setY(int y) { // todo: see if hitBox change is necessary - shouldn't be or should be in a separate method
        int dy = y - this.y;
        this.y = y;
        hitBox.offset(0, dy);
    }

    public BitmapData getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(BitmapData bitmapData) {
        this.bitmapData = bitmapData;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public boolean collides() {
        return collides;
    }

    public void setCollides(boolean collides) {
        this.collides = collides;
    }

    public boolean terminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getHP() {
        return hp;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }
}
