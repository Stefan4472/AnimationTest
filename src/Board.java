import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.Collectors;

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
    // difficulty level, incremented every frame
    private double difficulty;
    // score in current run
    private int score;
    // time, in ms, last frame was completed
    private long lastTime = 0;

    // Number of milliseconds to wait before repainting
    private final int DELAY = 10;

    public Spaceship getSpaceship() { return spaceship; }
    public Map getMap() { return map; }
    public double getDifficulty() { return difficulty; }
    public void incrementScore(int add) {
        score += add;
    }

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
                new Obstacle("obstacle_tile.png"),
                new Obstacle("obstacle_tile.png"),
                new Coin("coin_tile.png"),
                new Alien("alien.png")
        });
        map.setBoard(this);
        difficulty = 0.0f;
        score = 0;
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

    // draws screen
    private void doDrawing(Graphics g) {
        //System.out.println(System.currentTimeMillis() - lastTime);
        //lastTime = System.currentTimeMillis();

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.WHITE);

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
        for(Sprite p : map.getProjectiles()) {
            p.render(g2d, this);
        }
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        if(paused) {
            // todo: pause screen
        } else {
            updateSpaceship();
            map.update();
            scrollCounter -= map.getScrollSpeed();
            score += difficulty / 2;

            updateSprites(map.getTiles());
            /*System.out.println(map.getTiles().stream()
                    .filter(s -> s instanceof Alien)
                    .map(s -> s.getX() + "," + s.getY())
                    .collect(Collectors.joining("\n")));*/
            updateSprites(map.getProjectiles());
            updateSprites(spaceship.getProjectiles());

            checkCollisions(spaceship.getProjectiles(), map.getTiles());
            checkCollisions(map.getProjectiles(), map.getTiles());
            ArrayList<Sprite> sp = new ArrayList<>();
            sp.add(spaceship);
            checkCollisions(sp, map.getTiles());
            checkCollisions(sp, map.getProjectiles());


            spaceship.move();
            moveSprites(map.getTiles());
            moveSprites(map.getProjectiles());
            moveSprites(spaceship.getProjectiles());
        }
        repaint();
        difficulty += 0.01f;
    }

    private void updateSprites(ArrayList<Sprite> sprites) {
        Iterator<Sprite> i = sprites.iterator();
        while(i.hasNext()) {
            Sprite s = i.next();
            if(s.isVisible()) {
                s.updateActions();
                s.updateSpeeds();
            } else {
                i.remove();
            }
        }
    }

    private void checkCollisions(ArrayList<Sprite> sprites, ArrayList<Sprite> tiles) {
        for(Sprite s : sprites) {
            tiles.stream().filter(t -> s.collidesWith(t)).forEach(t -> {
                s.handleCollision(t);
                t.handleCollision(s);
            });
        }
    }

    private void moveSprites(ArrayList<Sprite> sprites) {
        sprites.forEach(Sprite::move);
    }

    private void updateSpaceship() {
        spaceship.updateSpeeds();

        if(spaceship.getX() < 200) {
            spaceship.setControllable(false);
            spaceship.setSpeedX(4.0f);
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
        spaceship.updateActions();
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
                paused = !paused; // toggle pause // todo: worry about pause and mainscreen in fxml
            } else {
                spaceship.keyPressed(e);
            }
        }
    }
}
