import javax.swing.*;
import java.awt.*;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Spaceships extends JFrame {

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
        add(new Board());

        setSize(600, 300);
        setResizable(false);

        setTitle("RocketShip");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
