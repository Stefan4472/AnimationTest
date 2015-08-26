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

        spaceship = new Spaceship("spaceship.png", -50, 125);
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

        for(Sprite t : map.getTiles()) {
            t.render(g2d, this);
        }
        spaceship.render(g2d, this);

        for (Sprite p : spaceship.getProjectiles()) {
            p.render(g2d, this);
        }
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        if(paused) {

        } else {
            updateSpaceship();

            map.update();
            scrollCounter -= map.getScrollSpeed();

            updateSprites(map.getTiles());
            updateSprites(spaceship.getProjectiles());

            checkCollisions(spaceship.getProjectiles(), map.getTiles());
            ArrayList<Sprite> sp = new ArrayList<>();
            sp.add(spaceship);
            checkCollisions(sp, map.getTiles());

            moveSprites(map.getTiles());
            moveSprites(spaceship.getProjectiles());
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
        sprites.forEach(Sprite::move);
    }

    private void updateSpaceship() {
        spaceship.update();

        if(spaceship.getX() < 200) {
            spaceship.setControllable(false);
            spaceship.setSpeedX(4.0f);
            spaceship.move();
        } else if(spaceship.getX() > 200) {
            spaceship.setX(200);
            spaceship.setSpeedX(0.0f);
            spaceship.setControllable(true);
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
