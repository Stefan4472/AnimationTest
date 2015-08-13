import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        spaceship = new Spaceship();

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

    // draws spaceship at coordinates
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(spaceship.getCurrentImage(), spaceship.getX(), spaceship.getY(), this);
    }

    // moves spaceship and repaints JPanel every 10 ms
    @Override
    public void actionPerformed(ActionEvent e) {
        spaceship.move();
        repaint();
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
