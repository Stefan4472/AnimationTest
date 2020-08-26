package com.plainsimple.spaceships.sprite;

;

import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.util.ProtectedQueue;
import com.plainsimple.spaceships.view.GameView;

import java.util.*;

/**
 * Sprite parent class.
 */
public abstract class Sprite { // todo: figure out public vs. protected

    protected GameContext gameContext;

    // Enumeration of implemented Sprite types
    enum SpriteType {
        ALIEN,
        ALIEN_BULLET,
        ASTEROID,
        BULLET,
        COIN,
        OBSTACLE,
        SPACESHIP
    };

    // Unique identifier assigned to this sprite
    protected int spriteId;
    protected SpriteType spriteType;

    // coordinates of sprite
    protected float x;
    protected float y;

    // "real" dimensions used to calculate whether sprite is out of bounds
    protected int width;
    protected int height;

    // intended movement in x and y directions each frame
    protected float speedX;
    protected float speedY;

    // hitpoints: essentially, measure of damage sprite can inflict or withstand
    protected int hp;

    // whether or not sprite can collide with other sprites
    protected boolean collides = true;
    // whether or not sprite should be removed from game
    protected boolean terminate;
    // whether sprite is "dead," i.e. hp has hit zero
    // this can be useful in cases where the sprite has zero health, but
    // can still be hit with projectiles. In these instances we'd want
    // to prevent certain animations or actions from happening that would
    // normally happen on collision
    protected boolean dead;

    // hitbox for collision detection todo: complex shapes?
    protected FloatRect hitBox;

    // random number generator
    protected static final Random random = new Random();

    // TODO: ADD OPTIONAL `PARENT` PARAM
    public Sprite(
            int spriteId,
            SpriteType spriteType,
            float x,
            float y,
            int width,
            int height,
            GameContext gameContext
    ) {
        this.spriteId = spriteId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gameContext = gameContext;
        hitBox = new FloatRect(0, 0, 0, 0);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            float x,
            float y,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, 0, 0, gameContext);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            float x,
            float y,
            BitmapData bitmapData,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, bitmapData.getWidth(), bitmapData.getHeight(), gameContext);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            float x,
            float y,
            BitmapID bitmapID,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, gameContext.getBitmapCache().getData(bitmapID), gameContext);
    }

    // update/handle any actions sprite takes
    public abstract void updateActions(UpdateContext updateContext);

    // update speedX and speedY
    public abstract void updateSpeeds();

    // start/stop/update any animations the sprite may play
    public abstract void updateAnimations();

    // handles collision with another sprite. Also passes damage taken
    // (hp's are cross-subtracted simultaneously; see GameEngineUtil)
    public abstract void handleCollision(Sprite s, int damage);

    /*
    Sprite should push its DrawParams onto the provided queue.
    Draw calls are executed in the order of addition to the queue (FIFO).
     */
    public abstract void getDrawParams(ProtectedQueue<DrawParams> drawQueue);

    // moves sprite using speedX and speedY, updates hitbox,
    // and checks if sprite is still visible
    public void move() {
//        x += GameView.screenW * speedX;
//        y += GameView.playScreenH * speedY;
//        hitBox.offset(GameView.screenW * speedX, GameView.playScreenH * speedY);
    }

    // checks whether sprite's image is partially within the screen bounds
    // returns false when sprite's width and height are fully off-screen
    // keep in mind sprites are generated past the screen
    public boolean isInBounds() {
//        if (x > GameView.screenW + width || x < -width) {
//            return false;
//        } else if (y > GameView.playScreenH || y < -height) {
//            return false;
//        } else {
//            return true;
//        }
        return true;
    }

    // subtracts specified damage from sprite's hp and
    // floors hp at 0 (i.e. if damage > hp)
    public void takeDamage(int damage) {
        hp -= damage;
        hp = (hp < 0) ? 0 : hp;
    }

    // returns whether hitbox of this sprite intersects hitbox of specified sprite // todo: some methods could be made static or putBitmap in a SpriteUtil or GameEngineUtil class
    public boolean collidesWith(Sprite s) {
        if (!collides || !s.collides) {
            return false;
        } else {
            return hitBox.intersects(s.hitBox);
        }
    }

    // returns distance between centers of sprite hitboxes, as portion of screen width
    public double distanceTo(Sprite s) {
//        return Math.sqrt(Math.pow((s.getHitboxCenter().getX() - getHitboxCenter().getX()), 2)
//                + Math.pow((s.getHitboxCenter().getY() - getHitboxCenter().getY()), 2)) / GameView.screenW;
        return 1.0;
    }

    // returns coordinates of center of sprite's hitbox
    // as a Point2D object
    public Point2D getHitboxCenter() { // todo: remove?
        return hitBox.getCenter();
    }

    public boolean getP(float probability) {
        return random.nextFloat() <= probability;
    }

    public int getSpriteId() {
        return spriteId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // returns sprite width
    public int getWidth() {
        return width;
    }

    // returns sprite height
    public int getHeight() {
        return height;
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

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public FloatRect getHitBox() {
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

    public int getHP() {
        return hp;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }
}
