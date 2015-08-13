import java.awt.*;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Sprite {

    private Shape sprite;

    private Color color;

    private int width;

    private int height;

    public Sprite(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public Shape getShape(int w, int h) {
        return new Ellipse2D.Float(w - 10, h - 10, width, height);
    }

    public Color getColor() { return color; }
}
