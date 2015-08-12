import javax.swing.*;
import java.awt.*;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Main {

    private JFrame frame;

    private Canvas canvas;

    public static void main(String[] args) {
        Main main = new Main();
        main.setUpGUI();


    }

    private void setUpGUI() {
        frame = new JFrame("AnimationTest");
        canvas = new Canvas(400, 400);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
