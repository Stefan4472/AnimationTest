package plainsimple.spaceships;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Spaceships extends JFrame {

    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Spaceships ex = new Spaceships();
                ex.setVisible(true);
            }
        });
    }

    public Spaceships() {
        initUI();
    }

    private void initUI() {
        add(new Board(SCREEN_WIDTH, SCREEN_HEIGHT));
        setResizable(false);
        setTitle("Spaceship");
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
