import javax.swing.*;
import java.awt.*;

/**
 * Created by Stefan on 8/12/2015.
 */
public class SpaceShips extends JFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                SpaceShips ex = new SpaceShips();
                ex.setVisible(true);
            }
        });
    }

    public SpaceShips() {
        initUI();
    }

    private void initUI() {
        add(new Board());

        setSize(800, 300);
        setResizable(false);

        setTitle("RocketShip");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
