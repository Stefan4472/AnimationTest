import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Craft craft;

    // Number of milliseconds to wait before repainting
    private final int DELAY = 10;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);

        craft = new Craft();

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
        g2d.drawImage(craft.getImage(), craft.getX(), craft.getY(), this);
    }

    // moves craft and repaints JPanel
    @Override
    public void actionPerformed(ActionEvent e) {
        craft.move();
        repaint();
    }

    // sends keystrokes to craft class
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            craft.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            craft.keyPressed(e);
        }
    }
}
