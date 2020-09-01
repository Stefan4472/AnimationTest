package com.plainsimple.spaceships.sprite;

;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.*;

/**
 * Sprite parent class.
 */
public abstract class Sprite {

    protected GameContext gameContext;

    // Enumeration of implemented Sprite types
    public enum SpriteType {
        ALIEN,
        ALIEN_BULLET,
        ASTEROID,
        BULLET,
        COIN,
        OBSTACLE,
        SPACESHIP
    };

    public enum SpriteState {
        // Sprite is "alive" and executing normal logic
        ALIVE,
        // Sprite has "died" (hit 0 health)
        DEAD,
        // Sprite has finished all logic and should be removed
        // from the game
        TERMINATED
    }

    // Unique identifier assigned to this sprite
    private int spriteId;
    private SpriteType spriteType;

    // Coordinates of sprite, top-left
    private double x, y;
    // Dimensions specifying bounds of Sprite's image
    private int width, height;
    // Intended speed-per-second in x- and y- (pixels)
    private double speedX, speedY;
    // Current state that this sprite is in
    private SpriteState currState;
    // The sprite's health. Also the amount of damage the sprite will
    // inflict upon collision.
    private int health;
    // Whether or not this sprite can collide with other sprites
    private boolean canCollide = true;
    // Whether or not this sprite should be shown on screen.
    // NOTE: ACTUALLY, IT'S THE SPRITE'S RESPONSIBILITY TO IMPLEMENT THIS IN GETDRAWPARAMS() -- TODO: WOULD BE NICE TO HAVE A HIGHER-LEVEL MECHANISM TO DO THAT
    private boolean isVisible;
    // Hitbox for collision detection
    private Rectangle hitbox;
    // Offset from Sprite's coordinates to hitbox coordinates
    private double hitboxOffsetX, hitboxOffsetY;

    // Random number generator
    // TODO: PUT IN GAMECONTEXT?
    protected static final Random random = new Random();

