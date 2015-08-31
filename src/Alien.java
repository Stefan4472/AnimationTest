import java.util.ArrayList;

/**
 * Created by Stefan on 8/28/2015.
 */
public class Alien extends Sprite {

    // ms to wait between firing bullets
    private final int BULLET_DELAY = 2000;
    private long lastFiredBullet;

    private float bulletSpeed;
    private ArrayList<Sprite> projectiles;

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

    public Alien(String imageName, int x, int y) {
        super(imageName, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        lastFiredBullet = 0;
        bulletSpeed = -1.0f;
        speedX = -2.0f;
        speedY = 0.0f;
        projectiles = new ArrayList<>();
    }

    @Override
    public void updateCurrentImage() {

    }

    @Override
    public void updateActions() {
        if(distanceTo(board.getSpaceship()) < 400 &&
                lastFiredBullet + BULLET_DELAY <= System.currentTimeMillis()) {
            if(getP(0.2f)) {
                System.out.println("Distance to spaceship is " + distanceTo(board.getSpaceship()));
                fireBullet(board.getSpaceship());
                lastFiredBullet = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void handleCollision(Sprite s) {
        if(!(s instanceof AlienBullet))
            vis = false;
    }

    // fires bullet at sprite based on current trajectories
    // that are slightly randomized
    private void fireBullet(Sprite s) {
        AlienBullet b = new AlienBullet(x, y + 20);
        b.setSpeedX(bulletSpeed);
        b.setSpeedY((y - s.getY()) / ((x - s.getX() / bulletSpeed)));
        System.out.println("Bullet fired at " + b.getX() + "," + b.getY() +
            " with speed " + b.getSpeedX() + "," + b.getSpeedY());
        projectiles.add(b);
    }
}
