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

    private int boardWidth;
    private int boardHeight;

    // num pixels scrolled
    private int scrollCounter;
    // whether game is paused currently
    private boolean paused;
    // space background (implements parallax scrolling)
    private Background background;
    // generates terrain and sprites on screen
    private Map map;
    // time, in ms, last frame was completed
    private long lastTime = 0;

    // Number of milliseconds to wait before repainting
    private final int DELAY = 10;

    public Board(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        initBoard();
    }

    private void initBoard() { // todo: optimize images? https://community.oracle.com/thread/1263684
        addKeyListener(new TAdapter());
        setFocusable(true);
        setDoubleBuffered(true);

        spaceship = new Spaceship("spaceship.png", 100, 125);
        background = new Background(new String[] {
                "space1.png",
                "space2.png",
                "space3.png",
                "space4.png"
        });
        scrollCounter = 0;
        paused = false;
        map = new Map(new Sprite[] {
                new Obstacle("obstacle_tile.png")
        });

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
        //map.render(g2d, this);

        ArrayList<Sprite> tiles = map.getTiles(); // todo: just use Sprite t : map.getTiles() ?
        for(Sprite t : tiles) {
            t.render(g2d, this);
        }

        spaceship.render(g2d, this);

        ArrayList<Sprite> rockets = spaceship.getRockets();
        ArrayList<Sprite> bullets = spaceship.getBullets();

        for (Sprite r : rockets) {
            r.render(g2d, this);
        }

        for(Sprite b : bullets) {
            b.render(g2d, this);
        }
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        if(paused) {

        } else {
            updateSpaceship();

            updateSprites(map.getTiles());
            updateSprites(spaceship.getBullets());
            updateSprites(spaceship.getRockets());

            checkCollisions(spaceship.getBullets(), map.getTiles());
            checkCollisions(spaceship.getRockets(), map.getTiles());
            ArrayList<Sprite> sp = new ArrayList<>();
            sp.add(spaceship);
            checkCollisions(sp, map.getTiles());

            moveSprites(map.getTiles());
            moveSprites(spaceship.getBullets());
            moveSprites(spaceship.getRockets());
        }
        repaint();
    }

    private void updateSprites(ArrayList<Sprite> sprites) {
        Iterator<Sprite> i = sprites.iterator();
        while(i.hasNext()) {
            Sprite s = i.next();
            if(s.isVisible()) {
                s.updateCurrentImage();
                s.updateActions();
                s.updateSpeedX();
                s.updateSpeedY();
            } else {
                i.remove();
            }
        }
    }

    private void checkCollisions(ArrayList<Sprite> sprites, ArrayList<Sprite> tiles) {
        for(Sprite s : sprites) {
            for(Sprite t : tiles) {
                if(s.collidesWith(t)) {
                    s.handleCollision(t);
                    t.handleCollision(s);
                }
            }
        }
    }

    private void moveSprites(ArrayList<Sprite> sprites) {
        for(Sprite s : sprites)
            s.move();
    }

    private void moveSprites(ArrayList<Sprite> sprites, float speedX, float speedY) {
        for(Sprite s : sprites) {
            s.setSpeedX(speedX);
            s.setSpeedY(speedY);
            s.move();
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
        if(spaceship.getY() < 0) {
            spaceship.setY(0);
        } else if(spaceship.getY() > boardHeight - spaceship.getHeight()) {
            spaceship.setY(boardHeight - spaceship.getHeight());
        }
    }

    // sends keystrokes to spaceship
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            spaceship.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
                paused = !paused; // toggle pause
            } else {
                spaceship.keyPressed(e);
            }
        }
    }
}