    // TODO: ADD OPTIONAL `PARENT` PARAM
    public Sprite(
            int spriteId,
            SpriteType spriteType,
            double x,
            double y,
            int width,
            int height,
            GameContext gameContext
    ) {
        this.spriteId = spriteId;
        this.spriteType = spriteType;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hitbox = new Rectangle(
                this.x + hitboxOffsetX,
                this.y + hitboxOffsetY,
                this.width,
                this.height
        );

        this.gameContext = gameContext;
        setCurrState(SpriteState.ALIVE);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            double x,
            double y,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, 0, 0, gameContext);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            double x,
            double y,
            BitmapData bitmapData,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, bitmapData.getWidth(), bitmapData.getHeight(), gameContext);
    }

    public Sprite(
            int spriteId,
            SpriteType spriteType,
            double x,
            double y,
            BitmapID bitmapID,
            GameContext gameContext
    ) {
        this(spriteId, spriteType, x, y, gameContext.getBitmapCache().getData(bitmapID), gameContext);
    }

    /* Begin core abstract methods */
    // Update/handle any actions sprite takes
    public abstract void updateActions(UpdateContext updateContext);

    // Update speedX and speedY
    public abstract void updateSpeeds(UpdateContext updateContext);

    // Start/stop/update any animations
    public abstract void updateAnimations(UpdateContext updateContext);

    // Handles collision with another sprite. Also passes damage taken
    // (health's are cross-subtracted simultaneously; see GameEngineUtil)
    public abstract void handleCollision(
            Sprite s,
            int damage,
            UpdateContext updateContext
    );

    // Called to tell the sprite to die.
    // TODO: SHOULD THE SPRITE DECIDE WHEN IT TAKES DAMAGE/HOW MUCH DAMAGE IT TAKES?
    // TODO: PROVIDE "KILLER" SPRITE?
    protected abstract void die(UpdateContext updateContext);

    /*
    Sprite should push its DrawParams onto the provided queue.
    Draw calls are executed in the order of addition to the queue (FIFO).
     */
    public abstract void getDrawParams(
            ProtectedQueue<DrawParams> drawQueue
    );

    /* Begin utility methods */
    // Moves the sprite based on current speeds and the number of
    // milliseconds since the previous update.
    public void move(UpdateContext updateContext) {
        x += speedX * updateContext.getGameTime().getMsSincePrevUpdate() / 1000;
        y += speedY * updateContext.getGameTime().getMsSincePrevUpdate() / 1000;
        hitbox.setX(x + hitboxOffsetX);
        hitbox.setY(y + hitboxOffsetY);
    }

    // Returns whether the sprite's hitbox is in game bounds.
    public boolean isHitboxInBounds() {
        return hitbox.intersects(
                0,
                0,
                gameContext.getGameWidthPx(),
                gameContext.getGameHeightPx()
        );
    }

    // Returns whether the sprite's coordinates + width/height
    // are in game bounds.
    public boolean isVisibleInBounds() {
        return x > -width &&
                y > -height &&
                x < gameContext.getGameWidthPx() + width &&
                y < gameContext.getGameHeightPx();
    }

    // Subtracts specified damage from sprite's health and floors
    // its health at 0. Calls `die()` if the sprite's health is now
    // zero or below zero.
    // NOTE: This method will call `die()` if health drops below 1
    // TODO: REMOVE? SHOULD IT CALL `DIE()`?
    public void takeDamage(int damage, UpdateContext updateContext) {
        // Do nothing if sprite is dead
        if (currState == SpriteState.ALIVE) {
            if (damage > health) {
                health = 0;
                die(updateContext);
            } else {
                health -= damage;
            }
        }
    }

    // Returns whether this Sprite's hitbox intersects the hitbox of
    // the specified sprite
    // TODO: some methods could be made static or put in a SpriteUtil or GameEngineUtil class
    public boolean collidesWith(Sprite s) {
        if (canCollide && s.canCollide) {
            return hitbox.intersects(s.hitbox);
        } else {
            return false;
        }
    }

    // Calculates and returns the distance between the center of this
    // Sprite's hitbox to the center of the specified Sprite's hitbox.
    public double distanceTo(Sprite s) {
        return hitbox.getCenter().calcDistance(s.hitbox.getCenter());
    }

    /* Begin getters and setters */
    public int getSpriteId() {
        return spriteId;
    }

    public SpriteType getSpriteType() {
        return spriteType;
    }

    public double getX() {
        return x;
    }

    // Set x and update hitbox
    public void setX(double x) {
        this.x = x;
        this.hitbox.setX(this.x + hitboxOffsetX);
    }

    public double getY() {
        return y;
    }

    // Set y and update hitbox
    public void setY(double y) {
        this.y = y;
        this.hitbox.setY(this.y + hitboxOffsetY);
    }

    public int getWidth() {
        return width;
    }

    // Note: does not update the hitbox
    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    // Note: does not update the hitbox
    protected void setHeight(int height) {
        this.height = height;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public SpriteState getCurrState() {
        return currState;
    }

    protected void setCurrState(SpriteState currState) {
        this.currState = currState;
    }

    public int getHealth() {
        return health;
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    public boolean canCollide() {
        return canCollide;
    }

    protected void setCollidable(boolean canCollide) {
        this.canCollide = canCollide;
    }

    public boolean isVisible() {
        return isVisible;
    }

    protected void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    protected void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public double getHitboxOffsetX() {
        return hitboxOffsetX;
    }

    protected void setHitboxOffsetX(double hitboxOffsetX) {
        this.hitboxOffsetX = hitboxOffsetX;
    }

    public double getHitboxOffsetY() {
        return hitboxOffsetY;
    }

    protected void setHitboxOffsetY(double hitboxOffsetY) {
        this.hitboxOffsetY = hitboxOffsetY;
    }

    protected void setHitboxWidth(double width) {
        hitbox.setWidth(width);
    }

    protected void setHitboxHeight(double height) {
        hitbox.setHeight(height);
    }

    public boolean isAlive() {
        return this.currState == SpriteState.ALIVE;
    }

    public boolean shouldTerminate() {
        return this.currState == SpriteState.TERMINATED;
    }

    // NOTE: TODO: CURRENTLY WE ARE JUST USING THE SPRITETYPE AS COLLISION LAYER
    public int getCollisionLayer() {
        return spriteType.ordinal();
    }
}
