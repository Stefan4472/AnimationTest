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

    // frames since alien was constructed
    // used for calculating trajectory
    private int elapsedFrames;

    // starting y-coordinate
    // used as a reference for calculating trajectory
    private double startingY;

    // defines sine wave that describes alien's trajectory
    private int amplitude;
    private int period;
    private int vShift;
    private int hShift;

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
        bulletSpeed = -2.0f - random.nextInt(10) / 10;
        speedX = -2.0f;
        projectiles = new ArrayList<>();

        startingY = y;
        elapsedFrames = 1; // avoid divide by zero
        amplitude = 70 + random.nextInt(60);
        period = 250 + random.nextInt(100);
        vShift = random.nextInt(20);
        hShift = -random.nextInt(3);
        System.out.println("Starting coordinates " + x + "," + y);
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
        double projected_y;
        // if sprite in top half of screen, start flying down. Else start flying up
        if(startingY <= 150) {
            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        } else { // todo: flying up
            projected_y = amplitude * Math.sin(2 * Math.PI / period * (elapsedFrames + hShift)) + startingY + vShift;
        }
        speedY = projected_y - y;
        elapsedFrames++;
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
        projectiles.add(b);
    }
}
