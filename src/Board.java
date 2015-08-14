import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Spaceship spaceship;

    // Number of milliseconds to wait before repainting
    private final int DELAY = 10;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);
        setDoubleBuffered(true);

        spaceship = new Spaceship(40, 60);

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

    // draws spaceship and rockets
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(spaceship.getCurrentImage(), spaceship.getX(), spaceship.getY(), this);

        ArrayList<Rocket> rockets = spaceship.getRockets();

        for (Rocket r : rockets) {
            g2d.drawImage(r.getCurrentImage(), r.getX(),
                    r.getY(), this);
        }
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        updateRockets();
        updateSpaceship();

        repaint();
    }

    private void updateRockets() {
        ArrayList<Rocket> rockets = spaceship.getRockets();

        for (int i = 0; i < rockets.size(); i++) {
            Rocket r = rockets.get(i);

            if (r.isVisible()) {
                r.move();
            } else {
                rockets.remove(i);
            }
        }
    }

    private void updateSpaceship() {
        spaceship.move();
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
