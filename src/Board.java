import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Spaceship spaceship;

    // num pixels scrolled
    private int scrollCounter;
    // whether background should be re-rendered in this frame
    private boolean renderBackground;
    // space background (implements parallax scrolling)
    private Background background;
    // generates terrain and sprites on screen
    private Map map;
    // time, in ms, last frame was completed
    private long lastTime = 0;

    // Number of milliseconds to wait before repainting
    private final int DELAY = 10;

    public Board() {
        initBoard();
    }

    private void initBoard() { // todo: optimize images? https://community.oracle.com/thread/1263684
        addKeyListener(new TAdapter());
        setFocusable(true);
        setDoubleBuffered(true);

        spaceship = new Spaceship("spaceship.png", 100, 100);
        spaceship.setBoard(this);
        background = new Background(new String[] {
                "space1.png",
                "space2.png",
                "space3.png",
                "space4.png"
        });
        scrollCounter = 0;
        renderBackground = true;
        map = new Map(new Sprite[] {
                new Obstacle("obstacle_tile.png")
        });
        map.setBoard(this);

        /* This will call the actionPerformed method of this class
        every DELAY milliseconds */
        timer = new Timer(DELAY, this);
        timer.start();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);

        // used for compatibility with Linux
        Toolkit.getDefaultToolkit().sync();
    }

    // draws background, spaceship, and rockets
    private void doDrawing(Graphics g) {
        //System.out.println(System.currentTimeMillis() - lastTime);
        //lastTime = System.currentTimeMillis();

        Graphics2D g2d = (Graphics2D) g;

        if(scrollCounter > 30) { // scroll background slowly
            background.scroll(1);
            scrollCounter = 0;
        }

        g2d.drawImage(background.render(), 0, 0, this);
        map.render(g2d, this);
        spaceship.render(g2d, this);

        ArrayList<Sprite> rockets = spaceship.getRockets();
        ArrayList<Sprite> bullets = spaceship.getBullets();
        ArrayList<Sprite> tiles = map.getTiles();

        for (Rocket r : rockets) {
            g2d.drawImage(r.getCurrentImage(), r.getX(),
                    r.getY(), this);
        }

        for(Bullet b : bullets) {
            g2d.drawImage(b.getCurrentImage(), b.getX(), b.getY(), this);
        }
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        //updateRockets(); // todo: one arraylist with all sprites and one method call to updateSprites()
        //updateBullets();
        updateSpaceship();
        updateSprites();

        repaint();
    }

    private void updateSprites() {
        System.out.println(sprites.size() + " sprites detected");
        // sprites that need to be checked for collision detection
        ArrayList<Sprite> hit_detection = new ArrayList<>();
        for(Sprite s : sprites) {
            if(s.isVisible() && s.collides())
                hit_detection.add(s);
        }

         // todo: speedup: check spaceship first?
        Sprite current;
        Sprite check;
        for(int i = hit_detection.size() - 1; i > 0; i--) {
            current = hit_detection.get(i);
            current.updateHitbox();
            for(int j = i - 1; j >= 0; j--) {
                check = hit_detection.get(j);
                if(current.collidesWith(check)) {
                    System.out.println("Collision Detected");
                    current.setCollision(true);
                    check.setCollision(true);
                }
            }
        }
        Iterator<Sprite> i = sprites.iterator();
        while (i.hasNext()) {
            Sprite s = i.next();
            s.update();
            s.move();
            if(!s.isVisible())
                i.remove();
        }
         // todo: check if visible. Also, s.move() will undo collision detection unless speedX and speedY are changed
    }

    private void updateRockets() {
        ArrayList<Rocket> rockets = spaceship.getRockets();

        for(int i = 0; i < rockets.size(); i++) {
            Rocket r = rockets.get(i);
            if (r.isVisible()) {
                r.move();
            } else {
                rockets.remove(r);
            }
        }
    }

    private void updateBullets() {
        ArrayList<Bullet> bullets = spaceship.getBullets();

        for(int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if(b.isVisible()) {
                b.move();
            } else {
                bullets.remove(i);
            }
        }
    }

    private void updateSpaceship() {
        spaceship.update();
        spaceship.move();

        // once spaceship gets past x = 200, start scrolling background
        if(spaceship.getX() > 200) { // todo: check if spaceship has collision = true
            map.scroll(spaceship.getX() - 200);
            scrollCounter += spaceship.getX() - 200;
            spaceship.setX(200);
        }
    }

    // sends keystrokes to spaceship class
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            spaceship.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            spaceship.keyPressed(e);
        }
    }
}
