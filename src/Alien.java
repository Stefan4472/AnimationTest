import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Alien extends Sprite {

    // ms to wait between firing bullets
    private final int BULLET_DELAY = 2000;
    private long lastFiredBullet;

    private double bulletSpeed;
    private ArrayList<Sprite> projectiles;

    private int amplitude;
    private int period;

    public Sprite getProjectile() {
        return projectiles.get(0);
    }

    public ArrayList<Sprite> getProjectiles() {
        return projectiles;
    }

    public Alien(String imageName) {
        super(imageName);
        initObstacle();
    }

    public Alien(String imageName, double x, double y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        lastFiredBullet = 0;
        bulletSpeed = -2.0f;
        speedX = -2.0f;
        projectiles = new ArrayList<>();

        amplitude = 100;
        period = 600;
    }

    @Override
    public void updateCurrentImage() {

    }

    @Override
    public void updateActions() {
        if(distanceTo(board.getSpaceship()) < 400 &&
                lastFiredBullet + BULLET_DELAY <= System.currentTimeMillis()) {
            if(getP(0.2f)) {
                fireBullet(board.getSpaceship());
                lastFiredBullet = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void updateSpeeds() {
        //float projected_y = (float) (amplitude * Math.sin(2 * Math.PI / period * x));
       // System.out.println("Projected y: " + projected_y);
       // speedY = projected_y - y;
    }

    @Override
    public void handleCollision(Sprite s) {
        if(!(s instanceof AlienBullet))
            vis = false;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    private void fireBullet(Sprite s) {
        Point2D.Double target = s.getHitboxCenter();
        AlienBullet b = new AlienBullet(x, y + 20);
        b.setSpeedX(bulletSpeed);
        double frames_to_impact = (x - s.x) / bulletSpeed;
        b.setSpeedY((y - target.y) / frames_to_impact);
        System.out.println("Firing bullet from " + x + "," + y + " at " + s.getX() + "," + s.getY() +
            " with speed " + b.speedX + "," + b.speedY);
        projectiles.add(b);
    }
}
