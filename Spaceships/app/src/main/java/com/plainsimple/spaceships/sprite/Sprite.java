package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.view.GameView;

import java.util.*;

/**
 * Sprite parent class.
 */
public abstract class Sprite { // todo: figure out public vs. protected

    // coordinates of sprite
    protected float x;
    protected float y;

    // intended movement in x and y directions each frame
    protected float speedX;
    protected float speedY;

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
    protected Hitbox hitBox;

    // data concerning sprite's default Bitmap
    protected BitmapData bitmapData;

    // random number generator
    protected Random random;

    public Sprite(BitmapData bitmapData, float x, float y) {
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
        hitBox = new Hitbox(0, 0, 0, 0);
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

    // draws sprite onto given canvas
    public abstract List<DrawParams> getDrawParams();

    // moves sprite using speedX and speedY, updates hitbox,
    // and checks if sprite is still visible
    public void move() {
        x += GameView.screenW * speedX; // todo: find a fix
        y += GameView.screenH * speedY;
        hitBox.offset(GameView.screenW * speedX, GameView.screenH * speedY);
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
            return hitBox.intersects(s.hitBox);
        }
    }

    // returns distance between centers of sprite hitboxes, as portion of screen width
    public double distanceTo(Sprite s) {
        return Math.sqrt(Math.pow((s.getHitboxCenter().getX() - getHitboxCenter().getX()), 2)
                + Math.pow((s.getHitboxCenter().getY() - getHitboxCenter().getY()), 2)) / GameView.screenW;
    }

    // returns coordinates of center of sprite's hitbox
    // as a Point2D object
    public Point2D getHitboxCenter() { // todo: remove?
        return hitBox.getCenter();
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
    public void setX(float x) {
        float dx = x - this.x;
        this.x = x;
        hitBox.offset(dx, 0);
    }

    // changes y-coordinate and updates hitbox as well
    public void setY(float y) { // todo: see if hitBox change is necessary - shouldn't be or should be in a separate method
        float dy = y - this.y;
        this.y = y;
        hitBox.offset(0, dy);
    }

    public BitmapData getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(BitmapData bitmapData) {
        this.bitmapData = bitmapData;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public Hitbox getHitBox() {
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
