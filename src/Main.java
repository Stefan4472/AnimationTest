import javax.swing.*;
import java.awt.*;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Main {

    private JFrame frame;

    private Canvas canvas;

    private Sprite sprite;

    public static void main(String[] args) {
        Main main = new Main();
        main.setUpGUI();
    }

    private void setUpGUI() {
        frame = new JFrame("AnimationTest");
        canvas = new Canvas(400, 400);
        canvas.setFPS(15);
        canvas.setBackgroundColor(Color.WHITE);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sprite = new Sprite(20, 20, Color.BLACK);
        canvas.draw(sprite, 200, 200);
    }
}
