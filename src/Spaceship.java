import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    // direction in x and y
    private int dx;
    private int dy;

    // speed in x and y
    private float speedX;
    private float speedY;

    private final float MAX_SPEED_X = 9.0f;
    private final float MAX_SPEED_Y = 2.0f;

    // Sprite's image when moving
    private SpriteAnimation movingAnimation;

    private SpriteAnimation startMovingAnimation;

    // keeps track of fired rockets
    private ArrayList<Rocket> rockets;

    /**
     * Default constructor, initializes sprite.
     */
    public Spaceship(int x, int y) {
        super(x, y);
        initCraft();
    }

    private void initCraft() {
        defaultImage = new ImageIcon("spaceship.png").getImage();

        try {
            startMovingAnimation =
                    new SpriteAnimation("spaceship_starting_spritesheet.png", 50, 50, 1, false);
        } catch(IOException e) {}

        try {
            movingAnimation = new SpriteAnimation("spaceship_moving_spritesheet.png", 50, 50, 5, true);
        } catch(IOException e) {}

        currentImage = defaultImage;

        moving = false;
        speedX = 0.0f;
        speedY = 0.0f;

        getImageDimensions();

        rockets = new ArrayList<>();
    }

    public ArrayList<Rocket> getRockets() { return rockets; }

    //
    public void move() {
        System.out.println("dx = " + dx);
        // sprite direction not equal to zero
        if(dx != 0 || dy != 0) {
            // sprite was previously not moving. Play startmoving animation and reset acceleration
            if(moving == false && dx == 1) {
                currentImage = startMovingAnimation.start();
            } else { // sprite was previously moving. Figure out speed
                if(startMovingAnimation.isPlaying()) {
                    currentImage = startMovingAnimation.nextFrame();
                    if(dx == 1 && speedX < MAX_SPEED_X)
                        speedX += 0.03;
                    if(dy == 1 && speedY < MAX_SPEED_Y)
                        speedY += 0.075;
                    else if(dy == -1 && speedY > -MAX_SPEED_Y)
                        speedY -= 0.075;
                } else { // Play moving animation as soon as startmoving animation is over
                    currentImage = movingAnimation.nextFrame(); // todo: engine animation is off

                    speedX = calcSpeedX(dx, speedX); // todo: accelerate, brake, drift functions and much simpler if/else branch
                    //speedY = calcSpeedY();
                }
            }
            moving = true;
        } else {
            currentImage = defaultImage;
            moving = false;
        }

        /*if(moving == true && acceleration < 0.1)
            acceleration += 0.007;
        else if(moving == true && acceleration < 2.0)
            acceleration += 0.05;
        else if(moving == true && acceleration < 3.0)
            acceleration += 0.08;
        else if(moving == false && acceleration >= 0.0)
            acceleration -= 0.05; */
        System.out.println("Speed is " + speedX + " px per frame!!!");
        x += speedX;
        y += speedY;
    }

    // fires rocket
    public void fire() {
        Rocket r1 = new Rocket(x + 43, y + 15);
        Rocket r2 = new Rocket(x + 43, y + 33);
        rockets.add(r1);
        rockets.add(r2);
    }

    private float calcSpeedX(int dx, float speedX) {
        if(dx == 1 && speedX <= MAX_SPEED_X) {
            if(speedX <= 1.0) {
                speedX += 0.04;
            } else if(speedX <= 3.0) {
              speedX += 0.07;
            } else if(speedX <= 4.0) {
                speedX += 0.025;
            } else if(speedX <= 7.5){
                speedX += 0.0125;
            } else {
                speedX += .001;
            }
        } else if(dx == -1 && speedX >= 0) {
            if(speedX >= 3.5) {
                speedX -= 0.05;
            } else if(speedX >= 2.0) {
                speedX -= 0.065;
            } else if(speedX >= 1.0) {
                speedX -= .025;
            } else {
                speedX = 0;
            }
        } else if(dx == 0 && speedX >= 0) {
            System.out.println("Slowing down");
            if(speedX > 8.9) {
                speedX -= 0.0002;
            } else if(speedX > 8.5) {
                speedX -= 0.0005;
            } else if(speedX > 7.5) {
                speedX -= 0.001;
            } else if(speedX > 5.0) {
                speedX -= 0.005;
            } else if(speedX > 2.0) {
                speedX -= 0.01;
            } else if(speedX > 1.0){
                speedX -= 0.03;
            }

        }

        if(speedX > MAX_SPEED_X)
            speedX = MAX_SPEED_X;
        return speedX;
    }

    //private float calcSpeedY(int dy, float speedY) {

    //}

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    // x-coordinate of center of sprite
    public int getCenterX() { return x + 31; }

    public int getY() { return y; }

    public void setY(int y) { this.y = y;}

    // y-coordinate of center of sprite
    public int getCenterY() { return y + 25; }

    /**
     * Returns this sprite's currentImage.
     * @return
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Sets direction of sprite based on key pressed.
     * @param e
     */
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -1;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 1;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -1;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 1;
        }

        if(key == KeyEvent.VK_SPACE)
            fire();
    }

    /**
     * Sets movement direction to zero once key is released
     * @param e
     */
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
